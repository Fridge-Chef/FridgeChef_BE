### 젠킨스 CI/CD 자동화 파이프라인 내용

    pipeline {
    agent any

    environment {
        JASYPT_PASSWORD = '' # 암호값
        DISCORD_WEBHOOK_URL = 'https://discord.com/api/webhooks/' # 비말값 주섯처리 
        BACKUP_DIR = '/home/ubuntu/dev/backup' #백업파일 위치 
        REMOTE_USER = 'ubuntu'
        
        # DB TNS 접근일 경우 사용  
        ZIP_BASE64_WALLET_FRIDGE_CHEF = "${env.Wallet_fridgeChef}"     
        ZIP_BASE64_WALLET_FRIDGE_CHEF_DEV = "${env.Wallet_fridgeChefDev}"
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'prod'], description: 'Choose the environment to deploy to')
    }

    stages {
        stage('Setup Environment') {
            steps {
                script {
                    if (params.ENVIRONMENT == 'dev') {
                        env.SSH_KEY = '/var/jenkins_home/ssh_fridge_dev.key'
                        env.REMOTE_HOST = '' # 개발서버 IP 
                    } else if (params.ENVIRONMENT == 'prod') {
                        env.SSH_KEY = '/var/jenkins_home/ssh_fridge.key'
                        env.REMOTE_HOST = '' # 운영 서버 IP 
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    def branchName = params.ENVIRONMENT == 'dev' ? 'develop' : 'main'
                    git branch: branchName, url: 'https://github.com/Fridge-Chef/FridgeChef_BE.git'
                }
            }
        }

        stage('Setup file') {
            steps {
                script {
                    // Base64 문자열을 파일로 복원
                    writeFile file: 'Wallet_fridgeChef.zip', text: ZIP_BASE64_WALLET_FRIDGE_CHEF, encoding: 'Base64'
                    writeFile file: 'Wallet_fridgeChefDev.zip', text: ZIP_BASE64_WALLET_FRIDGE_CHEF_DEV, encoding: 'Base64'

                    // 압축 해제
                    sh 'unzip -o Wallet_fridgeChef.zip -d src/main/resources/'
                    sh 'unzip -o Wallet_fridgeChefDev.zip -d src/main/resources/Wallet_fridgeChefDev'
                }
            }
        }

        stage('Test build') {
            steps {
                sh "./gradlew openapi3 -Dspring.profiles.active=${params.ENVIRONMENT}"
                sh 'redoc-cli bundle ./build/api-spec/openapi3.yaml ./src/main/resources/static/my-api-docs.html'
                sh "./gradlew bootjar -Dspring.profiles.active=${params.ENVIRONMENT}"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def date = new Date().format('yyyyMMdd-HHmmss')
                    env.TARGET_JAR = sh(script: 'find build/libs -name "*.jar" -print -quit', returnStdout: true).trim()

                    if (!env.TARGET_JAR) {
                        error "JAR file not found in build/libs directory"
                    }
                    echo "TARGET_JAR is set to ${env.TARGET_JAR}"
                    
                    sh """
                        echo "Backing up old JAR file on remote server..."
                        ssh -i ${env.SSH_KEY} ${env.REMOTE_USER}@${env.REMOTE_HOST} "mkdir -p ${env.BACKUP_DIR}"
                        ssh -i ${env.SSH_KEY} ${env.REMOTE_USER}@${env.REMOTE_HOST} "if [ -f /home/ubuntu/dev/server.jar ]; then mv /home/ubuntu/dev/server.jar ${env.BACKUP_DIR}/server-${date}.jar; fi"
                    """
                    sh """
                        echo "Deploying new JAR file to remote server..."
                        scp -i ${env.SSH_KEY} ${env.TARGET_JAR} ${env.REMOTE_USER}@${env.REMOTE_HOST}:/home/ubuntu/dev/server.jar
                    """
                    sh """
                        echo "Restarting the service on remote server..."
                        ssh -i ${env.SSH_KEY} ${env.REMOTE_USER}@${env.REMOTE_HOST} "sudo systemctl restart boot_server.service"
                    """
                }
            }
        }
    }

    post {
        success {
            script {
                def now = new Date().format('MM/dd HH:mm:ss')
                def message = "[${params.ENVIRONMENT}]서버: 성공 [${now}]"
                def payload = [content: message]
                sh """
                curl -X POST -H "Content-Type: application/json" -d '${groovy.json.JsonOutput.toJson(payload)}' ${env.DISCORD_WEBHOOK_URL}
                """
            }
        }
        failure {
            script {
                def now = new Date().format('MM/dd HH:mm:ss')
                def failedStage = currentBuild.currentResult == 'FAILURE' ? '빌드 실패' : '배포 실패'
                def message = "[${params.ENVIRONMENT}]서버: 실패 [${now}] - ${failedStage}"
                def payload = [content: message]
                sh """
                curl -X POST -H "Content-Type: application/json" -d '${groovy.json.JsonOutput.toJson(payload)}' ${env.DISCORD_WEBHOOK_URL}
                """
            }
        }
    }
    }



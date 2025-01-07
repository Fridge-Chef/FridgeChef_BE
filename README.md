## fridge chef - 나만의 냉장고 셰프

---

## 프로젝트 개요
### 팀명 : fridge chef

- 제작 기간 : 2024.08.22 ~ 2024.(개발중)

## 소개
### 냉장고 안에 재료로 레시피를 만들 수 있어요

- 안쓰는 재료로 만들 수 있는 추천 레시피 제공
- 나만의 레시피를 만들어 사람들 과 공유
- 사용자 편의성을 위주로 개발

![메인 이미지](docs/intro.png)


### 팀원 소개

| [강정훈](https://github.com/JHKoder) | [신민기](https://github.com/ABCganada) | [김민영](https://github.com/alsendrha) | 이서인  |
|:-----------------------:|:-----------------------------------:|:-----------------------------------:|:----:|
| <img src="https://avatars.githubusercontent.com/u/105915960?v=4" width="180"/> | <img src="https://avatars.githubusercontent.com/u/96655921?s=96&v=4" width="180"/> |<img src="https://avatars.githubusercontent.com/u/95726561?s=96&v=4" width="180"/> |         |
| 백엔드  |  백엔드 |  앱 |  디자이너   |

# 백엔드 사용 기술 및 아키텍처

## 빌드 및 실행 방법

1. 프로젝트 클론
   ```bash
   git clone https://github.com/Fridge-Chef/FridgeChef_BE.git
   ```
2. Gradle로 빌드
   ```bash
   ./gradlew build
   ```
3. 실행
   ```bash
   ./gradlew bootRun
   ```

#### active 실행 조건
- local
    - 어느 곳 에서나 실행 가능
- dev
    - Jasypt password 암호 필요
    - resources/private 폴더 내 priv,pub,config,pem 파일 필요
- prod
    - db 연결 용 tns 파일

## 사용 기술 & 환경

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.3
- **ORM**: JPA, QueryDsl
- **테스트**: REST Docs & Junit
- **CI/CD**: Git Actions
- **서버**: Oracle Cloud
    - cpu 3, memory 16
- **이미지 저장소**: Oracle Cloud Object Storage
- **DB**:
    - prod :Oracle Cloud oracle 19c
    - local,dev : h2
- **DNS**: cloudflare dns

## REST-DOC & Redocly API 문서

[API DOCS](https://fridgebe.site/docs.html)

## 커밋 규칙

- 브랜치명 : feat,fix,refactor,bug,test
- 메세지 :브랜치 명 or gitmoji 방식

## ERD

![erd](docs/db_erd6.png)

# 기여도

|     도메인     | 강정훈(기여도) | 신민기(기여도) |
|:-----------:|:--------:|:--------:|
|     유저      |   100%   |    -     |
|     재료      |   40%    |   60%    |
|     냉장고     |   40%    |   60%    |
|     레시피     |   90%    |   10%    |
|     게시판     |   100%   |    -     |
|     댓글      |   100%   |    -     |
| 게시글, 댓글 이벤트 |   100%   |    -     |
|     이미지     |   100%   |    -     |
| 서버 빌드 및 배포  |   100%   |    -     |
|             |          |          |
| 프로젝트 총 기여도  |   82%    |   18%    |

## 서비스
### 유저
- 로그인/회원가입 기능
- 회원 탈퇴 기능
- 이름 변경 기능
- 프로필 변경 기능
- 회원탈퇴후 1주일 동안 정보 보관 후 삭제 (00시 기준) 이후 복구 불가
### 재료 & 냉장고
- 재료 검색 기능
- 재료 추가,수정,삭제 기능
- 유통기한,카테고리 설정 기능
### 레시피&게시판
- 필수,선택 재료로 레시피 검색
- 상세보기,수정,삭제 기능
### 댓글
- 추가,조회,수정,삭제 기능
- 좋아요 기능
### 게시글,댓글 이벤트
- 조회수,찜하기,별점 평균 구별
- 레시피 북에서 이벤트 토대로 조회
### 이미지
- 이미지 Object Storage 저장 기능

## 아키텍처

### 서버 CI/CD

![arch](docs/architecture.png)

### Oauth2

![user](docs/user.png)

### Oracle cloud Object Storage ( 이미지 저장소 )

![image](docs/image.png)

### Test

![test](docs/test.png)




### 전체 유스케이스

![i](docs/img.png)


## 백엔드 비용

---

도메인 구매 비용 : 1만원 (강정훈 구입)<p>
AI 레시피 이미지 생성 비용 : 1.5만원 (강정훈 구입)<p>
서버 운영비: 0원






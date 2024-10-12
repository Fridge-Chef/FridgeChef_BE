import os
import re
import uuid
import yaml

# snippets 및 openapi 경로 설정
SNIPPETS_DIR = 'build/generated-snippets'
OPENAPI_FILE = 'build/api-spec/openapi3.yaml'

def find_snippet_directories(base_dir):
    """build/generated-snippets 디렉토리에서 request-parts.adoc이 있는 폴더를 찾음"""
    for root, dirs, files in os.walk(base_dir):
        if 'request-parts.adoc' in files:
            yield root

def extract_curl_data(curl_file):
    """curl-request.adoc 파일에서 -F로 시작하는 부분을 추출 및 경로와 메서드 추출"""
    with open(curl_file, 'r', encoding='utf-8') as file:
        content = file.read()

    # 경로 및 메서드 추출 (첫 번째 줄에서 경로와 메서드 추출)
    path_match = re.search(r"curl 'http://localhost:8080(/[\w/]+)' -i -X (\w+)", content)
    if path_match:
        api_path = path_match.group(1)
        method = path_match.group(2).lower()  # 메서드를 소문자로 처리
    else:
        return None, None

    return api_path, method

def extract_http_data(http_file):
    """http-request.adoc 파일에서 JSON 데이터를 추출"""
    json_data = {}
    with open(http_file, 'r', encoding='utf-8') as file:
        content = file.read()

    # JSON 데이터를 포함한 부분 찾기
    # 'name'을 동적으로 추출하도록 변경
    json_match = re.search(r'Content-Disposition: form-data; name=(\w+); filename=(.*)\nContent-Type: application/json\n\n(.*?)(?=\n--)', content, re.DOTALL)
    if json_match:
        name = json_match.group(1)  # 'name' 속성 추출
        json_content = json_match.group(3).strip()  # JSON 내용 추출
        json_data[name] = json_content  # 동적으로 name에 맞춰 데이터 추가

    return json_data

def extract_image_data(http_file):
    """http-request.adoc 파일에서 이미지 데이터 추출 (불필요한 부분은 무시)"""
    image_files = []
    with open(http_file, 'r', encoding='utf-8') as file:
        content = file.read()

    # 이미지 파일 목록 찾기
    image_matches = re.findall(r'Content-Disposition: form-data; name=images; filename=(.*)', content)
    for match in image_matches:
        image_files.append(match.strip())

    return image_files

def generate_random_uuid():
    """랜덤 UUID 생성"""
    return str(uuid.uuid4())

def load_openapi_spec(file_path):
    """openapi3.yaml 파일 읽기"""
    with open(file_path, 'r', encoding='utf-8') as file:
        return yaml.safe_load(file)

def save_openapi_spec(file_path, spec):
    """openapi3.yaml 파일 저장"""
    with open(file_path, 'w', encoding='utf-8') as file:
        yaml.dump(spec, file, allow_unicode=True)

def find_operation_id(api_path, method, openapi_spec):
    """openapi3.yaml 파일에서 경로와 메서드에 맞는 operationId 값을 찾아 반환"""
    if api_path in openapi_spec['paths'] and method in openapi_spec['paths'][api_path]:
        return openapi_spec['paths'][api_path][method].get('operationId')
    return None

def update_openapi_yaml(api_path, method, operation_id, openapi_spec, http_file):
    """openapi3.yaml 파일 업데이트"""
    random_uuid = generate_random_uuid()

    # 해당 경로와 메서드가 있는지 확인하고 없으면 추가
    if api_path not in openapi_spec['paths']:
        openapi_spec['paths'][api_path] = {}
    if method not in openapi_spec['paths'][api_path]:
        openapi_spec['paths'][api_path][method] = {}

    # JSON 데이터 추출
    json_data = extract_http_data(http_file)

    # 요청 바디의 새로운 스키마 추가
    openapi_spec['components']['schemas'][f'api-board-{random_uuid}'] = {
        'type': 'object',
        'properties': {}
    }

    # JSON 데이터에서 properties 동적으로 생성
    for key, value in json_data.items():
        if isinstance(value, list):
            # 리스트인 경우
            openapi_spec['components']['schemas'][f'api-board-{random_uuid}']['properties'][key] = {
                'type': 'array',
                'items': {
                    'type': 'object',
                    'properties': {}
                },
                'description': f'{key} 목록'  # 배열 설명 추가
            }

            # 리스트 아이템의 구조 분석
            if value:  # 리스트가 비어있지 않을 경우
                for item in value:
                    if isinstance(item, dict):
                        for item_key, item_value in item.items():
                            openapi_spec['components']['schemas'][f'api-board-{random_uuid}']['properties'][key]['items']['properties'][item_key] = {
                                'type': type(item_value).__name__,  # 타입 추출
                                'description': f'{item_key}에 대한 설명'  # 설명 추가
                            }

        elif isinstance(value, dict):
            # 객체인 경우
            openapi_spec['components']['schemas'][f'api-board-{random_uuid}']['properties'][key] = {
                'type': 'object',
                'properties': {}
            }
            for sub_key, sub_value in value.items():
                openapi_spec['components']['schemas'][f'api-board-{random_uuid}']['properties'][key]['properties'][sub_key] = {
                    'type': type(sub_value).__name__,  # 타입 추출
                    'description': f'{sub_key}에 대한 설명'  # 설명 추가
                }

        else:
            # 기본 타입인 경우
            openapi_spec['components']['schemas'][f'api-board-{random_uuid}']['properties'][key] = {
                'type': type(value).__name__,  # 타입 추출
                'description': f'{key}에 대한 설명'  # 설명 추가
            }

    # operationId 및 requestBody 등의 정보 추가
    request_body = {
        'content': {
            'application/x-www-form-urlencoded;charset=UTF-8': {
                'schema': {
                    '$ref': f'#/components/schemas/api-board-{random_uuid}'
                },
                'examples': {}
            }
        }
    }

    # JSON 데이터 추가
    if json_data:
        request_body['content']['application/x-www-form-urlencoded;charset=UTF-8']['examples'][operation_id] = {
            'value': json_data[next(iter(json_data))] + "\n"  # 동적으로 JSON 키 사용
        }

    # 이미지 파일 추가 (이름만)
    image_files = extract_image_data(http_file)
    if image_files:
        request_body['content']['application/x-www-form-urlencoded;charset=UTF-8']['examples'][operation_id]['value'] += "\n".join([f"-F 'images=@{img};type=image/png' \\" for img in image_files])

    # 업데이트하기
    openapi_spec['paths'][api_path][method].update({
        'operationId': operation_id,
        'requestBody': request_body
    })
def update_curl_request(curl_file, json_data, image_files):
    """curl-request.adoc 파일 업데이트"""
    with open(curl_file, 'r', encoding='utf-8') as file:
        content = file.read()

    # JSON 데이터의 첫 번째 키 추출
    json_key = next(iter(json_data))
    json_value = json_data[json_key]

    # curl 요청 업데이트
    updated_curl = re.sub(
        r"(-F '.*?=@.*?\.json;type=application/json'\s*\\\n)(.*)(?=\n----)",
        f"-F '{json_key}={json_value};type=application/json' \\\n",
        content
    )

    # 이미지 요청 추가
    if image_files:
        updated_curl += "\n" + "\n".join([f"-F 'images=@{img};type=image/png' \\" for img in image_files]) + "\n"

    # 변경된 내용을 파일에 다시 저장
    with open(curl_file, 'w', encoding='utf-8') as file:
        file.write(updated_curl)

def main():
    openapi_spec = load_openapi_spec(OPENAPI_FILE)

    for folder in find_snippet_directories(SNIPPETS_DIR):
        curl_file = os.path.join(folder, 'curl-request.adoc')
        http_file = os.path.join(folder, 'http-request.adoc')

        # curl-request.adoc에서 경로, 메서드, -F로 시작하는 데이터 추출
        api_path, method = extract_curl_data(curl_file)

        if api_path and method:
            # openapi3.yaml에서 operationId 값을 찾아서 사용
            operation_id = find_operation_id(api_path, method, openapi_spec)

            if operation_id:  # operationId가 존재하는 경우에만 진행
                # openapi3.yaml 업데이트
                update_openapi_yaml(api_path, method, operation_id, openapi_spec, http_file)

                # curl-request.adoc 업데이트
                json_data = extract_http_data(http_file)
                image_files = extract_image_data(http_file)
                update_curl_request(curl_file, json_data, image_files)

    # 업데이트된 OpenAPI YAML 파일 저장
    save_openapi_spec(OPENAPI_FILE, openapi_spec)

if __name__ == '__main__':
    main()

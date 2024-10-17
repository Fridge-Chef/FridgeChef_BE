import os
import re
import yaml
import uuid

# 경로 설정
adoc_path = 'build/generated-snippets/'
openapi_path = 'build/api-spec/openapi3.yaml'

# 원하는 순서
desired_order = [
    'openapi',
    'info',
    'servers',
    'tags',
    'paths',
    'components'
]

def load_yaml(file_path):
    """Load YAML file."""
    with open(file_path, 'r', encoding='utf-8') as file:
        return yaml.safe_load(file)

def save_yaml(file_path, data):
    """Save YAML file."""
    with open(file_path, 'w', encoding='utf-8') as file:
        yaml.dump(data, file, sort_keys=False)

def sort_yaml_data(yaml_data):
    """Sort YAML data based on desired order."""
    sorted_data = {}
    for key in desired_order:
        if key in yaml_data:
            sorted_data[key] = yaml_data[key]
    return sorted_data

# 함수: 파일명에 '-'가 포함된 것은 제외하고 adoc 파일들을 읽어들이기
# 해당 폴더에 'request-parts.adoc' 파일이 없는 폴더는 제외
def get_valid_adoc_folders(base_path):
    valid_folders = []
    for root, dirs, files in os.walk(base_path):
        # 폴더명에 '-'가 포함되지 않은지 확인
        folder_name = os.path.basename(root)
        if '-' not in folder_name:
            # 'request-parts.adoc' 파일이 있는지 확인
            if 'request-parts.adoc' in files:
                valid_folders.append([os.path.join(root, file) for file in files if file.endswith('.adoc')])
    return valid_folders



def extract_curl_request(file_content):
    match = re.search(r"curl '([^']+)' -i -X (\w+)", file_content)
    if match:
        full_uri, method = match.groups()
        # 'http://localhost:8080'을 제거하고 URI의 경로 부분만 추출
        uri = re.sub(r'^https?://[^/]+', '', full_uri)
        return uri, method
    return None, None
# 함수: request-parts.adoc에서 part와 description 추출
def extract_request_parts(file_content):
    parts = re.findall(r"\|`(.+?)`\s*\|\s*(.+)", file_content)
    return [{'part': part.strip(), 'description': desc.strip()} for part, desc in parts]


# 함수: openapi.yaml을 업데이트
def update_openapi_yaml(uri, method, parts):
    # YAML 파일 읽기
    with open(openapi_path, 'r', encoding='utf-8') as f:
        openapi_data = yaml.safe_load(f)

    # uuid 생성 및 기본 구조 생성
    operation_id = str(uuid.uuid4())
    schema_ref = f"api-{operation_id}"

    # paths에 추가
    if 'paths' not in openapi_data:
        openapi_data['paths'] = {}

    if uri not in openapi_data['paths']:
        openapi_data['paths'][uri] = {}

    if method.lower() not in openapi_data['paths'][uri]:
        openapi_data['paths'][uri][method.lower()] = {}

    # requestBody 작성
    openapi_data['paths'][uri][method.lower()]['requestBody'] = {
        'content': {
            'application/form-data': {
                'schema': {
                    '$ref': f"#/components/schemas/{schema_ref}"
                }

            }
        }
    }

    # components.schemas에 추가
    if 'components' not in openapi_data:
        openapi_data['components'] = {}
    if 'schemas' not in openapi_data['components']:
        openapi_data['components']['schemas'] = {}

    # 스키마 작성
    schema_structure = {
        'type': 'object',
        'properties': {}
    }


    for part in parts:
        # '+' 제거
        cleaned_part = part['part'].replace('+', '')  # '+' 플러스 문자를 제거

        # 괄호와 숫자 처리
        match = re.search(r'(\w+)\[(\d+)\]\.(\w+)', cleaned_part)  # field[0].name 형식 체크
        if match:
            field_name = match.group(1) + '.' + match.group(3)  # field.name 형식으로 변환
            number = int(match.group(2))  # 괄호 안의 숫자 추출

            if number >= 1:
                continue  # 숫자가 1 이상이면 건너뛰기
        else:
            clean_part = re.sub(r'[\[\]\(\)]', '', cleaned_part)  # 괄호 및 내부 숫자 제거
            field_name = clean_part  # 괄호 제거 후 field_name 설정

        # properties에 추가
        schema_structure['properties'][field_name] = {
            'type': 'string',  # 기본값 string, 필요한 경우 확장
            'description': part['description']
        }

    openapi_data['components']['schemas'][schema_ref] = schema_structure

    # YAML 파일에 다시 저장
    with open(openapi_path, 'w', encoding='utf-8') as f:
        yaml.dump(openapi_data, f, sort_keys=False)
# 함수: 깨진 유니코드 문자열을 한글로 변환
def decode_broken_string(broken_string):
    # 잘못 인코딩된 문자열을 바이트로 변환 후 UTF-8로 디코딩
    try:
        return bytes(broken_string, 'utf-8').decode('unicode_escape')
    except Exception as e:
        return broken_string  # 변환 실패 시 원본 문자열 반환

# 함수: YAML 파일을 읽고 문자열을 변환
def process_yaml_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        data = yaml.safe_load(f)

    def recursive_decode(data):
        if isinstance(data, dict):
            return {key: recursive_decode(value) for key, value in data.items()}
        elif isinstance(data, list):
            return [recursive_decode(item) for item in data]
        elif isinstance(data, str):
            # 깨진 문자열 처리
            if re.search(r'\\x[0-9A-Fa-f]{2}', data):  # \x로 시작하는 2자리 16진수 확인
                return decode_broken_string(data)
            return data
        return data

    # 변환된 데이터
    decoded_data = recursive_decode(data)

    # YAML 파일에 다시 저장
    with open(file_path, 'w', encoding='utf-8') as f:
        yaml.dump(decoded_data, f, sort_keys=False, allow_unicode=True)


# 메인 실행 함수
def main():
    valid_folders = get_valid_adoc_folders(adoc_path)
    for folder_files in valid_folders:
        uri, method, parts = None, None, None
        for adoc_file in folder_files:
            with open(adoc_file, 'r', encoding='utf-8') as f:
                content = f.read()

                # curl-request.adoc 처리
                if 'curl-request' in adoc_file:
                    uri, method = extract_curl_request(content)

                # request-parts.adoc 처리
                if 'request-parts' in adoc_file:
                    parts = extract_request_parts(content)

        # 추출된 데이터를 기반으로 OpenAPI YAML 파일 업데이트
        if uri and method and parts:
            update_openapi_yaml(uri, method, parts)

    process_yaml_file(openapi_path)

    # YAML 데이터 로드
    yaml_data = load_yaml(openapi_path)

    # YAML 데이터 정렬
    sorted_data = sort_yaml_data(yaml_data)

    # 정렬된 데이터 저장
    save_yaml(openapi_path, sorted_data)

if __name__ == "__main__":
    main()

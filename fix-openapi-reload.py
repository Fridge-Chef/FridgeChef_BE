import yaml
import urllib.parse

# 경로 설정
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
        yaml.dump(data, file, sort_keys=False, allow_unicode=True)

def sort_yaml_data(yaml_data):
    """Sort YAML data based on desired order."""
    sorted_data = {}
    for key in desired_order:
        if key in yaml_data:
            sorted_data[key] = yaml_data[key]
    return sorted_data

# 메인 실행 함수
def main():
    # YAML 데이터 로드
    yaml_data = load_yaml(openapi_path)

    # YAML 데이터 정렬
    sorted_data = sort_yaml_data(yaml_data)

    # 정렬된 데이터 저장
    save_yaml(openapi_path, sorted_data)

if __name__ == "__main__":
    main()

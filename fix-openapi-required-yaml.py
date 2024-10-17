import os
import json
import yaml
import urllib.parse

def load_json(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return json.load(file)

def load_yaml(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return yaml.safe_load(file)

def save_yaml(data, file_path):
    with open(file_path, 'w', encoding='utf-8') as file:
        yaml.dump(data, file, allow_unicode=True)

def find_optional_true_fields(root_dir):
    optional_fields = {}
    for dirpath, dirnames, filenames in os.walk(root_dir):
        if '-' not in os.path.basename(dirpath):
            if "resource.json" in filenames:
                resource_path = os.path.join(dirpath, "resource.json")
                resource_data = load_json(resource_path)
                for field in resource_data['request']['requestFields']:
                    if field.get('optional') is True:
                        path = resource_data['request']['path']
                        if path not in optional_fields:
                            optional_fields[path] = []
                        optional_fields[path].append(field['path'])
    return optional_fields

def update_required_fields(yaml_data, optional_fields):
    for path, fields in optional_fields.items():
        if path in yaml_data['paths']:
            for method in ['get', 'post', 'patch', 'delete', 'put']:
                method_data = yaml_data['paths'][path].get(method)
                if method_data:
                    schema_ref = method_data['requestBody']['content']['application/json;charset=UTF-8']['schema']['$ref']
                    schema_key = schema_ref.split('/')[-1]

                    schema = yaml_data['components']['schemas'].get(schema_key)
                    if schema:
                        schema['required'] = []
                        schema['required'].extend(fields)
                        schema['required'] = list(set(schema['required']))

def decode_operation_ids(yaml_data):
    """Decode operationId if it is URL encoded"""
    for path_data in yaml_data['paths'].values():
        for method_data in path_data.values():
            if 'operationId' in method_data:
                try:
                    decoded_id = urllib.parse.unquote(method_data['operationId'])
                    method_data['operationId'] = decoded_id
                except Exception as e:
                    print(f"Error decoding operationId: {e}")

def main():
    root_dir = "build/generated-snippets/"
    yaml_file_path = "build/api-spec/openapi3.yaml"

    # Step 1: Find all optional fields
    optional_fields = find_optional_true_fields(root_dir)

    # Step 2: Load YAML file and update required fields
    yaml_data = load_yaml(yaml_file_path)
    update_required_fields(yaml_data, optional_fields)

    # Step 3: Decode operationId to make sure it's in Korean (if applicable)
    decode_operation_ids(yaml_data)

    # Step 4: Save the updated YAML
    save_yaml(yaml_data, yaml_file_path)

if __name__ == "__main__":
    main()

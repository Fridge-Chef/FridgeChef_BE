#!/usr/bin/env python3
#$ python3 ./fix-openapi-yaml.py build/api-spec/openapi3.yaml >openapi-fixed.yaml && redoc-cli bundle openapi-fixed.yaml

import sys
import yaml
import json

def fix_examples(res: dict):
    for key, value in res.items():
        if isinstance(value, dict):
            fix_examples(value)
        if key in ["application/json", "application/json;charset=UTF-8"]:
            for example_name, content in value.get("examples", {}).items():
                try:
                    content["value"] = json.loads(content["value"])  # JSON 문자열을 파싱
                except:
                    pass

with open(sys.argv[1], "r") as api_file:
    res = yaml.safe_load(api_file)
    fix_examples(res)
    print(yaml.dump(res))

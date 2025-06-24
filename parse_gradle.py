import re
import json
import sys

def parse_gradle_kts(file_path):
    try:
        with open(file_path, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"Error: File not found {file_path}", file=sys.stderr)
        return {"file": file_path, "properties": {}, "dependencies": []}
    except Exception as e:
        print(f"Error reading file {file_path}: {e}", file=sys.stderr)
        return {"file": file_path, "properties": {}, "dependencies": []}

    dependencies = []
    properties = {}

    # Regex to find dependencies (implementation, api, compileOnly, etc.)
    # This regex tries to capture different forms of dependency declarations:
    # 1. "group:name:version"
    # 2. group = "group", name = "name", version = "version"
    # 3. project(":module-name")
    # 4. platform("group:name:version")
    # It also captures the configuration (implementation, api, etc.)
    dependency_pattern = re.compile(
        r'^\s*(?P<config>[\w.-]+)\s*'  # Configuration (e.g., implementation, api)
        r'(?:\(\s*project\s*\(\s*"(?P<project_name>:[^"]+)"\s*\)\s*\)|'  # project(":module-name")
        r'\(\s*platform\s*\(\s*"(?P<platform_coords>[^"]+)"\s*\)\s*\)|' # platform("group:name:version")
        r'\(\s*"(?P<coords_short>[^"]+)"\s*\)|'  # "group:name:version"
        r'\((?P<coords_map>[^)]+)\)|' # (group = "group", name = "name", version = "version")
        r'\s+"(?P<coords_single_arg>[^"]+)"' # "group:name:version" (single argument after config)
        r')',
        re.MULTILINE
    )

    # Regex for ext block properties like set("key", "value") or set("key", project.ext["value"])
    ext_property_pattern = re.compile(
        r'set\s*\(\s*"(?P<key>[^"]+)"\s*,\s*"(?P<value_simple>[^"]+)"\s*\)|' # set("key", "value")
        r'set\s*\(\s*"(?P<key_ext>[^"]+)"\s*,\s*project\.ext\["(?P<value_ext_ref>[^"]+)"\]\s*\)', # set("key", project.ext["value"])
         re.MULTILINE
    )

    # Regex for simple ext properties like val key = "value" or val key = project.ext["value"]
    val_property_pattern = re.compile(
        r'val\s+(?P<key>[^=\s]+)\s*=\s*"(?P<value_simple>[^"]+)"|' # val key = "value"
        r'val\s+(?P<key_ext_val>[^=\s]+)\s*=\s*project\.ext\["(?P<value_ext_ref_val>[^"]+)"\]', # val key = project.ext["value"]
        re.MULTILINE
    )

    # Regex for ext block properties defined with `ext { key = "value" }`
    ext_block_property_pattern = re.compile(r'ext\s*\{\s*([^}]+)\s*\}', re.DOTALL)
    ext_block_item_pattern = re.compile(r'set\s*\(\s*"(?P<key>[^"]+)"\s*,\s*"(?P<value>[^"]+)"\s*\)|' # set("key", "value")
                                        r'(?P<key_simple>\w+)\s*=\s*"(?P<value_simple>[^"]+)"') # key = "value"


    # Find properties in ext blocks
    for match in ext_property_pattern.finditer(content):
        if match.group("key") and match.group("value_simple"):
            properties[match.group("key")] = match.group("value_simple")
        elif match.group("key_ext") and match.group("value_ext_ref"):
             # Store as a reference, will need to resolve later if needed
            properties[match.group("key_ext")] = f'${{{match.group("value_ext_ref")}}}'

    for match in val_property_pattern.finditer(content):
        if match.group("key") and match.group("value_simple"):
            properties[match.group("key")] = match.group("value_simple")
        elif match.group("key_ext_val") and match.group("value_ext_ref_val"):
            properties[match.group("key_ext_val")] = f'${{{match.group("value_ext_ref_val")}}}'

    ext_block_match = ext_block_property_pattern.search(content)
    if ext_block_match:
        ext_content = ext_block_match.group(1)
        for item_match in ext_block_item_pattern.finditer(ext_content):
            if item_match.group("key") and item_match.group("value"):
                 properties[item_match.group("key")] = item_match.group("value")
            elif item_match.group("key_simple") and item_match.group("value_simple"):
                 properties[item_match.group("key_simple")] = item_match.group("value_simple")


    for match in dependency_pattern.finditer(content):
        group = None
        name = None
        version = None
        config = match.group("config")

        if match.group("project_name"):
            group = "project"
            name = match.group("project_name")
        elif match.group("platform_coords"):
            coords = match.group("platform_coords").split(':')
            if len(coords) >= 2:
                group = coords[0]
                name = coords[1]
            if len(coords) >= 3:
                version = coords[2]
            # Mark as platform dependency
            name = f"platform({name})"
        elif match.group("coords_short"):
            coords = match.group("coords_short").split(':')
            if len(coords) >= 2:
                group = coords[0]
                name = coords[1]
            if len(coords) >= 3:
                version = coords[2]
        elif match.group("coords_single_arg"):
            coords = match.group("coords_single_arg").split(':')
            if len(coords) >= 2:
                group = coords[0]
                name = coords[1]
            if len(coords) >= 3:
                version = coords[2]
        elif match.group("coords_map"):
            map_str = match.group("coords_map")
            # Try to parse group, name, version from map-like string
            group_match = re.search(r'group\s*=\s*"([^"]+)"', map_str)
            name_match = re.search(r'name\s*=\s*"([^"]+)"', map_str)
            version_match = re.search(r'version\s*=\s*"([^"]+)"', map_str)
            if group_match:
                group = group_match.group(1)
            if name_match:
                name = name_match.group(1)
            if version_match:
                version = version_match.group(1)

        if name: # Ensure name is found to consider it a valid dependency entry
            dependencies.append({
                "group": group,
                "name": name,
                "version": version,
                "config": config
            })

    return {"file": file_path, "properties": properties, "dependencies": dependencies}

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python parse_gradle.py <path_to_build.gradle.kts1> [<path_to_build.gradle.kts2> ...]", file=sys.stderr)
        sys.exit(1)

    all_gradle_data = []
    for gradle_file in sys.argv[1:]:
        all_gradle_data.append(parse_gradle_kts(gradle_file))

    print(json.dumps(all_gradle_data, indent=4))

import xml.etree.ElementTree as ET
import json
import sys

def parse_pom(file_path):
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
    except ET.ParseError as e:
        print(f"Error parsing XML in {file_path}: {e}", file=sys.stderr)
        return {"properties": {}, "dependencies": [], "dependency_management": []}

    # Namespace dictionary to handle namespaces in pom.xml
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}

    properties = {}
    # Find properties defined in the pom
    properties_element = root.find('mvn:properties', ns)
    if properties_element is not None:
        for prop in properties_element:
            tag_name = prop.tag.split('}')[-1] # Remove namespace
            properties[tag_name] = prop.text

    dependencies = []
    # Find direct dependencies
    dependencies_element = root.find('mvn:dependencies', ns)
    if dependencies_element is not None:
        for dep in dependencies_element.findall('mvn:dependency', ns):
            group_id_el = dep.find('mvn:groupId', ns)
            artifact_id_el = dep.find('mvn:artifactId', ns)
            version_el = dep.find('mvn:version', ns)
            scope_el = dep.find('mvn:scope', ns)

            group_id = group_id_el.text if group_id_el is not None else None
            artifact_id = artifact_id_el.text if artifact_id_el is not None else None
            version = version_el.text if version_el is not None else None
            scope = scope_el.text if scope_el is not None else None

            if group_id and artifact_id:
                dependencies.append({
                    "groupId": group_id,
                    "artifactId": artifact_id,
                    "version": version,
                    "scope": scope
                })

    dependency_management = []
    # Find dependencies in dependencyManagement
    dm_element = root.find('mvn:dependencyManagement/mvn:dependencies', ns)
    if dm_element is not None:
        for dep in dm_element.findall('mvn:dependency', ns):
            group_id_el = dep.find('mvn:groupId', ns)
            artifact_id_el = dep.find('mvn:artifactId', ns)
            version_el = dep.find('mvn:version', ns)
            scope_el = dep.find('mvn:scope', ns)
            classifier_el = dep.find('mvn:classifier', ns)


            group_id = group_id_el.text if group_id_el is not None else None
            artifact_id = artifact_id_el.text if artifact_id_el is not None else None
            version = version_el.text if version_el is not None else None
            scope = scope_el.text if scope_el is not None else None
            classifier = classifier_el.text if classifier_el is not None else None

            if group_id and artifact_id:
                dependency_management.append({
                    "groupId": group_id,
                    "artifactId": artifact_id,
                    "version": version,
                    "scope": scope,
                    "classifier": classifier
                })

    # Extract parent information (if any) to resolve parent-defined properties later if needed
    parent_info = {}
    parent_element = root.find('mvn:parent', ns)
    if parent_element is not None:
        parent_group_id_el = parent_element.find('mvn:groupId', ns)
        parent_artifact_id_el = parent_element.find('mvn:artifactId', ns)
        parent_version_el = parent_element.find('mvn:version', ns)
        parent_relativePath_el = parent_element.find('mvn:relativePath', ns)
        parent_info = {
            "groupId": parent_group_id_el.text if parent_group_id_el is not None else None,
            "artifactId": parent_artifact_id_el.text if parent_artifact_id_el is not None else None,
            "version": parent_version_el.text if parent_version_el is not None else None,
            "relativePath": parent_relativePath_el.text if parent_relativePath_el is not None else None,
        }


    return {
        "file": file_path,
        "parent": parent_info,
        "properties": properties,
        "dependencies": dependencies,
        "dependency_management": dependency_management
    }

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python parse_pom.py <path_to_pom1.xml> [<path_to_pom2.xml> ...]", file=sys.stderr)
        sys.exit(1)

    all_poms_data = []
    for pom_file in sys.argv[1:]:
        all_poms_data.append(parse_pom(pom_file))

    print(json.dumps(all_poms_data, indent=4))

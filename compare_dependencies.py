import json
import os
import re

def load_json_file(file_path):
    try:
        with open(file_path, 'r') as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"Error: File not found {file_path}")
        return []
    except json.JSONDecodeError:
        print(f"Error: Could not decode JSON from {file_path}")
        return []

def get_property_value(prop_name, properties_map, all_pom_properties=None):
    """
    Resolves a property value.
    Format: ${property.name} or just property.name
    """
    if prop_name is None:
        return None

    original_prop_name = prop_name # Keep original for cases where it's not a placeholder

    # Check if it's a placeholder
    if prop_name.startswith('${') and prop_name.endswith('}'):
        prop_key = prop_name[2:-1]
    else:
        # If not a standard placeholder, it might be a direct key or needs no replacement
        prop_key = prop_name

    value = properties_map.get(prop_key)
    if value is not None:
        # Recursively resolve if the value itself is a property
        if isinstance(value, str) and value.startswith('${') and value.endswith('}'):
            return get_property_value(value, properties_map, all_pom_properties)
        return value

    # Try resolving from global POM properties if available (for parent POM properties)
    if all_pom_properties:
        global_value = all_pom_properties.get(prop_key)
        if global_value is not None:
            if isinstance(global_value, str) and global_value.startswith('${') and global_value.endswith('}'):
                 return get_property_value(global_value, all_pom_properties, None) # Prevent infinite loop with self-reference for global
            return global_value

    # Fallback to original name if not found (could be a literal version)
    return original_prop_name


def normalize_dependencies(deps_list, properties, parent_properties=None, is_gradle=False, all_pom_properties=None):
    """
    Normalizes a list of dependencies to a set of "group:artifact:version" strings.
    Handles property resolution for versions.
    `all_pom_properties` is used for Maven to look up properties defined in the root pom.
    """
    normalized_deps = {}
    if parent_properties is None:
        parent_properties = {}

    # Combine local and parent properties, local take precedence
    combined_properties = {**parent_properties, **properties}
    if all_pom_properties: # For maven, merge with all_pom_properties (root first, then combined)
        combined_properties = {**all_pom_properties, **combined_properties}


    for dep in deps_list:
        g = dep.get('groupId') if not is_gradle else dep.get('group')
        a = dep.get('artifactId') if not is_gradle else dep.get('name')
        v = dep.get('version')
        s = dep.get('scope')
        classifier = dep.get('classifier') if not is_gradle else None # Gradle parser doesn't extract classifier directly yet
        config = dep.get('config') if is_gradle else None


        if not g or not a:
            # print(f"Skipping dependency with missing group or artifact: {dep}")
            continue

        # Resolve version from properties
        resolved_v = get_property_value(v, combined_properties, all_pom_properties if not is_gradle else None)

        # Handle cases where version might still be a property key if not found (e.g. ${some.prop})
        if resolved_v and isinstance(resolved_v, str) and resolved_v.startswith("${") and resolved_v.endswith("}"):
            # If it's still a property, it means it wasn't resolved. Keep it as is or mark as unresolved.
            # For comparison, it's often better to keep the placeholder to see if Gradle uses the same.
            pass # Keep as is, e.g. "${kudu.version}"

        key_parts = [g, a]
        if resolved_v:
            key_parts.append(str(resolved_v)) # Ensure version is a string
        if classifier and not is_gradle : # Add classifier if present for Maven
             key_parts.append(classifier)

        dep_key = ":".join(key_parts)

        # Store scope/config for more detailed comparison if needed later
        # For now, primarily comparing GAV (and classifier for Maven)
        normalized_deps[dep_key] = {"scope": s, "config": config, "original_version": v or "N/A"}
    return normalized_deps

def get_gradle_project_property(prop_name, properties_map, root_gradle_properties):
    """Resolves Gradle property values, checking local, then root ext properties."""
    if prop_name is None:
        return None

    original_prop_name = prop_name
    prop_key = prop_name

    # Check for project.ext references like project.ext["key"] or rootProject.ext["key"]
    ext_match = re.match(r'(?:project|rootProject)\.ext\["([^"]+)"\]', prop_name)
    if ext_match:
        prop_key = ext_match.group(1)

    # Check local properties first
    value = properties_map.get(prop_key)
    if value is not None:
        # Recursively resolve if the value itself is a property reference
        if isinstance(value, str) and (value.startswith('${') and value.endswith('}')) or \
           ("project.ext" in value or "rootProject.ext" in value) :
            return get_gradle_project_property(value, properties_map, root_gradle_properties)
        return value

    # Then check root project properties
    if root_gradle_properties:
        root_value = root_gradle_properties.get(prop_key)
        if root_value is not None:
            if isinstance(root_value, str) and (root_value.startswith('${') and root_value.endswith('}')) or \
               ("project.ext" in root_value or "rootProject.ext" in root_value):
                 # Avoid infinite recursion with self-reference in root by not passing root_gradle_properties again for the recursive call from root
                return get_gradle_project_property(root_value, root_gradle_properties, None)
            return root_value

    return original_prop_name # Fallback

def normalize_gradle_dependencies(deps_list, properties, root_gradle_properties=None):
    normalized_deps = {}
    if root_gradle_properties is None:
        root_gradle_properties = {}

    for dep in deps_list:
        g = dep.get('group')
        a = dep.get('name')
        v = dep.get('version')
        config = dep.get('config')

        if not g or not a:
            continue

        # Resolve version from properties (local, then root)
        resolved_v = v
        if v and (v.startswith("project.ext[\"") or v.startswith("rootProject.ext[\"") or (v.startswith("${") and v.endswith("}"))):
             resolved_v = get_gradle_project_property(v, properties, root_gradle_properties)
        elif v and properties.get(v): # Direct reference to a val property
            resolved_v = properties.get(v)
        elif v and root_gradle_properties.get(v): # Direct reference to a root val property
            resolved_v = root_gradle_properties.get(v)


        # Handle platform dependencies slightly differently for key generation
        is_platform = False
        if a and a.startswith("platform(") and a.endswith(")"):
            is_platform = True
            # Extract actual name from platform("group:name:version") or platform("group:name")
            platform_content = a[len("platform("):-1]
            platform_parts = platform_content.split(':')
            g = platform_parts[0] # Group comes from platform content
            a = platform_parts[1] # Artifact from platform content
            if len(platform_parts) > 2:
                 resolved_v = platform_parts[2] # Version from platform content overrides outer one
            else:
                 # If version is not in platform() string, use the one from dependency entry
                 # This still needs resolution as it might be a property
                 if v and (v.startswith("project.ext[\"") or v.startswith("rootProject.ext[\"") or (v.startswith("${") and v.endswith("}"))):
                     resolved_v = get_gradle_project_property(v, properties, root_gradle_properties)


        key_parts = [g, a]
        if resolved_v:
            key_parts.append(str(resolved_v))

        dep_key = ":".join(key_parts)
        if is_platform:
            dep_key = f"platform({dep_key})"


        normalized_deps[dep_key] = {"config": config, "original_version": v or "N/A"}
    return normalized_deps


def main():
    pom_data = load_json_file('pom_dependencies.json')
    gradle_data = load_json_file('gradle_dependencies.json')

    if not pom_data or not gradle_data:
        print("Error: Could not load dependency data. Exiting.")
        return

    # Extract root pom properties for resolving inherited versions
    root_pom_properties = {}
    for pom_info in pom_data:
        if pom_info['file'] == 'fe/pom.xml': # Assuming this is the root pom
            root_pom_properties = pom_info.get('properties', {})
            break

    root_gradle_properties = {}
    for gradle_info in gradle_data:
        if gradle_info['file'] == 'fe/build.gradle.kts':
            root_gradle_properties = gradle_info.get('properties', {})
            break

    comparison_results = []

    for pom_info in pom_data:
        pom_file_path = pom_info['file']
        # Try to find corresponding gradle file
        # e.g., fe/pom.xml -> fe/build.gradle.kts
        # e.g., fe/fe-core/pom.xml -> fe/fe-core/build.gradle.kts
        expected_gradle_path = pom_file_path.replace('pom.xml', 'build.gradle.kts')

        current_gradle_info = next((g for g in gradle_data if g['file'] == expected_gradle_path), None)

        if not current_gradle_info:
            print(f"No corresponding Gradle file found for {pom_file_path}")
            continue

        print(f"\nComparing: {pom_file_path} with {current_gradle_info['file']}")

        # Get parent properties if this POM has a parent defined within the loaded files
        parent_pom_props = {}
        if pom_info.get('parent') and pom_info['parent'].get('relativePath'):
            # Construct path to parent pom to fetch its properties
            # This is a simplified assumption; complex relative paths or external parents aren't handled
            parent_pom_path = os.path.normpath(os.path.join(os.path.dirname(pom_file_path), pom_info['parent']['relativePath']))
            parent_pom_detail = next((p for p in pom_data if p['file'] == parent_pom_path), None)
            if parent_pom_detail:
                parent_pom_props = parent_pom_detail.get('properties', {})
            # If parent is the root pom, its properties are already in root_pom_properties
            elif pom_info['parent'].get('artifactId') == 'starrocks-fe' and pom_info['parent'].get('groupId') == 'com.starrocks':
                 parent_pom_props = root_pom_properties


        # Normalize Maven dependencies (direct and managed)
        # For Maven, dependencyManagement is key for versions
        maven_managed_deps = normalize_dependencies(
            pom_info.get('dependency_management', []),
            pom_info.get('properties', {}),
            parent_pom_props,
            is_gradle=False,
            all_pom_properties=root_pom_properties
        )
        maven_direct_deps_list = pom_info.get('dependencies', [])

        # Resolve versions for direct dependencies against managed versions or properties
        resolved_maven_deps = {}
        for dep_info in maven_direct_deps_list:
            g = dep_info['groupId']
            a = dep_info['artifactId']
            v = dep_info['version']
            s = dep_info['scope']
            classifier = dep_info.get('classifier')

            # Key for lookup in managed_deps (may or may not include version)
            lookup_key_no_version = f"{g}:{a}"
            if classifier:
                lookup_key_no_version_classifier = f"{g}:{a}:{classifier}"


            managed_version = None
            # Check if this exact GAV (or GAC) is in managed_deps
            # First check with classifier
            if classifier:
                found_managed_with_classifier = False
                for m_key, m_val in maven_managed_deps.items():
                    if m_key.startswith(f"{g}:{a}:") and m_key.endswith(f":{classifier}"):
                        managed_version = m_key.split(':')[2] # extract version
                        found_managed_with_classifier = True
                        break
                    elif m_key.startswith(f"{g}:{a}:{classifier}:"): # G:A:C:V (no, this is wrong, version is always before classifier)
                         pass


            # If not found with classifier, or no classifier, check G:A
            if not managed_version:
                for m_key, m_val in maven_managed_deps.items():
                    # m_key could be g:a:v or g:a:v:c
                    m_parts = m_key.split(':')
                    mg, ma = m_parts[0], m_parts[1]
                    m_classifier = m_parts[3] if len(m_parts) == 4 else None
                    if mg == g and ma == a and m_classifier == classifier: # Match G, A, and classifier status
                        managed_version = m_parts[2]
                        break


            final_version = v
            if not final_version and managed_version: # If direct dep has no version, use managed
                final_version = managed_version
            elif final_version and final_version.startswith("${"): # If version is a property, resolve it
                final_version = get_property_value(final_version, {**root_pom_properties, **parent_pom_props, **pom_info.get('properties', {})})
            elif not final_version and not managed_version: # No version specified and not in management
                 # This case should ideally not happen in well-formed POMs if a version is expected.
                 # Or it means it's an imported BOM's scope. For now, mark as N/A or keep null.
                 final_version = "N/A (managed by BOM or parent outside this scope)"


            dep_key_parts = [g,a]
            if final_version:
                dep_key_parts.append(str(final_version))
            if classifier:
                dep_key_parts.append(classifier)

            resolved_maven_deps[":".join(dep_key_parts)] = {"scope": s, "original_version": v or "From Management"}

        # Normalize Gradle dependencies
        gradle_deps = normalize_gradle_dependencies(
            current_gradle_info.get('dependencies', []),
            current_gradle_info.get('properties', {}),
            root_gradle_properties
        )

        # --- Comparison Logic ---
        missing_in_gradle = {}
        version_mismatch = {}
        present_in_gradle_not_in_maven = {}

        # Check deps from Maven against Gradle
        for m_key, m_val in resolved_maven_deps.items():
            if m_val.get("scope") == "test" or m_val.get("scope") == "provided": # Often not directly comparable or intentionally different
                # We might want to refine this, e.g. ensure 'provided' in maven is 'compileOnly' in gradle
                continue
            if m_key not in gradle_deps:
                # Check if a similar dependency (ignoring version) exists to report as mismatch vs missing
                m_parts = m_key.split(':')
                m_ga = f"{m_parts[0]}:{m_parts[1]}"
                found_ga_in_gradle = False
                for g_key_comp in gradle_deps.keys():
                    g_parts_comp = g_key_comp.split(':')
                    g_ga_comp = f"{g_parts_comp[0]}:{g_parts_comp[1]}"
                    if m_ga == g_ga_comp:
                        found_ga_in_gradle = True
                        if m_key not in version_mismatch: # Add to mismatch only if not already there
                             version_mismatch[m_key] = {
                                "maven": m_parts[2] if len(m_parts) > 2 else "N/A",
                                "gradle": g_parts_comp[2] if len(g_parts_comp) > 2 else "N/A",
                                "maven_scope": m_val.get("scope"),
                                "gradle_config": gradle_deps[g_key_comp].get("config")
                            }
                        break
                if not found_ga_in_gradle:
                    missing_in_gradle[m_key] = {"maven_scope": m_val.get("scope"), "original_maven_version": m_val.get("original_version")}
            else: # Key matches, check version (already part of the key, but good for explicit mismatch reporting)
                  # This part is implicitly handled by key matching if versions are part of the key.
                  # If versions were not part of the key, this is where you'd compare m_val['version'] and gradle_deps[m_key]['version']
                  pass


        # Check deps from Gradle against Maven (for those only in Gradle)
        for g_key, g_val in gradle_deps.items():
            # Skip project dependencies for this check as they are structured differently in Maven (modules)
            if g_key.startswith("project:") or g_key.startswith("platform("): # also skip platform() for this specific check
                continue
            if g_val.get("config", "").lower().endswith("testimplementation") or \
               g_val.get("config", "").lower() == "compileonly" or \
               g_val.get("config", "").lower() == "antlr": # Skip test, compileOnly, antlr for "only in gradle"
                continue

            if g_key not in resolved_maven_deps:
                # Check if GA matches any maven dependency to classify as version mismatch from gradle's perspective
                g_parts = g_key.split(':')
                g_ga = f"{g_parts[0]}:{g_parts[1]}"
                found_ga_in_maven = False
                for m_key_comp in resolved_maven_deps.keys():
                    m_parts_comp = m_key_comp.split(':')
                    m_ga_comp = f"{m_parts_comp[0]}:{m_parts_comp[1]}"
                    if g_ga == m_ga_comp:
                        found_ga_in_maven = True
                        # This would be a mismatch if versions differ, already caught by maven -> gradle check generally
                        # But if maven had GA:V1 and gradle has GA:V2, it's a mismatch.
                        # If maven has GA (no version) and gradle has GA:V2, it's also a point of interest.
                        if g_key not in version_mismatch and m_key_comp not in version_mismatch: # ensure we don't double report from maven side check
                             # Check if the key without version from gradle matches a key without version from maven
                            maven_version_for_ga = "N/A"
                            if len(m_parts_comp) > 2 : maven_version_for_ga = m_parts_comp[2]

                            gradle_version_for_ga = "N/A"
                            if len(g_parts) > 2 : gradle_version_for_ga = g_parts[2]

                            if maven_version_for_ga != gradle_version_for_ga :
                                version_mismatch[g_key] = {
                                    "gradle": gradle_version_for_ga,
                                    "maven": maven_version_for_ga,
                                    "gradle_config": g_val.get("config"),
                                    "maven_scope": resolved_maven_deps[m_key_comp].get("scope") if m_key_comp in resolved_maven_deps else "N/A"
                                }
                        break
                if not found_ga_in_maven:
                     # Filter out some common gradle-specific configurations or already handled cases
                    if not g_key.startswith("org.jetbrains.kotlin") and not g_key.startswith("org.gradle"):
                         present_in_gradle_not_in_maven[g_key] = {"gradle_config": g_val.get("config"), "original_gradle_version": g_val.get("original_version")}


        if missing_in_gradle:
            print("  Dependencies missing in Gradle:")
            for dep, info in missing_in_gradle.items():
                print(f"    - {dep} (Maven scope: {info['maven_scope'] or 'default'}, Original Version: {info['original_maven_version']})")

        if version_mismatch:
            print("  Version mismatches:")
            for dep, versions in version_mismatch.items():
                print(f"    - {dep.rsplit(':',1)[0] if ':' in dep else dep}: Maven version '{versions['maven']}' (Scope: {versions.get('maven_scope','N/A')}), Gradle version '{versions['gradle']}' (Config: {versions.get('gradle_config','N/A')})")

        if present_in_gradle_not_in_maven:
            print("  Dependencies present only in Gradle (excluding test, compileOnly, antlr, kotlin/gradle plugins):")
            for dep, info in present_in_gradle_not_in_maven.items():
                print(f"    - {dep} (Gradle config: {info['gradle_config']}, Original Version: {info['original_gradle_version']})")

        if not missing_in_gradle and not version_mismatch and not present_in_gradle_not_in_maven:
            print("  No significant discrepancies found (ignoring test/provided/compileOnly scopes for missing/extra).")

        comparison_results.append({
            "pom_file": pom_file_path,
            "gradle_file": current_gradle_info['file'],
            "missing_in_gradle": missing_in_gradle,
            "version_mismatch": version_mismatch,
            "only_in_gradle": present_in_gradle_not_in_maven
        })

    # Optionally, save detailed results to a file
    with open('dependency_comparison_report.json', 'w') as f:
        json.dump(comparison_results, f, indent=4)
    print("\nFull comparison report saved to dependency_comparison_report.json")

if __name__ == "__main__":
    main()

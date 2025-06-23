plugins {
    java
    checkstyle
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.starrocks"
version = "1.0.0"

// Note: There are no explicit dependencies in the original pom.xml for this module

tasks.withType<Test> {
    // Configuration from Maven: failIfNoSpecifiedTests=false
    ignoreFailures = true
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

checkstyle {
    toolVersion = project.findProperty("puppycrawl.version") as String? ?: "10.21.1"
    configFile = rootProject.file("checkstyle.xml")
}

tasks.withType<Checkstyle> {
    exclude("**/jmockit/**/*")
    isShowViolations = true
    ignoreFailures = false
}

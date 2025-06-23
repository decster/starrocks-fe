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

// Property equivalent to fe_ut_parallel in Maven
val feUtParallel = project.findProperty("fe_ut_parallel") ?: "1"

dependencies {
    // StarRocks modules
    implementation(project(":fe-common"))
    implementation(project(":plugin-common"))

    // Regular dependencies
    implementation("com.google.guava:guava")
    implementation("com.google.code.gson:gson")
    implementation("io.netty:netty-handler")
    implementation("org.roaringbitmap:RoaringBitmap")

    // Provided scope dependencies - equivalent to compileOnly in Gradle
    compileOnly("commons-codec:commons-codec")
    compileOnly("org.apache.commons:commons-lang3")
    compileOnly("org.apache.spark:spark-core_2.12")
    compileOnly("org.apache.spark:spark-sql_2.12")
    compileOnly("org.apache.spark:spark-catalyst_2.12")
    compileOnly("org.apache.hadoop:hadoop-common") {
        exclude(group = "io.netty")
    }
    compileOnly("org.apache.parquet:parquet-column")
    compileOnly("org.apache.parquet:parquet-hadoop")
    compileOnly("org.apache.parquet:parquet-common")
    compileOnly("commons-collections:commons-collections")
    compileOnly("org.scala-lang:scala-library")
    compileOnly("com.esotericsoftware:kryo-shaded")
    compileOnly("org.apache.logging.log4j:log4j-slf4j-impl")

    // Test dependencies
    testImplementation("junit:junit")
    testImplementation("com.github.hazendaz.jmockit:jmockit")
}

tasks.withType<Test> {
    // Configure JMockit agent for tests
    jvmArgs("-javaagent:${repositories.mavenLocal().url.path}/com/github/hazendaz/jmockit/jmockit/1.49.4/jmockit-1.49.4.jar")

    // Set for parallel test execution as in the Maven config
    maxParallelForks = (feUtParallel as String).toInt()

    // Equivalent to reuseForks=false in Maven
    forkEvery = 1
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

// Equivalent to Maven Assembly plugin to create a jar with dependencies
tasks.register<Jar>("jarWithDependencies") {
    archiveClassifier.set("with-dependencies")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.starrocks.load.loadv2.etl.SparkEtlJob"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

// Make the jarWithDependencies task run as part of the build
tasks.build {
    dependsOn("jarWithDependencies")
}

// Set the final JAR name
tasks.jar {
    archiveBaseName.set("spark-dpp")
    archiveVersion.set(project.version.toString())
}

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.starrocks"
version = "1.0.0"

dependencies {
    implementation("com.google.guava:guava")
    implementation("org.roaringbitmap:RoaringBitmap")

    testImplementation("junit:junit")
    testImplementation("com.github.hazendaz.jmockit:jmockit")
}

tasks.withType<Test> {
    // Configure JMockit agent for tests
    jvmArgs("-javaagent:${repositories.mavenLocal().url.path}/com/github/hazendaz/jmockit/jmockit/1.49.4/jmockit-1.49.4.jar")

    // Set for parallel test execution as in the Maven config
    maxParallelForks = providers.gradleProperty("fe_ut_parallel").map { it.toInt() }.getOrElse(1)

    // Equivalent to reuseForks=false in Maven
    forkEvery = 1
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

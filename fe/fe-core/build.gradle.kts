import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    antlr
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    sourceSets {
        main {
            java {
                srcDir("src/main/java")
                srcDir("src/main/thrift")
                srcDir("src/main/genscript")
                srcDir("target/generated-sources/antlr4")
                srcDir("target/generated-sources/proto")
            }
        }
        test {
            java {
                srcDir("src/test/java")
            }
            resources {
                srcDir("src/test/resources")
            }
        }
    }
}

dependencies {
    antlr("org.antlr:antlr4:4.9.2")

    // Internal project dependencies
    implementation(project(":fe-common"))
    implementation(project(":plugin-common"))
    implementation(project(":hive-udf"))
    implementation(project(":spark-dpp"))

    // Core dependencies
    implementation("commons-cli:commons-cli")
    implementation("commons-codec:commons-codec")
    implementation("commons-lang:commons-lang")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-pool2")
    implementation("org.apache.kudu:kudu-client")
    implementation("com.github.seancfoley:ipaddress")
    implementation("org.apache.velocity:velocity-engine-core")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("commons-validator:commons-validator")
    implementation("com.google.code.gson:gson")
    implementation("com.google.guava:guava")

    // Jackson dependencies
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")

    // Cup and JFlex
    implementation("net.sourceforge.czt.dev:java-cup")
    implementation("de.jflex:jflex")

    // JProtobuf
    implementation("com.starrocks:jprotobuf-starrocks")
    implementation("com.baidu:jprotobuf-rpc-common")
    implementation("com.baidu:jprotobuf-rpc-core")
    compileOnly("com.starrocks:jprotobuf-starrocks:${project.ext["jprotobuf-starrocks.version"]}:jar-with-dependencies")
    compileOnly("com.baidu:jprotobuf-precompile-plugin")

    // Logging
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation("org.apache.logging.log4j:log4j-layout-template-json")
    implementation("org.apache.logging.log4j:log4j-1.2-api")

    // Netty
    implementation("io.netty:netty-all")
    implementation("io.netty:netty-handler")

    // Protobuf
    implementation("com.google.protobuf:protobuf-java")
    implementation("com.google.protobuf:protobuf-java-util")

    // HTTP clients
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okio:okio")

    // Other utilities
    implementation("commons-io:commons-io")
    implementation("org.json:json")
    implementation("org.apache.thrift:libthrift")
    implementation("javax.validation:validation-api")
    implementation("org.slf4j:slf4j-api")
    implementation("com.github.oshi:oshi-core")
    implementation("org.roaringbitmap:RoaringBitmap")
    implementation("dnsjava:dnsjava")
    implementation("org.apache.avro:avro")
    implementation("org.apache.ivy:ivy")

    // Kafka
    implementation("org.apache.kafka:kafka-clients")

    // XNIO
    implementation("org.jboss.xnio:xnio-nio")

    // Java 9+ support
    implementation("javax.annotation:javax.annotation-api")
    implementation("com.sun.activation:javax.activation")
    implementation("javax.xml.ws:jaxws-api")

    // Spark
    implementation("org.apache.spark:spark-core_2.12")
    implementation("org.apache.spark:spark-launcher_2.12")

    // Hive
    implementation("io.trino.hive:hive-apache")
    implementation("com.aliyun:datalake20200710")
    implementation("com.aliyun.datalake:metastore-client-hive3")

    implementation("io.trino:trino-parser:385")

    // Hadoop
    implementation("org.apache.hadoop:hadoop-common")
    implementation("commons-beanutils:commons-beanutils")
    implementation("org.apache.hadoop:hadoop-hdfs")
    implementation("org.apache.parquet:parquet-hadoop")
    implementation("org.apache.parquet:parquet-common")
    implementation("org.apache.parquet:parquet-column")
    implementation("org.apache.parquet:parquet-avro")
    implementation("commons-collections:commons-collections")
    implementation("org.apache.hadoop:hadoop-client")
    implementation("org.apache.hadoop:hadoop-client-api")
    implementation("org.apache.hadoop:hadoop-client-runtime")
    implementation("org.apache.hadoop:hadoop-aws")
    implementation("org.jdom:jdom2")
    implementation("org.apache.hadoop:hadoop-aliyun")
    implementation("com.qcloud.cos:hadoop-cos")
    implementation("com.qcloud:chdfs_hadoop_plugin_network")
    implementation("org.apache.hadoop:hadoop-azure-datalake")
    implementation("org.apache.hadoop:hadoop-azure")
    implementation("com.google.cloud.bigdataoss:gcs-connector:${project.ext["gcs.connector.version"]}:shaded")

    implementation("com.azure:azure-storage-blob")
    implementation("com.azure:azure-identity")

    // Scala
    implementation("org.scala-lang:scala-library")
    implementation("com.esotericsoftware:kryo-shaded")

    // Caching and CSV
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.opencsv:opencsv")

    // Paimon
    implementation("org.apache.paimon:paimon-bundle")
    implementation("org.apache.paimon:paimon-s3")
    implementation("org.apache.paimon:paimon-oss")

    // UUID
    implementation("com.fasterxml.uuid:java-uuid-generator")

    // Hudi
    implementation("org.apache.hudi:hudi-common")
    implementation("org.apache.hbase:hbase-client")
    implementation("org.apache.hbase:hbase-server")
    implementation("org.apache.hudi:hudi-io")
    implementation("org.apache.hudi:hudi-hadoop-mr")

    // Database drivers
    implementation("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.postgresql:postgresql")
    implementation("com.clickhouse:clickhouse-jdbc")
    implementation("com.oracle.database.jdbc:ojdbc10")
    implementation("com.oracle.database.nls:orai18n")
    implementation("com.microsoft.sqlserver:mssql-jdbc")
    implementation("com.mysql:mysql-connector-j")
    implementation("org.apache.commons:commons-dbcp2")

    // OpenTelemetry
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-jaeger")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")

    // Compression
    implementation("org.xerial.snappy:snappy-java")

    // Delta and Iceberg
    implementation("io.delta:delta-kernel-api")
    implementation("io.delta:delta-kernel-defaults")
    implementation("org.apache.iceberg:iceberg-api")
    implementation("org.apache.iceberg:iceberg-bundled-guava")
    implementation("org.apache.iceberg:iceberg-common")
    implementation("org.apache.iceberg:iceberg-core")
    implementation("org.apache.iceberg:iceberg-hive-metastore")
    implementation("org.apache.iceberg:iceberg-aws")

    // Starrocks
    implementation("com.starrocks:starclient")
    implementation("com.starrocks:starmanager")

    // Jetty
    implementation("org.eclipse.jetty:jetty-client")
    implementation("org.eclipse.jetty:jetty-server")
    implementation("org.eclipse.jetty:jetty-io")
    implementation("org.eclipse.jetty:jetty-webapp")
    implementation("org.eclipse.jetty:jetty-servlet")
    implementation("org.eclipse.jetty:jetty-util")
    implementation("org.eclipse.jetty:jetty-util-ajax")
    implementation("org.eclipse.jetty:jetty-security")

    // Groovy
    implementation("org.apache.groovy:groovy-groovysh")

    // Airlift components
    implementation("io.airlift:concurrent")
    implementation("io.airlift:aircompressor")
    implementation("io.airlift:security")

    // ODPS
    implementation("com.aliyun.odps:odps-sdk-core")
    implementation("com.aliyun.odps:odps-sdk-table-api")

    // GRPC
    implementation("io.grpc:grpc-api")
    implementation("io.grpc:grpc-netty-shaded")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-core")

    // Arrow
    implementation("org.apache.arrow:arrow-vector")
    implementation("org.apache.arrow:arrow-memory-netty")
    implementation("org.apache.arrow:flight-sql")
    implementation("org.apache.arrow:flight-core")
    implementation("org.apache.arrow:arrow-jdbc")

    // Security
    implementation("org.owasp.encoder:encoder")
    implementation("com.nimbusds:nimbus-jose-jwt")

    // Ranger and ANTLR
    implementation("org.apache.ranger:ranger-plugins-common")
    implementation("org.antlr:antlr4")

    // Byteman
    implementation("org.jboss.byteman:byteman")

    // FastUtil
    implementation("it.unimi.dsi:fastutil")

    implementation("com.starrocks:starrocks-bdb-je")

    implementation("jline:jline")
    implementation("org.apache.groovy:groovy-groovysh")
    implementation("org.apache.groovy:groovy")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    // Test dependencies
    testImplementation("org.apache.spark:spark-sql_2.12")
    testImplementation("junit:junit")
    testImplementation("org.apache.arrow:flight-sql-jdbc-driver")
    testImplementation("com.mockrunner:mockrunner-jdbc")
    testImplementation("com.github.hazendaz.jmockit:jmockit")
    testImplementation("org.openjdk.jmh:jmh-core:1.23")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.23")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("com.carrotsearch:junit-benchmarks:0.7.2")
    testImplementation("com.github.hazendaz.jmockit:jmockit")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

// Configure ANTLR plugin
tasks.generateGrammarSource {
    maxHeapSize = "512m"
    // Add the -lib argument to tell ANTLR where to find imported grammars
    arguments = arguments + listOf(
        "-visitor",
        "-package", "com.starrocks.sql.parser",
    )
    outputDirectory = layout.buildDirectory.get().dir("generated-sources/antlr4/com/starrocks/sql/parser").asFile
}

// Custom task for Protocol Buffer generation
tasks.register<JavaExec>("generateProtoSources") {
    // Create a special configuration for the protobuf compiler rather than using runtime classpath
    val protoGenClasspath = configurations.create("protoGenClasspath")
    dependencies {
        protoGenClasspath("com.starrocks:jprotobuf-starrocks:${project.ext["jprotobuf-starrocks.version"]}")
    }

    // Use the dedicated configuration instead of runtime classpath
    classpath = protoGenClasspath
    mainClass.set("com.baidu.bjf.remoting.protobuf.command.Main")

    val protoDir = "../../gensrc/proto"
    val outputDir = "${layout.buildDirectory.get()}/generated-sources/proto"

    args = listOf(
        "--java_out=$outputDir",
        "$protoDir/lake_types.proto",
        "$protoDir/internal_service.proto",
        "$protoDir/types.proto",
        "$protoDir/tablet_schema.proto"
    )

    doFirst {
        mkdir(outputDir)
    }
}

// Add source generation tasks to the build process
tasks.compileJava {
//    dependsOn("generateGrammarSource", "generateThriftSources", "generateProtoSources")
    dependsOn("generateGrammarSource", "generateProtoSources")
}

tasks.named<ProcessResources>("processTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Configure test task
tasks.test {
    maxParallelForks = (project.findProperty("fe_ut_parallel") as String? ?: "1").toInt()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
    systemProperty("starrocks.home", project.ext["starrocks.home"] as String)
}

// Configure JAR task
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.starrocks.StarRocksFE",
            "Implementation-Version" to project.version
        )
    }
}

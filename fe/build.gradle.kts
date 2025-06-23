import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    `maven-publish`
    checkstyle
}

allprojects {
    group = "com.starrocks"
    version = "3.4.0"

    repositories {
        mavenCentral()
        maven { url = uri("https://repository.cloudera.com/repository/public/") }
        maven { url = uri("https://repository.cloudera.com/repository/cloudera-repos/") }
        maven { url = uri("https://mirror.iscas.ac.cn/kunpeng/maven/") }
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
        plugin("checkstyle")
    }

    // Common properties from pom.xml
    ext {
        set("starrocks.home", "${rootDir}/../")
        set("jprotobuf-starrocks.version", "1.0.0")
        set("log4j.version", "2.19.0")
        set("jackson.version", "2.15.2")
        set("spark.version", "3.5.5")
        set("tomcat.version", "8.5.70")
        set("parquet.version", "1.15.2")
        set("hadoop.version", "3.4.1")
        set("gcs.connector.version", "hadoop3-2.2.26")
        set("hudi.version", "1.0.2")
        set("hive-apache.version", "3.1.2-22")
        set("dlf-metastore-client.version", "0.2.14")
        set("odps.version", "0.48.7-public")
        set("kudu.version", "1.17.1")
        set("hikaricp.version", "3.4.5")
        set("kafka-clients.version", "3.4.0")
        set("arrow.version", "18.0.0")
        set("grpc.version", "1.63.0")
        set("io.netty.version", "4.1.118.Final")
        set("puppycrawl.version", "10.21.1")
        set("aws-v2-sdk.version", "2.29.52")
        set("avro.version", "1.12.0")
        set("dnsjava.version", "3.6.3")
        set("nimbusds.version", "9.37.2")
        set("protobuf-java.version", "3.25.5")
        set("paimon.version", "1.0.1")
        set("delta-kernel.version", "4.0.0rc1")
        set("iceberg.version", "1.9.0")
        set("staros.version", "3.5-rc1")
        set("jetty.version", "9.4.57.v20241219")
        set("byteman.version", "4.0.24")
        set("azure.version", "1.2.34")
        set("fastutil.version", "8.5.15")
        set("commons-beanutils.version", "1.11.0")
        set("hbase.version", "2.6.2")
        set("opentelemetry.version", "1.14.0")
    }

    dependencies {
        // Azure
        implementation(platform("com.azure:azure-sdk-bom:${project.ext["azure.version"]}"))

        constraints {
            // Core dependencies
            implementation("commons-cli:commons-cli:1.4")
            implementation("commons-codec:commons-codec:1.13")
            implementation("commons-lang:commons-lang:2.4")
            implementation("org.apache.commons:commons-lang3:3.9")
            implementation("org.apache.commons:commons-pool2:2.3")
            implementation("org.apache.kudu:kudu-client:${project.ext["kudu.version"]}")
            implementation("com.github.seancfoley:ipaddress:5.4.2")
            implementation("org.apache.velocity:velocity-engine-core:2.4.1")
            implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
            implementation("commons-validator:commons-validator:1.7")
            implementation("com.google.code.gson:gson:2.8.9")
            implementation("com.google.guava:guava:32.0.1-jre")

            // Jackson
            implementation("com.fasterxml.jackson.core:jackson-core:${project.ext["jackson.version"]}")
            implementation("com.fasterxml.jackson.core:jackson-annotations:${project.ext["jackson.version"]}")
            implementation("com.fasterxml.jackson.core:jackson-databind:${project.ext["jackson.version"]}")
            implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${project.ext["jackson.version"]}")
            implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:${project.ext["jackson.version"]}")

            // Cup and JFlex
            implementation("net.sourceforge.czt.dev:java-cup:0.11-a-czt02-cdh")
            implementation("de.jflex:jflex:1.4.3")
            testImplementation("com.github.hazendaz.jmockit:jmockit:1.49.4")

            // JProtobuf
            implementation("com.starrocks:jprotobuf-starrocks:${project.ext["jprotobuf-starrocks.version"]}")
            compileOnly("com.starrocks:jprotobuf-starrocks:${project.ext["jprotobuf-starrocks.version"]}:jar-with-dependencies")
            implementation("com.baidu:jprotobuf-rpc-common:1.9")
            compileOnly("com.baidu:jprotobuf-precompile-plugin:2.2.12")
            implementation("com.baidu:jprotobuf-rpc-core:4.2.1")

            // Other utilities
            implementation("commons-io:commons-io:2.16.1")
            implementation("org.json:json:20231013")
            testImplementation("junit:junit:4.13.1")
            implementation("org.apache.thrift:libthrift:0.20.0")

            // Logging
            implementation("org.apache.logging.log4j:log4j-api:${project.ext["log4j.version"]}")
            implementation("org.apache.logging.log4j:log4j-core:${project.ext["log4j.version"]}")
            implementation("org.apache.logging.log4j:log4j-slf4j-impl:${project.ext["log4j.version"]}")
            implementation("org.apache.logging.log4j:log4j-layout-template-json:${project.ext["log4j.version"]}")
            implementation("org.apache.logging.log4j:log4j-1.2-api:${project.ext["log4j.version"]}")

            // Netty
            implementation("io.netty:netty-all:${project.ext["io.netty.version"]}")
            implementation("io.netty:netty-handler:${project.ext["io.netty.version"]}")

            // Protobuf
            implementation("com.google.protobuf:protobuf-java:${project.ext["protobuf-java.version"]}")
            implementation("com.google.protobuf:protobuf-java-util:${project.ext["protobuf-java.version"]}")

            // HTTP clients
            implementation("com.squareup.okhttp3:okhttp:4.10.0")
            implementation("com.squareup.okio:okio:3.4.0")

            // Validation and logging
            implementation("javax.validation:validation-api:1.1.0.Final")
            implementation("org.slf4j:slf4j-api:1.7.30")
            implementation("com.github.oshi:oshi-core:6.2.1")

            // Kafka
            implementation("org.apache.kafka:kafka-clients:${project.ext["kafka-clients.version"]}")

            // XNIO
            implementation("org.jboss.xnio:xnio-nio:3.8.16.Final")

            // Java 9+ support
            implementation("javax.annotation:javax.annotation-api:1.3.2")
            implementation("com.sun.activation:javax.activation:1.2.0")
            implementation("javax.xml.ws:jaxws-api:2.3.0")

            // Roaring bitmap and DNS
            implementation("org.roaringbitmap:RoaringBitmap:0.8.13")
            implementation("dnsjava:dnsjava:${project.ext["dnsjava.version"]}")

            // Avro and Ivy
            implementation("org.apache.avro:avro:${project.ext["avro.version"]}")
            implementation("org.apache.ivy:ivy:2.5.2")

            // Spark
            implementation("org.apache.spark:spark-core_2.12:${project.ext["spark.version"]}")
            implementation("org.apache.spark:spark-launcher_2.12:${project.ext["spark.version"]}")
            compileOnly("org.apache.spark:spark-sql_2.12:${project.ext["spark.version"]}")
            compileOnly("org.apache.spark:spark-catalyst_2.12:${project.ext["spark.version"]}")
            testImplementation("org.apache.spark:spark-sql_2.12:${project.ext["spark.version"]}")

            // Hive
            implementation("io.trino.hive:hive-apache:${project.ext["hive-apache.version"]}")
            implementation("com.aliyun:datalake20200710:2.0.12")
            implementation("com.aliyun.datalake:metastore-client-hive3:${project.ext["dlf-metastore-client.version"]}")

            // Hadoop
            implementation("org.apache.hadoop:hadoop-common:${project.ext["hadoop.version"]}")
            implementation("commons-beanutils:commons-beanutils:${project.ext["commons-beanutils.version"]}")
            implementation("org.apache.hadoop:hadoop-hdfs:${project.ext["hadoop.version"]}")
            implementation("org.apache.parquet:parquet-hadoop:${project.ext["parquet.version"]}")
            implementation("org.apache.parquet:parquet-common:${project.ext["parquet.version"]}")
            implementation("org.apache.parquet:parquet-column:${project.ext["parquet.version"]}")
            implementation("org.apache.parquet:parquet-avro:${project.ext["parquet.version"]}")
            implementation("commons-collections:commons-collections:3.2.2")

            // Scala
            implementation("org.scala-lang:scala-library:2.12.10")
            implementation("com.esotericsoftware:kryo-shaded:4.0.2")

            // Caching and CSV
            implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")
            implementation("com.opencsv:opencsv:5.7.1")

            // More Hadoop components
            implementation("org.apache.hadoop:hadoop-client:${project.ext["hadoop.version"]}")
            implementation("org.apache.hadoop:hadoop-client-api:${project.ext["hadoop.version"]}")
            implementation("org.apache.hadoop:hadoop-client-runtime:${project.ext["hadoop.version"]}")
            implementation("org.apache.hadoop:hadoop-aws:${project.ext["hadoop.version"]}")
            implementation("org.jdom:jdom2:2.0.6.1")
            implementation("org.apache.hadoop:hadoop-aliyun:${project.ext["hadoop.version"]}")
            implementation("com.qcloud.cos:hadoop-cos:3.3.0-8.3.2")
            implementation("com.qcloud:chdfs_hadoop_plugin_network:3.2")
            implementation("org.apache.hadoop:hadoop-azure-datalake:${project.ext["hadoop.version"]}")
            implementation("org.apache.hadoop:hadoop-azure:${project.ext["hadoop.version"]}")
            implementation("com.google.cloud.bigdataoss:gcs-connector:${project.ext["gcs.connector.version"]}:shaded")

            // UUID
            implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")

            // Hudi
            implementation("org.apache.hudi:hudi-common:${project.ext["hudi.version"]}")
            implementation("org.apache.hbase:hbase-client:${project.ext["hbase.version"]}")
            implementation("org.apache.hbase:hbase-server:${project.ext["hbase.version"]}")
            implementation("org.apache.hudi:hudi-io:${project.ext["hudi.version"]}")
            implementation("org.apache.hudi:hudi-hadoop-mr:${project.ext["hudi.version"]}")

            // Database drivers
            implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
            implementation("org.postgresql:postgresql:42.4.4")
            implementation("com.clickhouse:clickhouse-jdbc:0.4.6")
            implementation("com.oracle.database.jdbc:ojdbc10:19.18.0.0")
            implementation("com.oracle.database.nls:orai18n:19.18.0.0")
            implementation("com.microsoft.sqlserver:mssql-jdbc:12.4.2.jre11")
            implementation("com.mysql:mysql-connector-j:9.2.0")
            testImplementation("com.mockrunner:mockrunner-jdbc:1.0.1")
            implementation("org.apache.commons:commons-dbcp2:2.9.0")

            // Groovy
            implementation("jline:jline:2.14.6")
            implementation("org.apache.groovy:groovy:4.0.9")
            implementation("org.apache.groovy:groovy-groovysh:4.0.9")

            // Airlift components
            implementation("io.airlift:concurrent:202")
            implementation("io.airlift:aircompressor:0.27")
            implementation("io.airlift:security:202")

            // ODPS
            implementation("com.aliyun.odps:odps-sdk-core:${project.ext["odps.version"]}")
            implementation("com.aliyun.odps:odps-sdk-table-api:${project.ext["odps.version"]}")

            // GRPC
            implementation("io.grpc:grpc-api:${project.ext["grpc.version"]}")
            implementation("io.grpc:grpc-netty-shaded:${project.ext["grpc.version"]}")
            implementation("io.grpc:grpc-protobuf:${project.ext["grpc.version"]}")
            implementation("io.grpc:grpc-stub:${project.ext["grpc.version"]}")
            implementation("io.grpc:grpc-core:${project.ext["grpc.version"]}")

            // Arrow
            implementation("org.apache.arrow:arrow-vector:${project.ext["arrow.version"]}")
            implementation("org.apache.arrow:arrow-memory-netty:${project.ext["arrow.version"]}")
            implementation("org.apache.arrow:flight-sql:${project.ext["arrow.version"]}")
            implementation("org.apache.arrow:flight-core:${project.ext["arrow.version"]}")
            testImplementation("org.apache.arrow:flight-sql-jdbc-driver:${project.ext["arrow.version"]}")
            implementation("org.apache.arrow:arrow-jdbc:${project.ext["arrow.version"]}")

            // Security
            implementation("org.owasp.encoder:encoder:1.3.1")
            implementation("com.nimbusds:nimbus-jose-jwt:${project.ext["nimbusds.version"]}")

            // Paimon
            implementation("org.apache.paimon:paimon-bundle:${project.ext["paimon.version"]}")
            implementation("org.apache.paimon:paimon-s3:${project.ext["paimon.version"]}")
            implementation("org.apache.paimon:paimon-oss:${project.ext["paimon.version"]}")

            // Delta and Iceberg
            implementation("io.delta:delta-kernel-api:${project.ext["delta-kernel.version"]}")
            implementation("io.delta:delta-kernel-defaults:${project.ext["delta-kernel.version"]}")
            implementation("org.apache.iceberg:iceberg-api:${project.ext["iceberg.version"]}")
            implementation("org.apache.iceberg:iceberg-bundled-guava:${project.ext["iceberg.version"]}")
            implementation("org.apache.iceberg:iceberg-common:${project.ext["iceberg.version"]}")
            implementation("org.apache.iceberg:iceberg-core:${project.ext["iceberg.version"]}")
            implementation("org.apache.iceberg:iceberg-hive-metastore:${project.ext["iceberg.version"]}")
            implementation("org.apache.iceberg:iceberg-aws:${project.ext["iceberg.version"]}")

            // Starrocks
            implementation("com.starrocks:starclient:${project.ext["staros.version"]}")
            implementation("com.starrocks:starmanager:${project.ext["staros.version"]}")

            // Compression
            implementation("org.xerial.snappy:snappy-java:1.1.10.5")

            // Jetty
            implementation("org.eclipse.jetty:jetty-client:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-server:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-io:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-webapp:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-servlet:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-util:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-util-ajax:${project.ext["jetty.version"]}")
            implementation("org.eclipse.jetty:jetty-security:${project.ext["jetty.version"]}")

            // Ranger and ANTLR
            implementation("org.apache.ranger:ranger-plugins-common:2.5.0")
            implementation("org.antlr:antlr4:4.9.2")

            // Byteman
            implementation("org.jboss.byteman:byteman:${project.ext["byteman.version"]}")

            // FastUtil
            implementation("it.unimi.dsi:fastutil:${project.ext["fastutil.version"]}")

            // OpenTelemetry
            implementation("io.opentelemetry:opentelemetry-api:${project.ext["opentelemetry.version"]}")
            implementation("io.opentelemetry:opentelemetry-sdk:${project.ext["opentelemetry.version"]}")
            implementation("io.opentelemetry:opentelemetry-exporter-jaeger:${project.ext["opentelemetry.version"]}")
            implementation("io.opentelemetry:opentelemetry-exporter-otlp:${project.ext["opentelemetry.version"]}")

            // Module dependencies
            implementation("com.starrocks:plugin-common:1.0.0")
            implementation("com.starrocks:hive-udf:1.0.0")
            implementation("com.starrocks:fe-common:1.0.0")
            implementation("com.starrocks:spark-dpp:1.0.0")
            implementation("com.starrocks:starrocks-bdb-je:18.3.20")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }

    checkstyle {
        toolVersion = "10.21.1"
        configFile = file("${rootDir}/checkstyle.xml")
    }

    tasks.withType<Checkstyle> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}

// Profile-like configurations similar to pom.xml profiles
if (project.hasProperty("spark3")) {
    allprojects {
        ext {
            set("spark.version", "3.3.1")
        }
    }
} else {
    // Default to spark2 like in pom.xml
    allprojects {
        ext {
            set("spark.version", "2.4.6")
        }
    }
}

// Custom environment profiles would be handled through -P flags or gradle.properties

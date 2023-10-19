plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("com.palantir.graal") version "0.12.0"
}


group = "hes.instacli"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.+")
    implementation("com.networknt:json-schema-validator:1.0.79")
    implementation("io.javalin:javalin:5.4.+")
    implementation("io.ktor:ktor-client-core:2.2.+")
    implementation("io.ktor:ktor-client-java:2.2+")
    implementation("io.ktor:ktor-client-auth:2.2+")
    implementation("org.slf4j:slf4j-simple:2.0.+")

    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")
    implementation("org.jline:jline:3.22.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("com.lordcodes.turtle:turtle:0.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
}

tasks.test {
    useJUnitPlatform()
}

//
// Integration tests
//

sourceSets {
    create("testIntegration") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}

val testIntegration = task<Test>("testIntegration") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
    mustRunAfter(tasks["test"])
}

tasks.getByName<Test>("testIntegration") {
    useJUnitPlatform()
}

//
// Java version
//
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

//
// Executable jar file
//

tasks.jar {
    manifest {
        attributes["Main-Class"] = "instacli.cli.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

//
// Graal
//

graal {
    outputName("instacli-native")
    mainClass("instacli.cli.MainKt")
    javaVersion("11")
    graalVersion("22.3.2")
    option("--no-fallback")
    option("--enable-http")
    option("--enable-https")
    option("--report-unsupported-elements-at-runtime")
    option("-H:ReflectionConfigurationResources=reflect-config.json")
    option("-H:ReflectionConfigurationResources=META-INF/native-image/jansi/jni-config.json")
    option("-H:ResourceConfigurationFiles=src/main/resources/resource-config.json")
    option("-H:IncludeResources=org/fusesource/jansi/jansi.properties")
//    option("-Djava.util.logging.config.file=/logging.properties")
}
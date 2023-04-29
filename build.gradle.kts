import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.palantir.graal") version "0.12.0"
}


group = "hes.yak"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.+")
    implementation("io.javalin:javalin:5.4.+")
    implementation("io.ktor:ktor-client-core:2.2.+")
    implementation("io.ktor:ktor-client-java:2.2+")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.+")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.+")
    implementation("org.slf4j:slf4j-simple:2.0.+")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.assertj:assertj-core:3.24.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "yay.Cli"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

graal {
    outputName("yay-native")
    mainClass("yay.Cli")
    javaVersion("11")
    option("--no-fallback")
    option("--enable-https")
    option("-H:ReflectionConfigurationResources=reflect-config.json")
}
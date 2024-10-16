group = "hes.instacli"
version = "0.2.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+")
    implementation("com.networknt:json-schema-validator:1.4.0")
    implementation("io.ktor:ktor-client-core:2.3.+")
    implementation("io.ktor:ktor-client-java:2.3+")
    implementation("io.ktor:ktor-client-auth:2.3+")
    implementation("org.slf4j:slf4j-simple:2.0.+")
    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")
    implementation("org.jline:jline:3.22.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("com.lordcodes.turtle:turtle:0.8.0")
    implementation("io.javalin:javalin:6.1.+")
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("net.pwall.json:json-kotlin-schema:0.47")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

sourceSets.main.get().resources.srcDir("instacli-spec")

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            sources {
                java {
                    setSrcDirs(listOf("src/tests/unit"))
                }
            }
        }

        val specificationTest by registering(JvmTestSuite::class) {

            dependencies {
                implementation(project())
                implementation("io.kotest:kotest-assertions-core:5.7.2")
            }

            sources {
                java {
                    setSrcDirs(listOf("src/tests/specification"))
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {

            dependencies {
                implementation(project())
            }

            sources {
                java {
                    setSrcDirs(listOf("src/tests/integration"))
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}


tasks.named("check") {
    dependsOn(testing.suites.named("specificationTest"))
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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

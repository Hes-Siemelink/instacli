group = "hes.easyspec"
version = "0.3.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenLocal()
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
//    implementation("com.networknt:json-schema-validator:1.4.0")
//    implementation("io.ktor:ktor-client-core:3.0.+")
//    implementation("io.ktor:ktor-client-java:3.0+")
//    implementation("io.ktor:ktor-client-auth:3.0+")
//    implementation("org.slf4j:slf4j-simple:2.0.+")
//    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")
//    implementation("org.jline:jline:3.27.+")
//    implementation("org.fusesource.jansi:jansi:2.4.1")
//    implementation("com.lordcodes.turtle:turtle:0.10.0")
//    implementation("io.javalin:javalin:6.3.+")
//    implementation("org.xerial:sqlite-jdbc:3.47.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
//    testImplementation("net.pwall.json:json-kotlin-schema:0.47")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

sourceSets.main.get().resources.srcDir("spec")

sourceSets {
    main {
        kotlin {
            setSrcDirs(listOf("implementation/main"))
        }
    }

    test {
        kotlin {
            setSrcDirs(listOf("implementation/test"))
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            sources {
                java {
                    setSrcDirs(listOf("implementation/tests/unit"))
                }
            }
        }

        register<JvmTestSuite>("specificationTest") {

            dependencies {
                implementation(project())
            }

            sources {
                java {
                    setSrcDirs(listOf("implementation/tests/specification"))
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
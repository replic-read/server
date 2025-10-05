plugins {
    java
    id("org.sonarqube") version "6.3.1.5724"
}

group = "com.rere"
version = "0.0.1"
description = "The backend component of the replic-read system. Exposes the REST-API for communication with clients."

/*
 * Java language level configuration
 */

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

subprojects {
    plugins.withType<JavaPlugin> {
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}

/*
 * Dependency setup
 */

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/*
 * JUnit setup
 */

tasks.withType<Test> {
    useJUnitPlatform()
}
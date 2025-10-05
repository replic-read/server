plugins {
    id("java-conventions")
    id("test-conventions")
    id("org.sonarqube") version "6.3.1.5724"
}

group = "com.rere"
version = "0.0.1"
description = "The backend component of the replic-read system. Exposes the REST-API for communication with clients."

/*
 * Dependency setup
 */

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.5.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.6")
}
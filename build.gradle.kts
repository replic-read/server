plugins {
    id("java-conventions")
    id("test-conventions")
    id("org.sonarqube") version "6.3.1.5724"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
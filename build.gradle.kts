import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.sonarqube)
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
    implementation(libs.starter.actuator)

    // Required for detecting the controllers.
    implementation(project(":inter:dispatching"))

    // Required because no other module set a dependency on infrastructure.
    // Without this, we don't scan the components.
    implementation(project(":infrastructure:database"))
    implementation(project(":infrastructure:io"))
    implementation(project(":infrastructure:messaging"))
}

// See https://stackoverflow.com/questions/69134136/gradle-7-task-disttar-is-a-duplicate-but-no-duplicate-handling-strategy-has-b
tasks.named<BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
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
    implementation(libs.starter)
    testImplementation(libs.starter.test)
}
plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.spring.boot)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.starter)
    testImplementation(libs.starter.test)
}
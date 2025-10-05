plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.starter)
    testImplementation(libs.starter.test)
}
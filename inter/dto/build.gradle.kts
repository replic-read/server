plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.lombok)
    implementation(libs.starter.validation)
    implementation(libs.jackson)
    implementation(project(":domain:model"))
    annotationProcessor(libs.lombok)
}
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
    implementation(project(":domain:model"))
    implementation(project(":domain:service"))
    implementation(libs.starter.web)

    annotationProcessor(libs.lombok)
}
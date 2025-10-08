plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

dependencies {
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.starter.data)
    implementation(project(":domain:model"))
    implementation(project(":domain:repository"))
    implementation(libs.h2)
}
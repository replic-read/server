plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

dependencies {
    implementation(project(":domain:model"))
    implementation(project(":domain:repository"))
    implementation(project(":domain:io"))
    implementation(project(":domain:messaging"))
    implementation(libs.starter.security)
    implementation(libs.jwt)
}
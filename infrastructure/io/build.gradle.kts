plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

dependencies {
    implementation(project(":domain:model"))
    implementation(project(":domain:io"))
}
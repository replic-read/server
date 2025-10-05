plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
}

dependencies {
    implementation(libs.starter)
    implementation(project(":domain:model"))
}
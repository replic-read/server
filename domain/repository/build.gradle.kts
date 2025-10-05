plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
}

dependencies {
    implementation(project(":domain:model"))
}
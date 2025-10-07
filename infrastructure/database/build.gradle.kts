plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

dependencies {
    implementation(libs.starter.data)
}
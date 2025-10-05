plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
}

dependencies {
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}
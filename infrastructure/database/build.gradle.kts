plugins {
    alias(libs.plugins.convention.java)
    alias(libs.plugins.convention.test)
    alias(libs.plugins.convention.spring)
}

dependencies {
    implementation(libs.lombok)
    implementation(libs.postgres)
    implementation(libs.starter.data)
    implementation(project(":domain:model"))
    implementation(project(":domain:repository"))

    testImplementation(libs.h2)

    annotationProcessor(libs.lombok)

    testAnnotationProcessor(libs.lombok)
}
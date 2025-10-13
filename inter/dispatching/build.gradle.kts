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
    implementation(libs.starter.web)
    implementation(libs.starter.security)
    implementation(libs.spring.security.test)
    implementation(libs.springdoc.starter)
    implementation(project(":domain:model"))
    implementation(project(":domain:service"))
    implementation(project(":inter:execution"))
    implementation(project(":inter:dto"))

    annotationProcessor(libs.lombok)
}
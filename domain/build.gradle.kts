plugins {
    id("java-conventions")
    id("test-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.5.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.6")
}
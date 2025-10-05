import com.rere.server.build.versions.lib

plugins {
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    add("testImplementation", "org.junit.jupiter:junit-jupiter:5.11.3")
    add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    add("testImplementation", lib("mockito"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required = true
    }
}
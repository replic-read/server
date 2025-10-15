plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.11.1")
}

gradlePlugin {
    plugins {
        //register("myConventionPlugin") {
        //    id = "com.example.convention"
        //    implementationClass = "com.example.ConventionPlugin"
        //}
    }
}
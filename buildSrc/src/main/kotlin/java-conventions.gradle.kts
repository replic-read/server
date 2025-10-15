plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Jar> {
    var p: Project? = project
    var archiveName = ""

    while (p != null) {
        archiveName += "-${p.name}"
        p = p.parent
    }

    archiveBaseName.set(archiveName.removePrefix("-"))
}
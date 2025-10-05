package com.rere.server.build.versions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * The version catalog passed through from the root project.
 */
private val Project.libs: VersionCatalog
    get() = extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")

/**
 * Finds a library from the version catalog.
 *
 * @param name The name scheme as declared in the version catalog.
 */
fun Project.lib(name: String) = libs.findLibrary(name)
    .orElseThrow { IllegalArgumentException("Library identifier '$name' did not resolute to any library.") }
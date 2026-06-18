import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    // ✅ FIX: Infix functions require either NO parentheses (just spaces)
    // OR a dot operator if you use them like a method call:
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}

dependencyResolutionManagement {
    // ✅ FIX: Explicitly imported above, assignment syntax works cleanly
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "Es FISH WX"
include(":app")
include(":wear")
// En settings.gradle.kts
pluginManagement {
    repositories {
        google() // Repositorio de Google para plugins
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TFGFer"
include(":app")
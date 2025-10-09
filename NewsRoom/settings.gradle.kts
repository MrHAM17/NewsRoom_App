pluginManagement {
    repositories {
//        google {
//            content {
//                includeGroupByRegex("com\\.android.*")
//                includeGroupByRegex("com\\.google.*")
//                includeGroupByRegex("androidx.*")
//            }
//        }

        // ✅ CRITICAL FIX: Use the standard google() call here.
        // While your filtering (content { ... }) is usually good practice,
        // it sometimes prevents Gradle from resolving specific plugin artifacts
        // like the Baseline Profile plugin's internal .gradle.plugin file.
        google()
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

rootProject.name = "NewsRoom"
include(":app")
include(":benchmark")

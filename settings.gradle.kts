pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.boox.com/repository/proxy-public/")
        }
        maven {
            url = uri("https://repo.boox.com/repository/maven-public/")
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repo.boox.com/repository/proxy-public/")
        }
        maven {
            url = uri("https://repo.boox.com/repository/maven-public/")
        }
    }
}

rootProject.name = "Wanderwall"
include(":app")

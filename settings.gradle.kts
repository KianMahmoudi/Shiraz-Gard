pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://google403.ir/")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://google403.ir/")
    }
}

rootProject.name = "ShirazGard"
include(":app")

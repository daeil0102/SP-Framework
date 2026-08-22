rootProject.name = "SP-Framework"

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

include("plugin", "fabric", "proxy")
include("JPALib")
include("lang")
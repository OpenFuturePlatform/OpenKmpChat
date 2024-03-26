pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "OpenChatKMP"
include(":androidApp")
include(":shared")
include(":webApp")
include(":compose-desktop")
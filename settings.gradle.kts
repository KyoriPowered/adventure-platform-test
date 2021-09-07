
pluginManagement {
  plugins {
    id("xyz.jpenilla.run-paper") version "1.0.3"

  }
}

rootProject.name = "adventure-platform-test"

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    mavenCentral()
    maven(url = "https://repo.spongepowered.org/repository/maven-public") {
      name = "sponge"
      mavenContent {
        includeGroup("org.spongepowered")
      }
    }
    maven("https://papermc.io/repo/repository/maven-public/") {
      name = "papermc"
      mavenContent {
        includeGroupByRegex("io\\.papermc\\..*")
        includeGroupByRegex("com\\.destroystokyo\\..*")
      }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      name = "ossrh"
      mavenContent { snapshotsOnly() }
    }
  }
}

listOf("bukkit", "bukkit-legacy", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "test-$it"
}

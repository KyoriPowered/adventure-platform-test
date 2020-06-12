plugins {
  id("ca.stellardrift.opinionated") version "3.0" apply false
  id("com.github.johnrengelman.shadow") version "5.2.0" apply false
}

group = "net.kyori"
version = "0.1-SNAPSHOT"
description = "Test plugins for the Adventure library"

subprojects {
  apply(plugin = "ca.stellardrift.opinionated")

  repositories {
    jcenter()
    maven(url = "https://oss.sonatype.org/content/groups/public/") {
      name = "sonatype-oss"
    }
  }

  description = rootProject.description
}


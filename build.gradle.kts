plugins {
  id("ca.stellardrift.opinionated") version "3.1" apply false
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

  dependencies {
    "implementation"("net.kyori:adventure-text-minimessage:3.0.0-SNAPSHOT")
  }
}


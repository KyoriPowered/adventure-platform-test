plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
  id("xyz.jpenilla.run-paper") version "1.0.2"
}

repositories {
  maven("https://papermc.io/repo/repository/maven-public/") {
    name = "papermc"
  }
}

val mcVersion = "1.16.5"

dependencies {
  implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
  shadow("com.destroystokyo.paper:paper-api:$mcVersion-R0.1-SNAPSHOT")
}

tasks {
  runServer {
    minecraftVersion(mcVersion)
    jvmArgs("-Dnet.kyori.adventure.debug=true")
  }

  shadowJar {
    minimize()

    // Don't relocate in dev (for debugging)
    // if ("runServer" !in gradle.startParameter.taskNames) {
      sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
        relocate(it, "net.kyori.adventure.test.bukkit.ext.$it") {
          exclude("net/kyori/adventure/test/**")
        }
      }
    // }
    dependencies {
      exclude(dependency("org.checkerframework:.*"))
    }
  }

  assemble {
    dependsOn(shadowJar)
  }

  processResources {
    inputs.property("meta.version", project.version)

    filesMatching("plugin.yml") {
      expand("version" to project.version)
    }
  }
}

plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
  id("xyz.jpenilla.run-paper")
}

val mcVersion = "1.17.1"

dependencies {
  implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
  shadow("io.papermc.paper:paper-api:$mcVersion-R0.1-SNAPSHOT")
}

java {
  disableAutoTargetJvm()
}

indra {
  javaVersions {
    minimumToolchain(16)
  }
}

tasks {
  fun createVersionedRun(version: String, javaVersion: Int) {
    register("runServer${version.replace(".", "")}", xyz.jpenilla.runpaper.task.RunServerTask::class) {
      group = "run paper"
      pluginJars.from(shadowJar.flatMap { it.archiveFile })
      minecraftVersion(version)
      jvmArgs("-Dnet.kyori.adventure.debug=true")
      runDirectory(file("run/$version/"))
      javaLauncher.set(project.javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
      })
    }
  }

  mapOf(
    setOf("1.8.8", "1.9.4", "1.10.2", "1.11.2", "1.12.2") to 8,
    setOf("1.13.2", "1.14.4", "1.15.2") to 11,
    setOf("1.16.5") to 16
  ).forEach { (minecraftVersions, javaVersion) ->
    for (version in minecraftVersions) {
      createVersionedRun(version, javaVersion)
    }
  }

  runServer {
    minecraftVersion(mcVersion)
    jvmArgs("-Dnet.kyori.adventure.debug=true")
    runDirectory(file("run/latest/"))
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
      exclude(dependency("org.jetbrains:.*"))
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

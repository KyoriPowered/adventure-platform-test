plugins {
  id("com.github.johnrengelman.shadow")
  id("xyz.jpenilla.run-paper")
}

val mcVersion = "1.17.1"
java {
  disableAutoTargetJvm()
}

indra {
  javaVersions {
    minimumToolchain(16)
  }
}

dependencies {
  implementation(project(":test-bukkit"))
  implementation("com.google.code.gson:gson:2.8.7")
}

tasks.shadowJar.configure {
  sequenceOf("net.kyori.adventure", "net.kyori.examination", "com.google.gson").forEach {
    relocate(it, "net.kyori.adventure.test.bukkit.ext.$it") {
      exclude("net/kyori/adventure/test/**")
    }
  }
  dependencies {
    exclude(dependency("org.jetbrains:.*"))
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
    for(version in minecraftVersions) {
      createVersionedRun(version, javaVersion)
    }
  }

  runServer {
    minecraftVersion(mcVersion)
    jvmArgs("-Dnet.kyori.adventure.debug=true")
    runDirectory(file("run/latest/"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

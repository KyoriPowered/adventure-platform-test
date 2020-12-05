plugins {
  id("com.github.johnrengelman.shadow") // version in root pom
  id("kr.entree.spigradle.bungee") version "2.2.3"
}

dependencies {
  implementation("net.kyori:adventure-text-minimessage:4.0.0-SNAPSHOT")
  implementation("net.kyori:adventure-platform-bungeecord:4.0.0-SNAPSHOT")
  shadow("net.md-5:bungeecord-api:1.15-SNAPSHOT")
}

bungee {
  debug {
    jvmArgs = jvmArgs + listOf("-Dnet.kyori.adventure.debug=true")
  }
}

tasks.shadowJar.configure {
  minimize()
  if ("runBungee" !in gradle.startParameter.taskNames && "debugBungee" !in gradle.startParameter.taskNames) {
    sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
      relocate(it, "net.kyori.adventure.test.bungee.ext.$it") {
        exclude("net/kyori/adventure/test/**")
      }
    }
  }
  dependencies {
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

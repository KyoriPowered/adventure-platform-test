plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
  id("net.minecrell.plugin-yml.bungee") version "0.3.0"
}

dependencies {
  implementation(project(":test-platform"))
  implementation("net.kyori:adventure-platform-bungeecord:4.0.0-SNAPSHOT")
  shadow("net.md-5:bungeecord-api:1.15-SNAPSHOT")
}

opinionated {
  mit()
  github("KyoriPowered", "adventure-platform-test")
}

bungee {
  main = "net.kyori.adventure.test.bungee.AdventureTestPlugin"
}

tasks.shadowJar.configure {
  minimize()
  sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
    relocate(it, "net.kyori.adventure.test.bungee.ext.$it") {
      exclude("net/kyori/adventure/test/**")
    }
  }
  dependencies {
    exclude(dependency("com.google.code.gson:.*"))
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

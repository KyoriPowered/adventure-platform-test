plugins {
  id("com.github.johnrengelman.shadow") // version in root pom
  id("net.minecrell.plugin-yml.bungee") version "0.3.0"
}

dependencies {
  implementation("net.kyori:adventure-platform-bungeecord:4.0.0-SNAPSHOT")
  shadow("net.md-5:bungeecord-api:1.15-SNAPSHOT")
}

opinionated {
  github("KyoriPowered", "adventure-testplugins")
  mit()
}

bungee {
  main = "net.kyori.testplugin.bungee.AdventureTestPlugin"
}

tasks.shadowJar.configure {
  minimize()
  mergeServiceFiles()
  sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
    relocate(it, "net.kyori.testplugin.paper.ext.$it")
  }
  dependencies {
    exclude(dependency("com.google.code.gson:.*"))
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

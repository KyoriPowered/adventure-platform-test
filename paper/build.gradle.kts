import ca.stellardrift.build.common.paper

plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
  id("kr.entree.spigradle") version "1.2.4"
}

repositories {
  paper()
}

dependencies {
  implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
  shadow("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
}

opinionated {
  mit()
  github("KyoriPowered", "adventure-testplugins")
}

spigot {
  apiVersion = "1.13"
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

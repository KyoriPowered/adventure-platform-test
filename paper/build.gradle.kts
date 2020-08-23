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
  github("KyoriPowered", "adventure-platform-test")
}

spigot {
  apiVersion = "1.13"

  commands {
    create("adventure") {
      description = "Test command for adventure"
      permission = "adventure.test"
    }
  }
}

tasks.shadowJar.configure {
  minimize()
  sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
    relocate(it, "net.kyori.adventure.test.paper.ext.$it") {
      exclude("net/kyori/adventure/test/**")
    }
  }
  dependencies {
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

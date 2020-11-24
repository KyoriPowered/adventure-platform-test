import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc

plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
  id("kr.entree.spigradle") version "2.2.3"
}

repositories {
  papermc()
}

val mcVersion = "1.16.4"

dependencies {
  implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
  shadow(paper("$mcVersion-R0.1-SNAPSHOT"))
}

indra {
  mitLicense()
  github("KyoriPowered", "adventure-platform-test")
}

spigot {
  apiVersion = "1.13"

  debug {
    buildVersion = mcVersion
    jvmArgs = jvmArgs + listOf("-Dnet.kyori.adventure.printErrors=true", "-Dnet.kyori.adventure.printChosenHandler=true")
  }

  commands {
    create("adventure") {
      description = "Test command for adventure"
      permission = "adventure.test"
    }
  }
}

tasks.shadowJar.configure {
  minimize()

  // Don't relocate in dev (for debugging)
  if ("debugPaper" !in gradle.startParameter.taskNames) {
    sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
      relocate(it, "net.kyori.adventure.test.paper.ext.$it") {
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

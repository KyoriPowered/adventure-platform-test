import ca.stellardrift.build.common.sponge

plugins {
  id("com.github.johnrengelman.shadow") // version in root project
  id("ca.stellardrift.templating") version "3.0"
}

repositories {
  sponge()
}

dependencies {
  implementation("net.kyori:adventure-platform-spongeapi:4.0.0-SNAPSHOT")
  annotationProcessor(shadow("org.spongepowered:spongeapi:7.2.0")!!)
}

opinionated {
  mit()
  github("KyoriPowered", "adventure-platform-test")
}

tasks.shadowJar.configure {
  minimize {
    exclude(dependency("net.kyori:adventure-platform-spongeapi"))
  }
  mergeServiceFiles()
  sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
    relocate(it, "net.kyori.adventure.test.sponge.ext.$it") {
      exclude("net.kyori.adventure.test.*")
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

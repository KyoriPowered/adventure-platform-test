
plugins {
  id("com.github.johnrengelman.shadow") // version in root project
  id("ca.stellardrift.templating") version "4.0.1"
}

repositories {
  maven(url = "https://repo-new.spongepowered.org/repository/maven-public") {
    name = "sponge"
  }
}

dependencies {
  implementation("net.kyori:adventure-platform-spongeapi:4.0.0-SNAPSHOT")
  annotationProcessor(shadow("org.spongepowered:spongeapi:7.3.0")!!)
}

indra {
  mitLicense()
  github("KyoriPowered", "adventure-platform-test")
}

tasks.shadowJar.configure {
  minimize()
  sequenceOf("net.kyori.adventure", "net.kyori.examination").forEach {
    relocate(it, "net.kyori.adventure.test.sponge.ext.$it") {
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

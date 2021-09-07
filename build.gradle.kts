import net.kyori.indra.IndraExtension

plugins {
  val indraVersion = "2.0.6"
  id("net.kyori.indra") version indraVersion apply false
  id("com.github.johnrengelman.shadow") version "7.0.0" apply false
  id("com.github.ben-manes.versions") version "0.38.0"
}

allprojects {
  group = "net.kyori"
  version = "0.1-SNAPSHOT"
  description = "Test plugins for the Adventure library"
}

subprojects {
  apply(plugin = "net.kyori.indra")
  apply(plugin = "net.kyori.indra.license-header")
  apply(plugin = "net.kyori.indra.checkstyle")

  extensions.getByType(IndraExtension::class).apply {
    github("KyoriPowered", "adventure-platform-test")
    mitLicense()
  }

  dependencies {
    "implementation"("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT")
    "checkstyle"("ca.stellardrift:stylecheck:0.1")
  }

  tasks.withType(Javadoc::class) {
    onlyIf { false }
  }
}


import net.kyori.indra.IndraExtension
import net.kyori.indra.sonatypeSnapshots

plugins {
  val indraVersion = "1.2.0"
  id("net.kyori.indra") version indraVersion apply false
  id("com.github.johnrengelman.shadow") version "6.1.0" apply false
  id("com.github.ben-manes.versions") version "0.36.0"
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

  repositories {
    mavenLocal()
    jcenter()
    sonatypeSnapshots()
  }

  extensions.getByType(IndraExtension::class).apply {
    github("KyoriPowered", "adventure-platform-test")
    mitLicense()
  }

  dependencies {
    "implementation"("net.kyori:adventure-text-minimessage:4.0.0-SNAPSHOT")
    "checkstyle"("ca.stellardrift:stylecheck:0.1")
  }

  tasks.withType(Javadoc::class) {
    onlyIf { false }
  }
}



plugins {
  id("com.github.johnrengelman.shadow") // version in root project
  id("ca.stellardrift.templating") version "4.0.1"
}

val spongeRunClasspath by configurations.creating {
  attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
    attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class, Category.LIBRARY))
    attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class, LibraryElements.JAR))
  }
}

dependencies {
  implementation("net.kyori:adventure-platform-spongeapi:4.0.0-SNAPSHOT")
  annotationProcessor(shadow("org.spongepowered:spongeapi:7.3.0")!!)
  spongeRunClasspath("org.spongepowered:spongevanilla:1.12.2-7.3.0") { isTransitive = false }
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

  exclude("META-INF/versions/*/module-info.class")
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

val pluginJar = tasks.shadowJar.map { it.outputs }
val spongeRunFiles = spongeRunClasspath.asFileTree
val runSponge7 by tasks.registering(JavaExec::class) {
  group = "adventure"
  description = "Spin up a SpongeVanilla server environment"
  standardInput = System.`in`
  // Sponge on 1.12 is stuck on Java 8 because of LaunchWrapper
  javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(8)) })

  inputs.files(spongeRunClasspath, pluginJar)

  classpath(spongeRunFiles)
  mainClass.set("org.spongepowered.server.launch.VersionCheckingMain")
  workingDir = layout.projectDirectory.dir("run").asFile

  doFirst {
    // Prepare
    val modsDir = workingDir.resolve("mods")
    if (!modsDir.isDirectory) {
      modsDir.mkdirs()
    }

    project.copy {
      into(modsDir.absolutePath)
      from(pluginJar) {
        rename { "${rootProject.name}-${project.name}.jar" }
      }
    }
  }
}

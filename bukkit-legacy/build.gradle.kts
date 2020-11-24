
plugins {
  id("com.github.johnrengelman.shadow")
}

dependencies {
  implementation(project(":test-bukkit"))
  implementation("com.google.code.gson:gson:2.8.6")
}

tasks.shadowJar.configure {
  relocate("com.google.gson", "net.kyori.adventure.test.bukkit.ext.gson")
  dependencies {
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

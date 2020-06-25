
plugins {
  id("com.github.johnrengelman.shadow")
}

dependencies {
  implementation(project(":test-paper"))
  implementation("com.google.code.gson:gson:2.8.6")
}

tasks.shadowJar.configure {
  relocate("com.google.gson", "net.kyori.adventure.test.paper.ext.gson")
  dependencies {
    exclude(dependency("org.checkerframework:.*"))
  }
}

tasks.assemble.configure {
  dependsOn(tasks.shadowJar)
}

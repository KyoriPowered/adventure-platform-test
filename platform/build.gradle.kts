plugins {
  id("com.github.johnrengelman.shadow") // version defined in root project
}

dependencies {
  implementation("net.kyori:adventure-text-minimessage:3.0.0-SNAPSHOT")
  implementation("net.kyori:adventure-api:4.0.0-SNAPSHOT")
}

opinionated {
  mit()
  github("KyoriPowered", "adventure-platform-test")
}

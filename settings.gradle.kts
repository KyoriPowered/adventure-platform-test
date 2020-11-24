rootProject.name = "adventure-platform-test"

listOf("bukkit", "bukkit-legacy", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "test-$it"
}

rootProject.name = "adventure-platform-test"

listOf("paper", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "test-$it"
}

rootProject.name = "adventure-platform-test"

listOf("paper", "paper-legacy", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "test-$it"
}

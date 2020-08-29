rootProject.name = "adventure-platform-test"

listOf("platform", "paper", "paper-legacy", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "test-$it"
}

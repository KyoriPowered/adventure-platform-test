rootProject.name = "adventure-testplugin"

listOf("paper", "sponge", "bungee").forEach {
    include(it)
    findProject(":$it")?.name = "testplugin-$it"
}

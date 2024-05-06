package de.miraculixx.mchallenge.utils

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree

fun command(name: String, tree: CommandTree.() -> Unit = {}) = Command(name, tree)

class Command(
    val name: String,
    private val tree: CommandTree.() -> Unit,
    register: Boolean = true
) {
    fun register() {
        CommandTree(name).apply(tree).register()
    }

    fun unregister() {
        CommandAPI.unregister(name)
    }

    init {
        if (register) register()
    }
}
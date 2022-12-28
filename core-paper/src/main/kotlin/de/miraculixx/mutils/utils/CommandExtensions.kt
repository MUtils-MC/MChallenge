package de.miraculixx.mutils.utils

import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.registerCommand(cmd: String, executor: CommandExecutor) {
    val obj = getCommand(cmd) ?: return
    obj.setExecutor(executor)
    if (executor is TabCompleter) obj.tabCompleter = executor
}
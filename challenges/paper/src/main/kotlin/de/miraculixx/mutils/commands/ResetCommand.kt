package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.messages.cError
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.utils.settings
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ResetCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        settings.set("ResetWorld", true)
        onlinePlayers.forEach { player -> player.sendMessage(cmp("Die Welt wird nun resettet...", cError)) }
        Bukkit.shutdown()
        return true
    }

    init {
        register("reset")
    }
}
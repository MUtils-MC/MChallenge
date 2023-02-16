package de.miraculixx.mutils.modules.mods.huntMob

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class MobHuntCommand(private val data: MobHunt): TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        return when (args?.size ?: 0) {
            0, 1 -> listOf("skip", "reset")
            else -> emptyList()
        }.filter { it.contains(args?.lastOrNull() ?: "") }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when (args?.getOrNull(0)?.lowercase()) {
            "skip" -> data.nextMob(sender.name, sender)
            "reset" -> data.reset()
        }
        return true
    }
}
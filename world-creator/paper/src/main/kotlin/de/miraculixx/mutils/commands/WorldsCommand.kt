package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.actions.GUIWorlds
import de.miraculixx.mutils.utils.items.ItemsWorlds
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class WorldsCommand : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }

        GUITypes.WORLD_OVERVIEW.buildInventory(sender, "${sender.uniqueId}-OVERVIEW", ItemsWorlds(sender.world.uid), GUIWorlds(null))
        sender.playSound(sender, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return mutableListOf()
    }

    init {
        register("worlds")
    }
}
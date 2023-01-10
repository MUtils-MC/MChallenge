package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.actions.GUIMenu
import de.miraculixx.mutils.utils.items.ItemsMenu
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WorldCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() && sender is Player) {
            GUITypes.WORLD_MENU.buildInventory(sender, "WORLD_MENU", ItemsMenu(), GUIMenu())
            return true
        }
        if (!(args.size == 1 && args[0] == "info") && args.size < 2) {
            sender.sendMessage(prefix + msg("command.world.help"))
            return false
        }
        // world create <name> <Environment> <BiomeProvider> <Seed>

        when (args[0]) {
            "tp" -> {

            }

            "delete" -> {

            }

            "info" -> {

            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    init {
        register("world")
    }
}
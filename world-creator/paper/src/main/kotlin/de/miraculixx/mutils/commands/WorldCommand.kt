package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plainSerializer
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
                if (sender !is Player) {
                    sender.sendMessage(msg("command.noPlayer"))
                    return false
                }
                val worldName = args.getOrNull(1)
                val world = worlds.firstOrNull { it.name == worldName }
                if (world == null) {
                    sender.sendMessage(prefix + msg("command.noWorld"))
                    return false
                }
                sender.teleport(world.spawnLocation)
            }

            "delete" -> {

            }

            "info" -> {

            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return buildList {
            when (args.size) {
                0,1 -> addAll(listOf("tp", "delete", "info"))
                2 -> worlds.map { it.name }
            }
        }.toMutableList()
    }

    init {
        register("world")
    }
}
package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mutils.await.AwaitConfirm
import de.miraculixx.api.data.printInfo
import de.miraculixx.mvanilla.extensions.soundDelete
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.actions.GUIBuilderType
import de.miraculixx.mutils.utils.actions.GUIMenu
import de.miraculixx.mutils.utils.checkPermission
import de.miraculixx.mutils.utils.items.ItemsBuilderType
import de.miraculixx.mutils.utils.items.ItemsMenu
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WorldCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() && sender is Player) {
            if (!sender.checkPermission("mutils.command.world")) return false
            GUITypes.WORLD_MENU.buildInventory(sender, "WORLD_MENU", ItemsMenu(), GUIMenu())
            return true
        }

        when (args[0]) {
            "tp" -> {
                if (sender !is Player) {
                    sender.sendMessage(msg("command.noPlayer"))
                    return false
                }
                if (!sender.checkPermission("mutils.event.tp")) return false
                val world = sender.getWorld(args.getOrNull(1)) ?: return false
                sender.teleport(world.spawnLocation)
            }

            "delete" -> {
                if (!sender.checkPermission("mutils.event.delete")) return false
                val world = sender.getWorld(args.getOrNull(1)) ?: return false
                if (sender is Player) AwaitConfirm(sender, {
                    WorldManager.deleteWorld(world.uid)
                    sender.soundDelete()
                }) { sender.soundDisable() }
            }

            "info" -> {
                if (!sender.checkPermission("mutils.command.info")) return false
                val worldData = if (args.size == 1 && sender is Player) WorldManager.getWorldData(sender.world.uid)
                else {
                    val world = sender.getWorld(args.getOrNull(1)) ?: return false
                    WorldManager.getWorldData(world.uid)
                }
                if (worldData == null) {
                    sender.sendMessage(prefix + msg("command.noWorld"))
                    return false
                }
                sender.sendMessage(worldData.printInfo(false))
            }

            "create" -> {
                if (sender !is Player) {
                    sender.sendMessage(msg("command.noPlayer"))
                    return false
                }
                if (!sender.checkPermission("mutils.event.create")) return false
                GUITypes.WORLD_CREATOR_TYPE.buildInventory(sender, "${sender.uniqueId}-TYPE", ItemsBuilderType(), GUIBuilderType())
                sender.playSound(sender, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return buildList {
            when (args.size) {
                0, 1 -> addAll(listOf("tp", "delete", "info"))
                2 -> {
                    when (args[0]) {
                        "info", "delete", "tp" -> addAll(worlds.map { it.name })
                    }
                }
            }
        }.filter { args.lastOrNull()?.let { tab -> it.startsWith(tab, ignoreCase = true) } ?: true }.toMutableList()
    }

    private fun CommandSender.getWorld(worldName: String?): World? {
        val world = worlds.firstOrNull { it.name == worldName }
        return if (world == null) {
            sendMessage(prefix + msg("command.noWorld"))
            null
        } else world
    }

    init {
        register("world")
    }
}
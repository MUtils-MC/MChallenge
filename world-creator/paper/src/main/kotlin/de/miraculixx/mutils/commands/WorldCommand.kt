package de.miraculixx.mutils.commands

import de.miraculixx.challenge.api.data.WorldData
import de.miraculixx.challenge.api.data.printInfo
import de.miraculixx.mcore.await.AwaitConfirm
import de.miraculixx.mutils.data.toDimension
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.actions.GUIBuilderType
import de.miraculixx.mutils.utils.actions.GUIMenu
import de.miraculixx.mutils.utils.items.ItemsBuilderType
import de.miraculixx.mutils.utils.items.ItemsMenu
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.jvm.optionals.getOrNull

class WorldCommand {
    private val onCommand = commandTree("world") {
        withPermission("mutils.command.world")
        playerExecutor { player, _ ->
            player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 0.7f, 1f)
            GUITypes.WORLD_MENU.buildInventory(player, "WORLD_MENU", ItemsMenu(), GUIMenu())
        }

        literalArgument("tp") {
            withPermission("mutils.event.tp")
            worldArgument("world") {
                playerExecutor { player, args ->
                    val world = args[0] as World
                    player.teleportAsync(world.spawnLocation)
                }
            }
        }

        literalArgument("info") {
            withPermission("mutils.command.info")
            worldArgument("world", true) {
                anyExecutor { sender, args ->
                    val world = args.getWorld(0, sender) ?: return@anyExecutor
                    val worldData = WorldManager.getWorldData(world.uid) ?: WorldData("Vanilla", world.name, seed = world.seed, environment = world.environment.toDimension())
                    sender.sendMessage(worldData.printInfo(false))
                }
            }
        }

        literalArgument("delete") {
            withPermission("mutils.event.delete")
            worldArgument("world", true) {
                anyExecutor { sender, args ->
                    val world = args.getWorld(0, sender) ?: return@anyExecutor
                    if (sender is Player) {
                        AwaitConfirm(sender, {
                            WorldManager.deleteWorld(world.uid)
                            sender.soundDisable()
                        }) { sender.soundDisable() }
                    } else WorldManager.deleteWorld(world.uid)
                    sender.sendMessage(prefix + msg("command.worldDeleted", listOf(world.name)))
                }
            }
        }

        literalArgument("create") {
            withPermission("mutils.event.create")
            playerExecutor { player, _ ->
                GUITypes.WORLD_CREATOR_TYPE.buildInventory(player, "${player.uniqueId}-TYPE", ItemsBuilderType(), GUIBuilderType())
                player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
            }
        }
    }

    private fun CommandArguments.getWorld(index: Int, sender: CommandSender): World? {
        var world = getOptional(0).getOrNull() as? World
        if (world == null) {
            if (sender !is Player) {
                sender.sendMessage(prefix + msg("command.noWorld"))
                return null
            } else world = sender.world
        }
        return world
    }
}
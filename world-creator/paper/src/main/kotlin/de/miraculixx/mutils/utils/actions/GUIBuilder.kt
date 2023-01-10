package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.mutils.await.AwaitChatMessage
import de.miraculixx.mutils.data.BiomeAlgorithm
import de.miraculixx.mutils.data.WorldData
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsNoiseAlgos
import de.miraculixx.mutils.utils.items.ItemsNoiseSettings
import org.bukkit.Sound
import org.bukkit.World.Environment
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class GUIBuilder(worldData: WorldData) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (val id = item.itemMeta?.customModel) {
            1 -> {
                player.click()
                AwaitChatMessage(false, player, "World Name", 60, worldData.worldName, {
                    worldData.worldName = it.replace(' ', '_')
                    player.soundUp()
                }) {
                    inv.update()
                    inv.open(player)
                }
                return@event
            }

            2 -> {
                player.click()
                AwaitChatMessage(false, player, "Seed", 60, worldData.seed.toString(), {
                    try {
                        worldData.seed = it.toLong(36)
                    } catch (_: Exception) {
                        player.soundError()
                        player.sendMessage(prefix + cmp("Your input is not a valid seed representation!", cError))
                    }
                }) {
                    inv.update()
                    inv.open(player)
                }
                return@event
            }

            3 -> {
                val new = arrayOf(Environment.NORMAL, Environment.NETHER, Environment.THE_END).enumRotate(worldData.environment)
                worldData.environment = new
                player.click()
            }

            4 -> {
                if (worldData.generateStructures) {
                    player.soundDisable()
                    worldData.generateStructures = false
                } else {
                    player.soundEnable()
                    worldData.generateStructures = true
                }
            }

            5 -> {
                val new = WorldType.values().enumRotate(worldData.worldType)
                worldData.worldType = new
                player.click()
            }

            6 -> {
                val new = BiomeAlgorithm.values().enumRotate(worldData.biomeProvider.algorithm)
                worldData.biomeProvider.algorithm = new
                player.click()
            }

            7 -> {
                GUITypes.WORLD_CREATOR_ALGOS.buildInventory(player, "WORLD_CREATOR_NOISE", ItemsNoiseAlgos(), GUINoiseAlgo(inv, worldData))
                player.click()
            }

            in 100..105 -> {
                val index = id?.minus(100) ?: return@event
                val data = worldData.chunkProviders.getOrNull(index) ?: return@event
                GUITypes.WORLD_CREATOR_SETTINGS.buildInventory(player, "${player.uniqueId}-SETTINGS", ItemsNoiseSettings(data), GUIMenu())
                player.click()
            }

            8 -> {
                player.closeInventory()
                player.playSound(player, Sound.BLOCK_CONDUIT_ACTIVATE, 1f, 0.8f)
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
                val name = worldData.worldName
                player.title(msg("event.newWorld", listOf(name)), msg("event.newWorldSub", listOf(name)), 0.5.seconds, 1.hours)

                val world = WorldManager.createWorld(worldData)
                if (world == null) {
                    player.soundError()
                    inv.open(player)
                    return@event
                }

                async {
                    player.sendMessage(
                        miniMessages.deserialize(
                            "<grey><gold>┐</gold> <green>New world successfully created!</green>\n" +
                                    "<gold>├></gold> ${msgString("event.name")} ≫ <blue>${world.name}</blue>\n" +
                                    "<gold>├></gold> ${msgString("event.seed")} ≫ <blue>${world.seed}</blue>\n" +
                                    "<gold>├></gold> ${msgString("event.dimension")} ≫ <blue>${msgString("event.${world.environment.name}")}</blue>\n" +
                                    "<gold>├></gold> ${msgString("event.type")} ≫ <blue>${msgString("event.${worldData.worldType.name}")}</blue>\n" +
                                    "<gold>├></gold> ${msgString("event.biomeProvider")} ≫ <blue><hover:show_text:'<red>Settings TODO'>${
                                        msgString("items.algo.${worldData.biomeProvider.algorithm.name}.n")
                                    }</hover></blue>\n" +
                                    "<gold>└></gold> ${msgString("event.noiseProvider")} ≫ <blue><hover:show_text:'${
                                        buildString hover@{
                                            worldData.chunkProviders.forEach { cp ->
                                                val gen = cp.generator
                                                append(
                                                    "<grey>- <blue>${msgString("items.creator.${gen.name}.n")}</blue> (${
                                                        buildString setting@{
                                                            cp.x1?.let { append("$it, ") }
                                                            cp.x2?.let { append("$it, ") }
                                                            cp.x3?.let { append("$it, ") }
                                                            cp.rnd?.let { append("$it, ") }
                                                            cp.invert?.let { append(it) }
                                                        }.removeSuffix(", ")
                                                    })</grey>"
                                                )
                                            }
                                        }
                                    }'>[2 Rules]</hover></blue>"
                        )
                    )
                    player.soundEnable()
                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                    player.title(emptyComponent(), emptyComponent())
                    player.teleport(world.spawnLocation)
                }
                return@event
            }

            else -> return@event
        }
        inv.update()
    }
}
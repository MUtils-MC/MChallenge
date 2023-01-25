package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.await.AwaitChatMessage
import de.miraculixx.mutils.data.WorldData
import de.miraculixx.mutils.data.enums.BiomeAlgorithm
import de.miraculixx.mutils.data.enums.Dimension
import de.miraculixx.mutils.data.enums.VanillaGenerator
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.MapRender
import de.miraculixx.mutils.module.WorldDataHandling
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsNoiseAlgos
import de.miraculixx.mutils.utils.items.ItemsNoiseSettings
import de.miraculixx.mutils.utils.items.ItemsNoiseVanilla
import de.miraculixx.mutils.utils.items.ItemsWorlds
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class GUIBuilder(worldData: WorldData, isSet: Boolean) : GUIEvent {
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
                AwaitChatMessage(false, player, msgString("event.category"), 60, worldData.seed.toString(), {
                    worldData.category = it
                }) {
                    inv.update()
                    inv.open(player)
                }
                return@event
            }

            3 -> {
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

            10 -> {
                val new = Dimension.values().enumRotate(worldData.environment)
                worldData.environment = new
                player.click()
            }

            5 -> {
                val new = VanillaGenerator.values().enumRotate(worldData.worldType)
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
                val click = it.click
                if (click == ClickType.LEFT) {
                    GUITypes.WORLD_CREATOR_SETTINGS.buildInventory(player, "${player.uniqueId}-SETTINGS", ItemsNoiseSettings(data), GUINoiseSettings(data, inv))
                    player.click()
                } else if (click == ClickType.SHIFT_RIGHT) {
                    worldData.chunkProviders.remove(data)
                    player.soundDelete()
                }
            }

            8 -> {
                val click = it.click
                if (click == ClickType.RIGHT) {
                    player.click()
                    MapRender(player, inv, worldData.chunkProviders)

                } else if (click == ClickType.LEFT) {

                    if (isSet) {
                        if (worldData.seed == null) worldData.seed = Random.nextLong()
                        task(true, 0, 20, 3, endCallback = {
                            GUITypes.WORLD_OVERVIEW.buildInventory(player, "${player.uniqueId}-OVERVIEW", ItemsWorlds(player.uniqueId), GUIWorlds())
                        }) {
                            when (it.counterDownToZero) {
                                2L -> {
                                    val overworldData = worldData.copy(environment = Dimension.NORMAL)
                                    if (overworldData.createWorld(player, inv) == null) it.cancel()
                                }
                                1L -> {
                                    val netherData = worldData.copy(worldName = "${worldData.worldName}_nether", environment = Dimension.NETHER)
                                    if (netherData.createWorld(player, inv) == null) it.cancel()
                                }
                                0L -> {
                                    val endData = worldData.copy(worldName = "${worldData.worldName}_the_end", environment = Dimension.THE_END)
                                    if (endData.createWorld(player, inv) == null) it.cancel()
                                }
                            }
                        }
                    } else {
                        val world = worldData.createWorld(player, inv) ?: return@event
                        player.teleport(world.spawnLocation)
                    }
                    return@event
                }
            }

            9 -> {
                GUITypes.WORLD_CREATOR_SETTINGS.buildInventory(player, "${player.uniqueId}-VANILLA", ItemsNoiseVanilla(worldData.chunkDefaults), GUINoiseVanilla(worldData.chunkDefaults, inv))
                player.click()
            }

            else -> return@event
        }
        inv.update()
    }

    private fun WorldData.createWorld(player: Player, oldInventory: CustomInventory): World? {
        val name = worldName
        if (worlds.firstOrNull { it.name == name } != null) {
            player.soundError()
            player.sendMessage(prefix + cmp("command.nameUsed"))
            oldInventory.open(player)
            return null
        }

        player.closeInventory()
        player.playSound(player, Sound.BLOCK_CONDUIT_ACTIVATE, 1f, 0.8f)
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
        player.title(msg("event.newWorld", listOf(name)), msg("event.newWorldSub", listOf(name)), 0.5.seconds, 1.hours)

        val worldID = WorldManager.createWorld(this)
        val world = worldID?.let { Bukkit.getWorld(it) }
        if (world == null) {
            player.soundError()
            oldInventory.open(player)
            return null
        }

        player.removePotionEffect(PotionEffectType.BLINDNESS)

        async {
            WorldDataHandling.setCategory(world, category)
            player.sendMessage(
                miniMessages.deserialize(
                    "<grey><gold>┐</gold> <green>New world successfully created!</green>\n" +
                            "<gold>├></gold> ${msgString("event.name")} ≫ <blue>${world.name}</blue>\n" +
                            "<gold>├></gold> ${msgString("event.category")} ≫ <blue>${category}</blue>\n" +
                            "<gold>├></gold> ${msgString("event.seed")} ≫ <blue>${world.seed}</blue>\n" +
                            "<gold>├></gold> ${msgString("event.dimension")} ≫ <blue>${msgString("event.env.${world.environment.name}")}</blue>\n" +
                            "<gold>├></gold> ${msgString("event.type")} ≫ <blue>${msgString("event.gen.${worldType.name}")}</blue>\n" +
                            "<gold>├></gold> ${msgString("event.biomeProvider")} ≫ <blue><hover:show_text:'<red>Settings TODO'>${
                                msgString("items.algo.${biomeProvider.algorithm.name}.n")
                            }</hover></blue>\n" +
                            "<gold>└></gold> ${msgString("event.noiseProvider")} ≫ <blue><hover:show_text:'${
                                buildString hover@{
                                    chunkProviders.forEach { cp ->
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
                                            })</grey>\n"
                                        )
                                    }
                                }
                            }'>[2 Rules]</hover></blue>"
                )
            )
            player.soundEnable()
            player.title(emptyComponent(), emptyComponent())
        }
        return world
    }
}
package de.miraculixx.mutils.modules.mods.dimSwap

import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.mutils.messages.*
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.time.Duration

/**
 * Disclaimer - The following code is awful and I know that. But it works. Never change a running system
 */
class DimSwapSchedule {

    fun worldGen(): Boolean {
        val normal = worlds[0]
        val end = Bukkit.getWorld("${normal.name}_the_end")
        val prepareString = cmp("Prepare all worlds...", cHighlight)
        var i = 0
        var progress = 0
        var high = 0
        if (end == null) {
            broadcast(prefix + msg("event.worldNotFound", listOf("${normal.name}_the_end")))
            return false
        }
        val timings = Title.Times.times(Duration.ZERO, Duration.ofHours(1), Duration.ZERO)
        task(true, 2, 2) {
            if (i == 3) {
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("Prepare End", cError), timings))
                }
                for (x in -4..4) {
                    for (z in -4..4) {
                        end.loadChunk(x, z)
                    }
                }
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("End Loaded", cError), timings))
                }
            }
            if (i == 5) {
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("Prepare Overworld", cError), timings))
                }
                for (x in -4..4) {
                    for (z in -4..4) {
                        normal.loadChunk(x, z)
                    }
                }
                high = normal.getHighestBlockYAt(0, 0) - end.getHighestBlockYAt(0, 0)
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("Overworld Loaded", cError), timings))
                }
            }
            if (i == 6) {
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("Stabilize TPS (< 16TPS)", cError), timings))
                }
            }
            if (i in 11..498) { //50x110x50 -> 1x per Tick, 110y per Tick, 50z per Tick -> 5500 Blocks per Tick
                if (progress > 100) {
                    //Ready -> skip remaining ticks
                    i = 499
                    return@task
                }
                for (player in onlinePlayers) {
                    player.showTitle(Title.title(prepareString, cmp("Dragon Battle generating ($progress%)", cError), timings))
                }
                for (y in 0..110) {
                    for (z in -50..50) {
                        val x = progress - 50
                        val endBlock = end.getBlockAt(x, y, z)
                        if (endBlock.type == Material.OBSIDIAN || endBlock.type == Material.BEDROCK || endBlock.type == Material.IRON_BARS) {
                            normal.getBlockAt(x, y + high, z).type = endBlock.type
                            normal.getBlockAt(x, y + high, z).blockData = endBlock.blockData
                            if (y < 70 && endBlock.type == Material.BEDROCK) continue
                            if (endBlock.type == Material.BEDROCK) {
                                for (entity in endBlock.chunk.entities) {
                                    entity.remove()
                                }
                                normal.spawnEntity(endBlock.location.add(0.5, (1 + high).toDouble(), 0.5), EntityType.ENDER_CRYSTAL)
                            }
                            end.getBlockAt(x, y, z).type = Material.END_STONE
                        }
                    }
                }
                progress += 1
            }
            if (i == 500) {
                //Spawne Endplattform
                for (player in Bukkit.getOnlinePlayers()) {
                    player.showTitle(Title.title(prepareString, cmp("Dragon Battle generating (Structures)", cError), timings))
                }
                val y = normal.getHighestBlockYAt(0, 0)
                for (j in 0..4) normal.getBlockAt(0, y + j, 0).type = Material.BEDROCK
                for (j in -2..2) {
                    for (l in -2..2) {
                        normal.getBlockAt(j, y, l).type = Material.BEDROCK
                    }
                }
                for (j in -1..1) normal.getBlockAt(-3, y + 1, j).type = Material.BEDROCK
                for (j in -1..1) normal.getBlockAt(3, y + 1, j).type = Material.BEDROCK
                for (j in -1..1) normal.getBlockAt(j, y + 1, -3).type = Material.BEDROCK
                for (j in -1..1) normal.getBlockAt(j, y + 1, 3).type = Material.BEDROCK
                normal.getBlockAt(-2, y + 1, 2).type = Material.BEDROCK
                normal.getBlockAt(-2, y + 1, -2).type = Material.BEDROCK
                normal.getBlockAt(2, y + 1, 2).type = Material.BEDROCK
                normal.getBlockAt(2, y + 1, -2).type = Material.BEDROCK
            }
            if (i == 505) for (player in Bukkit.getOnlinePlayers()) {
                if (player.gameMode == GameMode.SPECTATOR) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                    player.showTitle(Title.title(emptyComponent(), emptyComponent(), Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ZERO)))
                    continue
                }
                schedule(player, end, prepareString, timings)
                it.cancel()
                return@task
            }
            i += 1
        }
        return true
    }

    private fun schedule(player: Player, end: World, prepareString: Component, timings: Title.Times) {
        val loc: Location = player.location
        var i = -3
        task(true, 2, 2) {
            when (i) {
                0 -> {
                    player.showTitle(Title.title(prepareString, cmp("Search End Spawn", cError), timings))
                    player.gameMode = GameMode.CREATIVE
                    player.teleport(end.getHighestBlockAt(0, 0).location.add(0.0, 10.0, 0.0))
                    player.isFlying = true
                }

                2 -> {
                    end.enderDragonBattle?.enderDragon?.remove()
                    end.enderDragonBattle?.bossBar?.isVisible = false
                    end.enderDragonBattle?.bossBar?.removeAll()
                    end.enderDragonBattle?.generateEndPortal(true)
                    for (entity in end.entities) {
                        if (entity is Player) continue
                        entity.remove()
                    }
                }

                3 -> player.teleport(loc)
                6 -> {
                    player.showTitle(Title.title(prepareString, cmp("Search Nether Spawn", cError), timings))
                    player.teleport(Location(loc.world, loc.blockX.toDouble(), 2.0, loc.blockZ.toDouble()))
                }

                8 -> loc.world?.getBlockAt(loc.blockX, 2, loc.blockZ)?.type = Material.NETHER_PORTAL
                10 -> {
                    loc.world?.getBlockAt(loc.blockX, 2, loc.blockZ)?.type = Material.STONE
                    val loc1: Location = player.location
                    var y = -10
                    while (y < 10) {
                        var x = -10
                        while (x < 10) {
                            var z = -10
                            while (z < 10) {
                                val block = Location(loc1.world, loc1.x + x, loc1.y + y, loc1.z + z).block
                                if (block.type == Material.OBSIDIAN || block.type == Material.NETHER_PORTAL) {
                                    if (block.location.y < loc1.y) {
                                        block.type = Material.NETHERRACK
                                    } else {
                                        block.type = Material.AIR
                                    }
                                }
                                player.stopSound(Sound.BLOCK_GLASS_BREAK)
                                ++z
                            }
                            ++x
                        }
                        ++y
                    }
                    for (entity in player.world.entities) {
                        if (entity is Player) continue
                        entity.remove()
                    }
                    player.gameMode = GameMode.SURVIVAL
                    player.removePotionEffect(PotionEffectType.BLINDNESS)
                    player.showTitle(Title.title(emptyComponent(), emptyComponent(), Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ZERO)))
                    if (challenges.getSetting(Challenges.DIM_SWAP).settings["starter"]?.toBool()?.getValue() == true) {
                        val item = itemStack(Material.WOODEN_PICKAXE) {
                            meta {
                                name = cmp("Starter Wooden Pickaxe", NamedTextColor.WHITE)
                                isUnbreakable = true
                            }
                        }
                        onlinePlayers.forEach { op ->
                            op.inventory.addItem(item)
                        }
                    }
                }

                11 -> {
                    it.cancel()
                    return@task
                }
            }
            ++i
        }
    }
}
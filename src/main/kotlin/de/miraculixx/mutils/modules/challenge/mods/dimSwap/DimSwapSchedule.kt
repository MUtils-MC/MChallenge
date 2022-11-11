package de.miraculixx.mutils.modules.challenge.mods.dimSwap

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class DimSwapSchedule {

    fun worldGen(): Boolean {
        val normal = worlds[0]
        val end = Bukkit.getWorld("${normal.name}_the_end")
        val prepareString = msg("module.challenge.dimSwap.prepare", pre = false)
        var i = 0
        var progress = 0
        var high = 0
        if (end == null) {
            broadcast(msg("module.challenge.worldNotFound", input = "${normal.name}_the_end"))
            return false
        }
        task(true, 2, 2) {
            if (i == 3) {
                for (player in onlinePlayers) {
                    player.sendTitle(prepareString, "§cPrepare End world", 0, 9999, 0)
                }
                for (x in -4..4) {
                    for (z in -4..4) {
                        end.loadChunk(x, z)
                    }
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendTitle(prepareString, "§cEnd world loaded", 0, 9999, 0)
                }
            }
            if (i == 5) {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendTitle(prepareString, "§ccPrepare Overworld", 0, 9999, 0)
                }
                for (x in -4..4) {
                    for (z in -4..4) {
                        normal.loadChunk(x, z)
                    }
                }
                high = normal.getHighestBlockYAt(0, 0) - end.getHighestBlockYAt(0, 0)
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendTitle(prepareString, "§cOverworld loaded", 0, 9999, 0)
                }
            }
            if (i == 6) {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendTitle(prepareString, "§cStabilize TPS (< 16TPS)", 0, 9999, 0)
                }
            }
            if (i in 11..498) { //50x110x50 -> 1x per Tick, 110y per Tick, 50z per Tick -> 5500 Blocks per Tick
                if (progress > 100) {
                    //Ready -> skip remaining ticks
                    i = 499
                    return@task
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendTitle(prepareString, "§cDragon Battle generating ($progress%)", 0, 9999, 0)
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
                    player.sendTitle(prepareString, "§cDragon Battle generating (Structures)", 0, 9999, 0)
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
                    player.sendTitle("", "", 0, 0, 0)
                    continue
                }
                schedule(player, end, prepareString)
                it.cancel()
                return@task
            }
            i += 1
        }
        return true
    }

    private fun schedule(p: Player, end: World, prepareString: String) {
        val loc: Location = p.location
        var i = -3
        task(true, 2, 2) {
            when (i) {
                0 -> {
                    p.sendTitle(prepareString, "§cSearch End Spawn", 0, 9999, 0)
                    p.gameMode = GameMode.CREATIVE
                    p.teleport(end.getHighestBlockAt(0, 0).location.add(0.0, 10.0, 0.0))
                    p.isFlying = true
                }
                2 -> {
                    try {
                        end.enderDragonBattle!!.enderDragon!!.remove()
                    } catch (exception: NullPointerException) {
                        end.enderDragonBattle?.bossBar?.isVisible = false
                        end.enderDragonBattle?.bossBar?.removeAll()
                        end.enderDragonBattle?.generateEndPortal(true)
                        for (entity in end.entities) {
                            if (entity is Player) continue
                            entity.remove()
                        }
                    }
                    end.enderDragonBattle?.bossBar?.isVisible = false
                    end.enderDragonBattle?.bossBar?.removeAll()
                    end.enderDragonBattle?.generateEndPortal(true)
                    for (entity in end.entities) {
                        if (entity is Player) continue
                        entity.remove()
                    }
                }
                3 -> p.teleport(loc)
                6 -> {
                    p.sendTitle(prepareString, "§cSearch Nether Spawn", 0, 9999, 0)
                    p.teleport(Location(loc.world, loc.blockX.toDouble(), 2.0, loc.blockZ.toDouble()))
                }
                8 -> loc.world!!.getBlockAt(loc.blockX, 2, loc.blockZ).type = Material.NETHER_PORTAL
                10 -> {
                    loc.world!!.getBlockAt(loc.blockX, 2, loc.blockZ).type = Material.STONE
                    val loc1: Location = p.location
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
                                p.stopSound(Sound.BLOCK_GLASS_BREAK)
                                ++z
                            }
                            ++x
                        }
                        ++y
                    }
                    for (entity in p.world.entities) {
                        if (entity is Player) continue
                        entity.remove()
                    }
                    p.gameMode = GameMode.SURVIVAL
                    p.removePotionEffect(PotionEffectType.BLINDNESS)
                    p.sendTitle(" ", " ", 1, 1, 1)
                    val config = ConfigManager.getConfig(Configs.MODULES)
                    if (config.getBoolean("DIM_SWAP.Pickaxe")) {
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
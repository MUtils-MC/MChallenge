@file:Suppress("DEPRECATION")

package de.miraculixx.mutils.utils.tools.gui.items

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.addLines
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemsWorld {

    fun getItems(id: Int, player: Player? = null, c: FileConfiguration? = null): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(player)
            2 -> g2(c!!)
            else -> {
                linkedMapOf(Pair(ItemStack(Material.BARRIER), false))
            }
        }
        /* ID Glossary
        1 -> World Overview
        2 -> Global World Settings
         */
        return list
    }

    private fun g3(bp: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        return linkedMapOf(

        )
    }

    private fun g2(c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        val bp = ConfigManager.getConfig(Configs.BACKPACK)
        val l = listOf(" ", "§9Click §7≫ Toggle")

        return linkedMapOf(
            itemStack(Material.ENDER_CHEST) {
                meta {
                    customModel = 100
                    name = "§9§lBackpack"
                    lore = listOf(
                        " ", "§7∙ §9§nSettings",
                        "   §7Global: §9${bp.getBoolean("Global Backpack")}",
                        "   §7Size: §9${bp.getInt("Backpack Size")}",
                        " ", "§9Right Click §7≫ Settings", "§9Left Click §7≫ Toggle"
                    )
                }
            } to c.getBoolean("Global.Backpack"),
            itemStack(Material.BEETROOT) {
                meta {
                    customModel = 101
                    name = "§9§lHearts in Tablist"
                    lore = l
                }
            } to c.getBoolean("Global.HeartsTab"),
            itemStack(Material.TOTEM_OF_UNDYING) {
                meta {
                    customModel = 102
                    name = "§9§lHardcore"
                    lore = l
                }
            } to c.getBoolean("Global.Hardcore"),
            itemStack(Material.IRON_SWORD) {
                meta {
                    customModel = 103
                    name = "§9§lForced Difficulty"
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    lore = listOf(
                        " ", "§7∙ §9§nSettings",
                        "   §7Difficulty: §9${c.getString("Global.DifficultyMode")}",
                        " ", "§9Right Click §7≫ Change Mode", "§9Left Click §7≫ Toggle"
                    )
                }
            } to c.getBoolean("Global.Difficulty"),
            itemStack(Material.CLOCK) {
                meta {
                    customModel = 104
                    name = "§9§lTime Freeze"
                    lore = l
                }
            } to c.getBoolean("Global.TimeFreeze"),
            itemStack(Material.SUNFLOWER) {
                meta {
                    customModel = 105
                    name = "§9§lForced Weather"
                    lore = listOf(
                        " ", "§7∙ §9§nSettings",
                        "   §7Weather: §9${c.getString("Global.WeatherMode")}",
                        " ", "§9Right Click §7≫ Change Mode", "§9Left Click §7≫ Toggle"
                    )
                }
            } to c.getBoolean("Global.Weather"),
            itemStack(Material.WHITE_WOOL) {
                meta {
                    customModel = 106
                    name = "§9§lFall Damage"
                    lore = l
                }
            } to c.getBoolean("Global.FallDamage"),
            itemStack(Material.MAGMA_BLOCK) {
                meta {
                    customModel = 107
                    name = "§9§lFire Damage"
                    lore = l
                }
            } to c.getBoolean("Global.FireDamage"),
            itemStack(Material.POTION) {
                meta {
                    customModel = 108
                    name = "§9§lDrowning Damage"
                    lore = l
                }
            } to c.getBoolean("Global.DrowningDamage"),
            itemStack(Material.SNOW_BLOCK) {
                meta {
                    customModel = 109
                    name = "§9§lFreeze Damage"
                    lore = l
                }
            } to c.getBoolean("Global.FreezeDamage"),
            itemStack(Material.BOOK) {
                meta {
                    customModel = 110
                    name = "§9§lAnnounce Advancements"
                    lore = l
                }
            } to c.getBoolean("Global.Advancements"),
            itemStack(Material.WRITABLE_BOOK) {
                meta {
                    customModel = 111
                    name = "§9§lAnnounce Deaths"
                    lore = l
                }
            } to c.getBoolean("Global.Deaths"),
            itemStack(Material.CHEST) {
                meta {
                    customModel = 112
                    name = "§9§lKeep Inventory on Death"
                    lore = l
                }
            } to c.getBoolean("Global.KeepInv"),
            itemStack(Material.IRON_AXE) {
                meta {
                    customModel = 113
                    name = "§9§lRaids"
                    lore = l
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                }
            } to c.getBoolean("Global.Raids"),
            itemStack(Material.FLINT_AND_STEEL) {
                meta {
                    customModel = 114
                    name = "§9§lFire Tick (Block Damage)"
                    lore = l
                }
            } to c.getBoolean("Global.FireTick"),
            itemStack(Material.BOOK) {
                meta {
                    customModel = 115
                    name = "§9§lInstant Respawn"
                    lore = l
                }
            } to c.getBoolean("Global.InstantRespawn"),
            itemStack(Material.PHANTOM_MEMBRANE) {
                meta {
                    customModel = 116
                    name = "§9§lNatural Phantoms"
                    lore = l
                }
            } to c.getBoolean("Global.Phantoms"),
            itemStack(Material.TNT) {
                meta {
                    customModel = 117
                    name = "§9§lMob Block Damage"
                    lore = l
                }
            } to c.getBoolean("Global.MobDamage"),
            itemStack(Material.CLOCK) {
                meta {
                    customModel = 118
                    name = "§9§lTick Speed"
                    lore = TODO()
                }
            } to c.getBoolean("Global.TickSpeed"),
            itemStack(Material.BOOKSHELF) {
                meta {
                    customModel = 119
                    name = "§9§lF3 World Information"
                    lore = l
                }
            } to c.getBoolean("Global.F3"),
        )
    }

    private fun g1(player: Player?): LinkedHashMap<ItemStack, Boolean> {
        val l = arrayListOf(" ", "§7∙ §9§nWorld Info")
        val l2 = arrayListOf(" ", "§9Left click §7≫ Teleport", "§9Shift right click §7≫ Delete World")
        val list = LinkedHashMap<ItemStack, Boolean>()
        for ((i, world) in worlds.withIndex()) {
            val inWorld = if (player != null) world.players.contains(player)
            else false
            val env = world.environment
            val material = when (world.environment) {
                World.Environment.NORMAL -> Material.GRASS_BLOCK
                World.Environment.NETHER -> Material.NETHERRACK
                World.Environment.THE_END -> Material.END_STONE
                else -> Material.BARRIER
            }
            val wName = world.name
            val loc = world.spawnLocation
            val locS = "${loc.blockX} ${loc.blockY} ${loc.blockZ}"
            val item = itemStack(material) {
                meta {
                    customModel = i
                    name = "§9$wName"
                    lore = ArrayList<String>(l).addLines(
                        "   §7Name: §9$wName",
                        "   §7Environment: §9${env.name}",
                        "   §7Seed: §9${world.seed}",
                        "   §7Difficulty: §9${world.difficulty.name}",
                        "   §7Spawn Point: §9$locS",
                        "   §7Player: §9${world.playerCount}",
                    ).addLines(l2)
                }
            }
            list[item] = inWorld
        }
        return list
    }
}
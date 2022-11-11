package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack

class ItemsTimerSettings {
    fun getItems(id: Int, c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(c)
            2 -> g2(c)
            else -> {
                linkedMapOf(Pair(ItemStack(Material.BARRIER), false))
            }
        }
        /* ID Glossary
        1 -> Goals
        2 -> Rules
         */
        return list
    }

    private fun g1(c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        val l = getMessageList("item.Timer.MobDeath.l", "   ")
        val l2 = listOf(" ", "§7∙ §9§nInfo")
        return linkedMapOf(
            Pair(itemStack(Material.DRAGON_EGG) {
                meta {
                    customModel = 101
                    name = "§9Dragon Death"
                    lore = l2.toMutableList().toMutableList().addLines(
                        l[0].replace("<INPUT>", "Ender Dragon"), l[1], " ",
                        "§9Click§7 ≫ Toggle Goal"
                    )
                }
            }, c.getBoolean("Goals.Dragon")),
            Pair(itemStack(Material.WITHER_ROSE) {
                meta {
                    customModel = 102
                    name = "§9Wither Death"
                    lore = l2.toMutableList().addLines(
                        l[0].replace("<INPUT>", "Wither"), l[1], " ",
                        "§9Click§7 ≫ Toggle Goal"
                    )
                }
            }, c.getBoolean("Goals.Wither")),
            Pair(itemStack(Material.PRISMARINE_CRYSTALS) {
                meta {
                    customModel = 104
                    name = "§9Elder Guardian Death"
                    lore = l2.toMutableList().addLines(
                        l[0].replace("<INPUT>", "Elder Guardian"), l[1], " ",
                        "§9Click§7 ≫ Toggle Goal"
                    )
                }
            }, c.getBoolean("Goals.Elder Guardian")),
            Pair(itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 103
                    name = "§9Player Death"
                    lore = l2.toMutableList().addLines(
                        l[0].replace("<INPUT>", "Player"), l[1], " ",
                        " ", "§7∙ §9§nSettings",
                        "   §7Vanilla Respawn: §9${c.getBoolean("Goals.Player Death Vanilla")}", " ",
                        "§9Left Click§7 ≫ Toggle Goal",
                        "§9Right Click§7 ≫ Toggle Instant Respawn"
                    )
                }
            }, c.getBoolean("Goals.Player Death")),
            Pair(itemStack(Material.STRUCTURE_VOID) {
                meta {
                    customModel = 105
                    name = "§9Last Player Leave"
                    lore = mutableListOf(" ", "§7∙ §9§nInfo")
                        .addLines(getMessageList("item.Timer.LastLeave.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Toggle Goal")
                }
            }, c.getBoolean("Goals.Empty Server"))
        )
    }

    private fun g2(c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        val l = listOf(" ", "§7∙ §9§nInfo")
        return linkedMapOf(
            Pair(itemStack(Material.KNOWLEDGE_BOOK) {
                meta {
                    customModel = 9
                    name = "§9Announce Seed"
                    lore = l.toMutableList().addLines(getMessageList("item.Timer.SendSeed.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Toggle")
                }
            }, c.getBoolean("Settings.Send Seed")),
            Pair(itemStack(Material.MAP) {
                meta {
                    customModel = 10
                    name = "§9Announce Location"
                    lore = l.toMutableList().addLines(getMessageList("item.Timer.SendLocation.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Toggle")
                }
            }, c.getBoolean("Settings.Send Location")),
            Pair(itemStack(Material.WHITE_STAINED_GLASS) {
                meta {
                    customModel = 6
                    name = "§9Spec after Death"
                    lore = l.toMutableList().addLines(getMessageList("item.Timer.SpecDeath.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Toggle")
                }
            }, c.getBoolean("Settings.Spec on Death")),
            Pair(itemStack(Material.LIGHT_GRAY_STAINED_GLASS) {
                meta {
                    customModel = 7
                    name = "§9Spec after Joining"
                    lore = l.toMutableList().addLines(getMessageList("item.Timer.SpecJoin.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Toggle")
                }
            }, c.getBoolean("Settings.Spec on Join")),
            Pair(itemStack(Material.ANVIL) {
                meta {
                    customModel = 8
                    name = "§9Punishment"
                    lore = l.toMutableList().addLines(getMessageList("item.Timer.Punish.l", "   "))
                        .addLines(
                            " ", "§7∙ §9§nSettings",
                            "   §7Death: §9${c.getString("Settings.Death Punishment")}", " ",
                            "§9Left Click§7 ≫ Toggle",
                            "§9Right Click§7 ≫ Switch Death"
                        )
                }
            }, c.getBoolean("Settings.Punishment")),
        )
    }
}
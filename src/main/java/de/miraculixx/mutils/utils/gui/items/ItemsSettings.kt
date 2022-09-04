package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsSettings {
    fun getItems(id: Int, c: FileConfiguration, player: Player? = null): List<ItemStack> {
        val list = when (id) {
            1 -> g1(c, player)
            2 -> g2(c)
            3 -> g3(c)
            4 -> g4(c)
            5 -> g5(c)
            6 -> g6(c)
            7 -> g7(c)
            8 -> g8(c)
            else -> {
                List(1) {
                    itemStack(Material.BARRIER) {}
                }
            }
        }
        /* ID Glossary
        1 -> In Time
        2 -> Captive
        3 -> Ghost
        4 -> No Same Item
        5 -> Boost Up
        6 -> ChunkBreaker
        7 -> Damager
        8 -> Collect Rivals
         */
        return list
    }

    private fun g8(c: FileConfiguration): List<ItemStack> {
        val s1 = msg("item.GUI.RIVALS_COLLECT_S.1.n", pre = false)
        val s2 = msg("item.GUI.RIVALS_COLLECT_S.2.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9${c.getString("RIVALS_COLLECT.Mode")} §8(Default ITEMS)",
            "   §7$s2: §9${c.getDouble("RIVALS_COLLECT.Joker")} §8(Default 3)",
            " ",
        )
        return listOf(
            itemStack(Material.BOOK) { meta {
                customModel = 318
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.RIVALS_COLLECT_S.1.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Swap"
                    )
            }},
            itemStack(Material.DIAMOND_SWORD) { meta {
                customModel = 319
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.RIVALS_COLLECT_S.1.l"), ov)
                    .addLines(
                        "§9Left Click§7 ≫ +1",
                        "§9Right Click§7 ≫ -1"
                    )
            }},
        )
    }

    private fun g7(c: FileConfiguration): List<ItemStack> {
        val s1 = msg("item.GUI.DAMAGER_S.1.n", pre = false)
        val s2 = msg("item.GUI.DAMAGER_S.2.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9${c.getString("DAMAGER.Mode")} §8(Default SLOT_CHANGE)",
            "   §7$s2: §9${c.getDouble("DAMAGER.Damage")}hp §8(Default 1hp)",
            " ",
        )
        return listOf(
            itemStack(Material.BOOK) { meta {
                customModel = 316
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.DAMAGER_S.1.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Swap"
                    )
            }},
            itemStack(Material.DIAMOND_SWORD) { meta {
                customModel = 317
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.DAMAGER_S.1.l"), ov)
                    .addLines(
                        "§9Left Click§7 ≫ +1hp",
                        "§9Right Click§7 ≫ -1hp"
                    )
            }},
        )
    }

    private fun g6(c: FileConfiguration): List<ItemStack> {
        val s1 = msg("item.GUI.ChunkBlockBreakerS.1.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9${c.getBoolean("CHUNK_BLOCK_BREAK.Bundle")} §8(Default true)",
            " ",
        )
        return listOf(
            itemStack(Material.HOPPER) { meta {
                customModel = 315
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.ChunkBlockBreakerS.1.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Toggle"
                    )
            }})
    }

    private fun g5(c: FileConfiguration): List<ItemStack> {
        val s1 = msg("item.GUI.BoostUpS.1.n", pre = false)
        val s2 = msg("item.GUI.BoostUpS.2.n", pre = false)
        val s3 = msg("item.GUI.BoostUpS.3.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9${c.getInt("BOOST_UP.Boost")} §8(Default 5)",
            "   §7$s2: §9${c.getInt("BOOST_UP.Radius")}b §8(Default 5)",
            "   §7$s3: §9${c.getString("BOOST_UP.Mode")} §8(Default true)",
            " ",
        )

        return listOf(itemStack(Material.RABBIT_FOOT) { meta {
            customModel = 312
            name = "§9§l$s1"
            lore = ArrayList<String>()
                .addLines(getMessageList("item.GUI.BoostUpS.1.l"), ov)
                .addLines(
                    "§9Left click§7 ≫ +1",
                    "§9Right click§7 ≫ -1"
                )
        }},
        itemStack(Material.SUNFLOWER) { meta {
            customModel = 313
            name = "§9§l$s2"
            lore = ArrayList<String>()
                .addLines(getMessageList("item.GUI.BoostUpS.2.l"), ov)
                .addLines(
                    "§9Left click§7 ≫ +1b",
                    "§9Right click§7 ≫ -1b"
                )
        }},
        itemStack(Material.CHEST) { meta {
            customModel = 314
            name = "§9§l$s3"
            lore = ArrayList<String>()
                .addLines(getMessageList("item.GUI.BoostUpS.3.l"), ov)
                .addLines(
                    "§9Click§7 ≫ Toggle",
                )
        }})
    }

    private fun g4(c: FileConfiguration): List<ItemStack> {
        val l = ArrayList<ItemStack>(3)
        val s1 = msg("item.GUI.NoSameItemS.1.n", pre = false)
        val s2 = msg("item.GUI.NoSameItemS.2.n", pre = false)
        val s3 = msg("item.GUI.NoSameItemS.3.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9${c.getInt("NO_SAME_ITEM.Lives")} §8(Default 5)",
            "   §7$s2: §9${c.getBoolean("NO_SAME_ITEM.SyncHeart")} §8(Default false)",
            "   §7$s3: §9${c.getString("NO_SAME_ITEM.Info")} §8(Default EVERYTHING)",
            " ",
        )
        l.add(
            itemStack(Material.BEETROOT) { meta {
                customModel = 309
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.NoSameItemS.1.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +1",
                        "§9Right click§7 ≫ -1"
                    )}}
        )
        l.add(
            itemStack(Material.GOLDEN_APPLE) { meta {
                customModel = 310
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.NoSameItemS.2.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Switch Mode"
                    )}}
        )
        l.add(
            itemStack(Material.WRITABLE_BOOK) { meta {
                customModel = 311
                name = "§9§l$s3"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.NoSameItemS.3.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Switch Mode"
                    )}}
        )
        return l
    }

    private fun g3(c: FileConfiguration): List<ItemStack> {
        val l = ArrayList<ItemStack>(3)
        val s1 = msg("item.GUI.GhostS.1.n", pre = false)
        val s2 = msg("item.GUI.GhostS.2.n", pre = false)
        val s3 = msg("item.GUI.GhostS.3.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9" + c.getInt("GHOST.Radius") + "b §8(Default 7b)",
            "   §7$s2: §9" + c.getBoolean("GHOST.Adventure") + " §8(Default false)",
            "   §7$s3: §9" + c.getBoolean("GHOST.Mode") + " §8(Default true)",
            " ",
        )
        l.add(
            itemStack(Material.SUNFLOWER) { meta {
                customModel = 306
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.GhostS.1.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +1b",
                        "§9Right click§7 ≫ -1b"
                    )}}
        )
        l.add(
            itemStack(Material.STRUCTURE_VOID) { meta {
                customModel = 307
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.GhostS.2.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Switch Mode"
                    )}}
        )
        l.add(
            itemStack(Material.PHANTOM_MEMBRANE) { meta {
                customModel = 308
                name = "§9§l$s3"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.GhostS.3.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Switch Mode"
                    )}}
        )
        return l
    }

    private fun g2(c: FileConfiguration): List<ItemStack> {
        val l = ArrayList<ItemStack>(3)
        val s1 = msg("item.GUI.CaptiveS.1.n", pre = false)
        val s2 = msg("item.GUI.CaptiveS.2.n", pre = false)
        val s3 = msg("item.GUI.CaptiveS.3.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9" + c.getInt("CAPTIVE.Size") + "s §8(Default 5b)",
            "   §7$s2: §9" + c.getInt("CAPTIVE.Amplifier") + "s §8(Default 1b)",
            "   §7$s3: §9" + c.getBoolean("CAPTIVE.LevelMode") + " §8(Default true)",
            " ",
        )
        l.add(
            itemStack(Material.SUNFLOWER) { meta {
                customModel = 303
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.CaptiveS.1.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +1b",
                        "§9Right click§7 ≫ -1b"
                    )}}
        )
        l.add(
            itemStack(Material.EXPERIENCE_BOTTLE) { meta {
                customModel = 304
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.CaptiveS.2.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +1b",
                        "§9Right click§7 ≫ -1b"
                    )}}
        )
        l.add(
            itemStack(Material.KNOWLEDGE_BOOK) { meta {
                customModel = 305
                name = "§9§l$s3"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.CaptiveS.3.l"), ov)
                    .addLines(
                        "§9Click§7 ≫ Switch Mode"
                    )}}
        )
        return l
    }

    private fun g1(c: FileConfiguration, player: Player?): List<ItemStack> {
        val l = ArrayList<ItemStack>(3)
        val s1 = msg("item.GUI.InTimeS.1.n", pre = false)
        val s2 = msg("item.GUI.InTimeS.2.n", pre = false)
        val s3 = msg("item.GUI.InTimeS.3.n", pre = false)
        val ov = ArrayList<String>().addLines(
            " ",
            "§7∙ §9§nSettings",
            "   §7$s1: §9" + c.getInt("IN_TIME.PlayerTime") + "s §8(Default 120s)",
            "   §7$s2: §9" + c.getInt("IN_TIME.MobTime") + "s §8(Default 120s)",
            "   §7$s3: §9" + c.getInt("IN_TIME.DamageTime") + "s §8(Default 5s)",
            " ",
        )
        l.add(
            itemStack(Material.PLAYER_HEAD) { meta<SkullMeta> {
                owningPlayer = player
                customModel = 300
                name = "§9§l$s1"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.InTimeS.1.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +10s",
                        "§9Right click§7 ≫ -10s"
                    )}}
        )
        l.add(
            itemStack(Material.ZOMBIE_HEAD) { meta {
                customModel = 301
                name = "§9§l$s2"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.InTimeS.2.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +10s",
                        "§9Right click§7 ≫ -10s"
                    )}}
        )
        l.add(
            itemStack(Material.DIAMOND_SWORD) { meta {
                customModel = 302
                name = "§9§l$s3"
                lore = ArrayList<String>()
                    .addLines(getMessageList("item.GUI.InTimeS.3.l"), ov)
                    .addLines(
                        "§9Left click§7 ≫ +1s",
                        "§9Right click§7 ≫ -1s"
                    )}}
        )

        return l
    }
}
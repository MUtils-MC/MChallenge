package de.miraculixx.mutils.utils.tools.gui.items

import de.miraculixx.mutils.utils.addLines
import de.miraculixx.mutils.utils.getMessageList
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsMainMenu {

    fun getItems(id: Int): Map<ItemStack, Int> {
        val list = when (id) {
            1 -> g1()
            2 -> g2()
            3 -> g3()
            else -> {
                mapOf(Pair(ItemStack(Material.BARRIER), 4))
            }
        }
        /* ID Glossary
        1 -> Main Selector
        2 -> World Manager
        3 -> Challenge Creator
         */
        return list
    }

    private fun g3(): Map<ItemStack, Int> {
        val l = arrayListOf(" ", "§7∙ §9§nInfo")
        return mapOf(
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 1
                    name = "§9Create New Challenge"
                    lore = buildList {
                        addAll(l)
                        addAll(getMessageList("item.Creator.NewCh.l"))
                    }
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19"
                    )
                }
            } to 10,
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 2
                    name = "§9Delete Challenges"
                    lore = buildList {
                        addAll(l)
                        addAll(getMessageList("item.Creator.DeleteCh.l"))
                    }
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
                    )
                }
            } to 11,
            itemStack(Material.BOOK) {
                meta {
                    customModel = 3
                    name = "§9Challenge List"
                    lore = buildList {
                        addAll(l)
                        addAll(getMessageList("item.Creator.ChList"))
                    }
                }
            } to 13,
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 4
                    name = "§9Upload Challenge"
                    lore = buildList {
                        addAll(l)
                        addAll(getMessageList("item.Creator.UploadCh.l"))
                    }
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0ZjJmOTY5OGMzZjE4NmZlNDRjYzYzZDJmM2M0ZjlhMjQxMjIzYWNmMDU4MTc3NWQ5Y2VjZDcwNzUifX19"
                    )
                }
            } to 15,
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 5
                    name = "§9Global Challenge List"
                    lore = buildList {
                        addAll(l)
                        addAll(getMessageList("item.Creator.GlobalList.l"))
                    }
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ=="
                    )
                }
            } to 16,
        )
    }

    private fun g2(): Map<ItemStack, Int> {
        val l = arrayListOf(" ", "§7∙ §9§nInfo")
        return mapOf(
            Pair(itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 1
                    name = "§9World Overview"
                    lore = ArrayList<String>().addLines(l, getMessageList("item.World.Overview.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Open Overview")
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ=="
                    )
                }
            }, 10),
            Pair(itemStack(Material.CRAFTING_TABLE) {
                meta {
                    customModel = 2
                    name = "§9World Creator"
                    lore = ArrayList<String>().addLines(l, getMessageList("item.World.Creator.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Open Overview")
                }
            }, 11),
            Pair(itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 3
                    name = "§9Custom Data"
                    lore = ArrayList<String>().addLines(l, getMessageList("item.World.CustomData.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Open Overview")
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ=="
                    )
                }
            }, 13),
            Pair(itemStack(Material.GLOWSTONE_DUST) {
                meta {
                    customModel = 4
                    name = "§9World Settings"
                    lore = ArrayList<String>().addLines(l, getMessageList("item.World.WorldSettings.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Open Overview")
                }
            }, 15),
            Pair(itemStack(Material.REDSTONE) {
                meta {
                    customModel = 5
                    name = "§9Global World Settings"
                    lore = ArrayList<String>().addLines(l, getMessageList("item.World.GWorldSettings.l", "   "))
                        .addLines(" ", "§9Click§7 ≫ Open Overview")
                }
            }, 16)
        )
    }

    private fun g1(): Map<ItemStack, Int> {
        val soon = itemStack(Material.STRUCTURE_VOID) {
            meta {
                customModel = 0
                name = "§3§oComing Soon"
            }
        }
        val i1 = itemStack(Material.GOLDEN_APPLE) {
            meta {
                customModel = 1
                name = "§9§lChallenges"
                lore = infoLore(1)
            }
        }
        val i2 = itemStack(Material.GRASS_BLOCK) {
            meta {
                customModel = 2
                name = "§9§lWorld Manager"
                lore = infoLore(2)
            }
        }
        val i3 = itemStack(Material.GOLDEN_SWORD) {
            meta {
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                customModel = 3
                name = "§9§lCompetitions"
                lore = infoLore(3)
            }
        }
        val i4 = itemStack(Material.CLOCK) {
            meta {
                customModel = 4
                name = "§9§lTimer"
                lore = infoLore(4)
            }
        }
        val i5 = itemStack(Material.ANVIL) {
            meta {
                customModel = 5
                name = "§9§lChallenge Creator"
                lore = infoLore(5)
            }
        }
        val i6 = itemStack(Material.ENDER_EYE) {
            meta {
                customModel = 6
                name = "§9§lGeneral Settings"
                lore = infoLore(6)
            }
        }
        val i7 = itemStack(Material.END_CRYSTAL) {
            meta {
                customModel = 7
                name = "§9§lSpeed running"
                lore = infoLore(7)
            }
        }
        return mapOf(Pair(i1, 3), Pair(i2, 5), Pair(i3, 11), Pair(i4, 15), Pair(i5, 20), Pair(i6, 24), Pair(i7, 30), Pair(soon, 32))
    }


    private fun infoLore(id: Int): List<String> {
        val ph = listOf(" ", "§7∙ §9§nInfo")
        return ArrayList<String>()
            .addLines(ph, getMessageList("item.GUI.Select.$id.l", "   "))
    }
}
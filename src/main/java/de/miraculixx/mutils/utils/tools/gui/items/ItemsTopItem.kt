package de.miraculixx.mutils.utils.tools.gui.items

import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsTopItem {
    fun getItems(id: Int): ItemStack {
        return when (id) {
            1 -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 200
                    name = "§9World Overview"
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ=="
                    )
                }
            }
            2 -> itemStack(Material.GOLDEN_APPLE) {
                meta {
                    customModel = 200
                    name = "§9§lChallenges"
                }
            }
            3 -> itemStack(Material.STRUCTURE_VOID) {
                meta {
                    customModel = 200
                    name = "§9§lBanned Players"
                }
            }
            4 -> itemStack(Material.MAP) {
                meta {
                    customModel = 200
                    name = "§9§lWhitelisted Players"
                }
            }
            5 -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 200
                    name = "§9Global World Settings"
                    itemMeta = skullTexture(
                        this,
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ=="
                    )
                }
            }

            else -> ItemStack(Material.BARRIER)
        }
        /*
        1 -> Worlds
        2 -> Challenges
        3 -> Banned Players
        4 -> Whitelist Players
         */
    }
}
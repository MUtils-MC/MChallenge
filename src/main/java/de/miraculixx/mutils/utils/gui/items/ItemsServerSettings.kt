package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.text.SimpleDateFormat
import java.util.*

class ItemsServerSettings {
    fun getItems(id: Int, c: FileConfiguration?): Map<ItemStack, Int> {
        val list = when (id) {
            1 -> g1(c!!)
            2 -> g2()
            3 -> g3()
            else -> {
                mapOf(Pair(ItemStack(Material.BARRIER), 4))
            }
        }
        /* ID Glossary
        1 -> Server Settings
         */
        return list
    }


    private fun g3(): Map<ItemStack, Int> {
        val map = HashMap<ItemStack, Int>()
        val formatter = SimpleDateFormat("dd.MM.YYYY HH:mm:ss")
        formatter.timeZone = TimeZone.getDefault()

        Bukkit.getWhitelistedPlayers().forEach { player ->
            val item = itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    owningPlayer = player
                    name = "§9§l${player.name ?: "§cUnloaded"}"
                    customModel = 151
                    lore = listOf(
                        "§9Last Seen: §7${formatter.format(Date(player.lastSeen))}",
                        "§9Online: §7${player.isOnline}",
                        "§9OP: §7${player.isOp}",
                        "§9UUID: §7${player.uniqueId}",
                        "§9",
                        " ", "§9Click§7 ≫ Remove Player"
                    )
                }
            }
            map[item] = 0
        }
        return map
    }

    private fun g2(): Map<ItemStack, Int> {
        val map = HashMap<ItemStack, Int>()
        val formatter = SimpleDateFormat("dd.MM.YYYY HH:mm:ss")
        formatter.timeZone = TimeZone.getDefault()

        Bukkit.getBannedPlayers().forEach { player ->
            val item = itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    owningPlayer = player
                    name = "§9§l${player.name ?: "§cUnloaded"}"
                    customModel = 150
                    lore = listOf("§9Last Seen: §7${formatter.format(Date(player.lastSeen))}",
                            "§9UUID: §7${player.uniqueId}",
                            " ","§9Click§7 ≫ Unban Player")
                }
            }
            map[item] = 0
        }
        return map
    }

    private fun g1(c: FileConfiguration): Map<ItemStack, Int> {
        val ov = listOf(" ", "§7∙ §9§nSettings")
        return mapOf(
            itemStack(Material.PAINTING) {
                meta {
                    customModel = 100
                    name = "§9§lServer Icon"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Icon.l"), ov)
                        .addLines("   §7Path: §9${c.getString("Server Icon")}",
                            " ", "§9Click§7 ≫ Change")
                }
            } to 10,
            itemStack(Material.KNOWLEDGE_BOOK) {
                meta {
                    customModel = 101
                    name = "§9§lServer MOTD"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.MOTD.l"), ov)
                        .addLines(c.getString("MOTD") ?: "   §7None",
                        " ", "§9Click§7 ≫ Change")
                }
            } to 19,
            itemStack(Material.TOTEM_OF_UNDYING) {
                meta {
                    customModel = 102
                    name = "§9§lServer Slots"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Slots.l"), ov)
                        .addLines("   §7Slots: §9${c.getInt("Slots")}",
                        " ", "§9Left Click§7 ≫ +1 Slot",
                            "§9Right Click§7 ≫ -1 Slot")
                }
            } to 11,
            itemStack(Material.STRUCTURE_VOID) {
                meta {
                    customModel = 103
                    name = "§9§lBanned Players"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Bans.l"), ov)
                        .addLines("   §7Players: §9${Bukkit.getBannedPlayers().size}",
                        " ", "§9Click§7 ≫ Open Overview")
                }
            } to 13,
            itemStack(Material.MAP) {
                meta {
                    customModel = 104
                    name = "§9§lWhitelisted Players"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Whitelist.l"), ov)
                        .addLines("   §7Players: §9${Bukkit.getWhitelistedPlayers().size}",
                            "   §7Active: §9${Bukkit.hasWhitelist()}",
                        " ", "§9Left Click§7 ≫ Open Overview", "§9Right Click§7 ≫ Toggle Whitelist")
                }
            } to 22,
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 105
                    name = "§9§lUpload Settings"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Upload.l"))
                        .addLines(" ", "§9Click§7 ≫ Upload")
                }
                itemMeta = skullTexture(itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0ZjJmOTY5OGMzZjE4NmZlNDRjYzYzZDJmM2M0ZjlhMjQxMjIzYWNmMDU4MTc3NWQ5Y2VjZDcwNzUifX19")
            } to 16,
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 106
                    name = "§9§lDownload Settings"
                    lore = ArrayList<String>()
                        .addLines(getMessageList("item.Server.Download.l"))
                        .addLines(" ", "§9Click§7 ≫ Download")
                }
                itemMeta = skullTexture(itemMeta as SkullMeta,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmIyOWVjZWVmM2RkYjE0ZjkwNmRiZDRmYTQxZDYzZjNkN2Q0NTM3ODcxY2VlNDMxNWM1OWU3NmViYzVmODUifX19")
            } to 25
        )
    }
}
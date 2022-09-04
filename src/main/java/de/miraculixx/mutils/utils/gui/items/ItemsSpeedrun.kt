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

class ItemsSpeedrun {

    fun getItems(id: Int, c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(c)
            else -> {
                linkedMapOf(Pair(ItemStack(Material.BARRIER), false))
            }
        }
        /* ID Glossary
        1 → Speedrun Settings
        2 -> TODO
         */
        return list
    }

    private fun g1(c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {

        return linkedMapOf(
            Pair(itemStack(Material.VILLAGER_SPAWN_EGG) { meta {
                customModel = 1
                name = "§9Villager Spawn"
                lore = ArrayList<String>().addLines(getMessageList("item.Speedrun.Village.l"))
                    .addLines(" ", "§7∙ §9§nSettings",
                        "   §7Radius: §9${c.getInt("Village Radius")} §8(default 300)",
                        "   §7Teleport: §9${c.getBoolean("Village Teleport")} §8(default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle",
                        "§9Shift left click§7 ≫ Toggle teleport",
                        "§9Right click§7 ≫ +50 Blocks",
                        "§9Shift right click§7 ≫ -50 Blocks")
            }}, c.getBoolean("Village Spawn")),
            Pair(itemStack(Material.CRYING_OBSIDIAN) { meta {
                customModel = 2
                name = "§9Ruined Portal Spawn"
                lore = ArrayList<String>().addLines(getMessageList("item.Speedrun.Portal.l"))
                    .addLines(" ", "§7∙ §9§nSettings",
                        "   §7Radius: §9${c.getInt("Portal Radius")} §8(default 500)",
                        " ",
                        "§9Left click§7 ≫ Toggle",
                        "§9Right click§7 ≫ +50 Blocks",
                        "§9Shift right click§7 ≫ -50 Blocks")
            }}, c.getBoolean("Portal Spawn")),
            Pair(itemStack(Material.CLOCK) { meta {
                customModel = 3
                name = "§9Timer Delay"
                lore = ArrayList<String>().addLines(getMessageList("item.Speedrun.Delay.l"))
                    .addLines(" ", "§7∙ §9§nSettings",
                        "   §7Radius: §9${c.getInt("Timer Delay")} §8(default 3)",
                        " ",
                        "§9Left click§7 ≫ Toggle",
                        "§9Right click§7 ≫ +1s",
                        "§9Shift right click§7 ≫ -1s")
            }}, c.getBoolean("Timer")),
            Pair(itemStack(Material.GOLD_INGOT) { meta {
                customModel = 4
                name = "§9Old Piglin Trades"
                lore = ArrayList<String>().addLines(getMessageList("item.Speedrun.OldTrades.l"))
                    .addLines(" ","§7∙ §9§nSettings",
                        "   §7None",
                        " ",
                        "§9Left click§7 ≫ Toggle")
            }}, c.getBoolean("Old Trading")),
            Pair(itemStack(Material.GOLDEN_AXE) { meta {
                customModel = 5
                name = "§9Disable Piglin Brutes"
                lore = ArrayList<String>().addLines(getMessageList("item.Speedrun.Brutes.l"))
                    .addLines(" ","§7∙ §9§nSettings",
                        "   §7None",
                        " ",
                        "§9Left click§7 ≫ Toggle")
            }}, c.getBoolean("Disable Brutes"))
        )
    }
}
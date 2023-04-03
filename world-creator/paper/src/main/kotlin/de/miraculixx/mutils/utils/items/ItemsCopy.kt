package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsCopy: ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))
    private val infoClick = listOf(emptyComponent(), msgClick + cmp("Select"))

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        return listOf(
            itemStack(Material.CHEST) {
                meta {
                    customModel = 1
                    name = cmp(msgString("items.creator.copy.n"), cHighlight)
                    lore(infoLore + msgList("items.creator.copy.l") + infoClick)
                }
            },
            itemStack(Material.ENDER_CHEST) {
                meta {
                    customModel = 2
                    name = cmp(msgString("items.creator.copyFull.n"), cHighlight)
                    lore(infoLore + msgList("items.creator.copyFull.l") + infoClick)
                }
            }
        )
    }
}
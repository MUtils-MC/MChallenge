package de.miraculixx.mtimer.gui.items

import de.miraculixx.mcore.gui.items.ItemProvider
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TestProvider: ItemProvider {

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        return buildList {
            Material.values().copyOfRange(from, to).forEach {
                add(ItemStack(it))
            }
        }
    }
}
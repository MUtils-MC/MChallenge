package de.miraculixx.mutils.gui.items

import org.bukkit.inventory.ItemStack

interface ItemProvider {
    fun getSlotMap(): Map<ItemStack, Int> { return emptyMap() }

    /**
     * @param from Inclusive
     * @param to exclusive
     */
    fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> { return emptyMap() }
    fun getItemList(from: Int = 0, to: Int = 0): List<ItemStack> { return emptyList() }
}
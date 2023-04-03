package de.miraculixx.mcore.gui.items

import org.bukkit.inventory.ItemStack

interface ItemProvider {
    fun getSlotMap(): Map<Int, ItemStack> { return emptyMap() }

    /**
     * @param from Inclusive
     * @param to exclusive
     */
    fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> { return emptyMap() }
    fun getItemList(from: Int = 0, to: Int = 0): List<ItemStack> { return emptyList() }

    /**
     * Returns a list of custom extra items for special postions.
     *
     * - Headers -> Storage GUI
     * - Reset Button -> Settings GUI
     */
    fun getExtra(): List<ItemStack> { return emptyList() }
}
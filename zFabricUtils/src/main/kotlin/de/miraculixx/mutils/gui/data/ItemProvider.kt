package de.miraculixx.mutils.gui.data

import net.minecraft.world.item.ItemStack

interface ItemProvider {
    fun getSlotMap(): Map<Int, ItemStack> { return emptyMap() }

    /**
     * @param from Inclusive
     * @param to exclusive
     */
    fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> { return emptyMap() }
    fun getItemList(): List<ItemStack> { return emptyList() }

    /**
     * Returns a list of custom extra items for special postions.
     *
     * - Headers -> Storage GUI
     * - Reset Button -> Settings GUI
     */
    fun getExtra(): List<ItemStack> { return emptyList() }
}
package de.miraculixx.mutils.gui.items

import org.bukkit.inventory.ItemStack

interface ItemProvider {
    fun getSlotMap(): Map<ItemStack, Int> { return emptyMap() }
    fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> { return emptyMap() }
    fun getItemList(): List<ItemStack> { return emptyList() }
}
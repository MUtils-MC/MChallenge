package de.miraculixx.mutils.challenge.utils

import org.bukkit.Material
import org.bukkit.inventory.Inventory

class BasicItem(private val material: Material, private val amount: Int) {

    fun getAmount(): Int {
        return amount
    }
    fun getMaterial(): Material {
        return material
    }

    fun invContains(inventory: Inventory): Boolean {
        var needed = 0
        inventory.forEach { item ->
            if (item == null) return@forEach
            if (item.type == material)
                needed += item.amount
        }
        return needed >= amount
    }
    fun removeItem(inventory: Inventory) {
        var remain = amount
        for ((slot, item) in inventory.withIndex()) {
            if (item == null) continue
            if (item.type == material) {
                if (item.amount > remain) {
                    item.amount = item.amount - remain
                    inventory.setItem(slot, item)
                    return
                }
                if (item.amount == remain) {
                    inventory.setItem(slot, null)
                    return
                }
                inventory.setItem(slot, null)
                remain -= item.amount
            }
        }
    }
}
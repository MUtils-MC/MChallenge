package de.miraculixx.mutils.utils.tools.gui

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemMeta
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.fillPlaceholder(): Inventory {
    for (i in 0 until size) {
        setItem(i, InvUtils.primaryPlaceholder)
    }
    if (size != 9) {
        setItem(17, InvUtils.secondaryPlaceholder)
        setItem(size - 18, InvUtils.secondaryPlaceholder)
        repeat(2) { setItem(it, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(it + 7, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(size - it - 8, InvUtils.secondaryPlaceholder) }
        repeat(2) { setItem(size - it - 1, InvUtils.secondaryPlaceholder) }

    } else {
        setItem(0, InvUtils.secondaryPlaceholder)
        setItem(8, InvUtils.secondaryPlaceholder)
    }
    return this
}

object InvUtils {
    val primaryPlaceholder: ItemStack
    val secondaryPlaceholder: ItemStack


    init {
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val mat1 = Material.getMaterial(config.getString("Placeholder 1") ?: "") ?: Material.GRAY_STAINED_GLASS_PANE
        val mat2 = Material.getMaterial(config.getString("Placeholder 2") ?: "") ?: Material.BLACK_STAINED_GLASS_PANE
        val meta = itemMeta(mat1) {
            name = " "
            customModel = 200
        }
        primaryPlaceholder = itemStack(mat1) { itemMeta = meta }
        secondaryPlaceholder = itemStack(mat2) { itemMeta = meta }
    }
}
package de.miraculixx.mchallenge.utils

import de.miraculixx.mcommons.majorVersion
import org.bukkit.Material
import org.bukkit.inventory.CreativeCategory


fun getItems(silkTouch: Boolean, removeHardToObtain: Boolean): List<Material> {
    val list = Material.entries.filter {
        it.isItem
                && !it.isLegacy
                && it.creativeCategory != null
                && !it.name.endsWith("_ore")
                && !it.name.contains("WEATHERED_")
                && (!it.name.contains("OXIDIZED_") || !removeHardToObtain)
                && !it.name.startsWith("INFESTED_")
                && !it.name.endsWith("SPAWN_EGG")
                && (!it.name.contains("_CORAL") || !silkTouch)
                && !it.name.endsWith("_HEAD")
                && (!it.name.contains("AMETHYST") || !silkTouch)
    }.toMutableList()
    if (!silkTouch) list.removeAll(
        listOf(
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.ICE,
            Material.BLUE_ICE,
            Material.PACKED_ICE,
            Material.MYCELIUM,

            )
    )
    if (removeHardToObtain) list.removeAll(
        listOf(
            Material.TURTLE_EGG,
        )
    )
    list.removeAll(
        listOf(
            Material.BEDROCK,
            Material.DIRT_PATH,
            Material.FARMLAND,
            Material.END_PORTAL_FRAME,
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION,
            Material.TIPPED_ARROW,
            Material.ENCHANTED_BOOK,
            Material.LIGHT
        )
    )
    if (majorVersion >= 17) list.remove(Material.BUDDING_AMETHYST)
    return list
}

package de.miraculixx.mutils.modules.creator.enums

import org.bukkit.Material

enum class CreatorActionInput(val material: Material) {
    //Custom Types
    MATERIAL(Material.CRAFTING_TABLE),
    SELECTOR(Material.TOTEM_OF_UNDYING),
    SOUND(Material.JUKEBOX),

    //Raw Types
    INT(Material.GOLD_NUGGET),
    DOUBLE(Material.GOLD_INGOT),
    TEXT(Material.BOOK),
}
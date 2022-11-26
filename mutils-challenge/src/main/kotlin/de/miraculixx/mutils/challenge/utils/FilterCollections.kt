package de.miraculixx.mutils.challenge.utils

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*


fun getItems(silkTouch: Boolean, removeHardToObtain: Boolean): List<Material> {
    val list = Material.values().filter {
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
    if (!silkTouch) list.removeAll(listOf(
        Material.CRIMSON_NYLIUM,
        Material.WARPED_NYLIUM,
        Material.ICE,
        Material.BLUE_ICE,
        Material.PACKED_ICE,
        Material.MYCELIUM,

        ))
    if (removeHardToObtain) list.removeAll(listOf(
        Material.TURTLE_EGG,
        ))
    list.removeAll(
        listOf(
            Material.BEDROCK,
            Material.BUDDING_AMETHYST,
            Material.DIRT_PATH,
            Material.FARMLAND,
            Material.END_PORTAL_FRAME,
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION,
            Material.TIPPED_ARROW,
            Material.ENCHANTED_BOOK
        )
    )
    return list
}

fun getLivingMobs(natural: Boolean): MutableList<EntityType> {
    val list = kotlin.collections.ArrayList<EntityType>(Arrays.stream(EntityType.values()).filter {
            entityType -> entityType.entityClass != null && LivingEntity::class.java.isAssignableFrom(entityType.entityClass)
    }.toList()).toMutableList()
    list.remove(EntityType.PLAYER)
    if (natural) {
        list.removeAll(
            listOf(
                EntityType.GIANT,
                EntityType.ILLUSIONER,
                EntityType.ZOMBIE_HORSE,
                EntityType.SKELETON_HORSE
            )
        )
    }
    return list
}
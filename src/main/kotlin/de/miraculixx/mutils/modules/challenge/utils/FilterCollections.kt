package de.miraculixx.mutils.modules.challenge.utils

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*


fun getItems(silktouch: Boolean): List<Material> {
    val list = Material.values().filter {
        it.isItem
                && !it.isLegacy
                && it.creativeCategory != null
                && !it.name.endsWith("_ore")
                && !it.name.contains("WEATHERED_")
                && !it.name.contains("OXIDIZED_")
                && !it.name.startsWith("INFESTED_")
                && !it.name.endsWith("SPAWN_EGG")
                && !it.name.contains("_CORAL")
                && !it.name.endsWith("_HEAD")
                && !it.name.contains("AMETHYST")
    }.toMutableList()
    list.removeAll(
        listOf(
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.BEDROCK,
            Material.BUDDING_AMETHYST,
            Material.ICE,
            Material.PACKED_ICE,
            Material.BLUE_ICE,
            Material.DIRT_PATH,
            Material.FARMLAND,
            Material.END_PORTAL_FRAME,
            Material.MYCELIUM,
            Material.SNOW,
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION,
            Material.TIPPED_ARROW,
            Material.TURTLE_EGG,
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
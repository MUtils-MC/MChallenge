package de.miraculixx.mutils.utils

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*

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

fun getMaterials(survival: Boolean): List<Material> {
    val list = Material.values().filter {
        val name = it.name
        it.isItem
                && !it.isAir
                && (it.creativeCategory != null || !survival)
                && (!name.startsWith("INFESTED_") || !survival)
                && (!name.endsWith("_SPAWN_EGG") || !survival)
    }.toMutableList()
    if (survival) list.remove(Material.BEDROCK)
    return list
}
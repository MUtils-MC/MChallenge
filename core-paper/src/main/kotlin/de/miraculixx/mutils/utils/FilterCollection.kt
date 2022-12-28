package de.miraculixx.mutils.utils

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
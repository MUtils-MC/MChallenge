package de.miraculixx.mcore.utils

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

fun getMaterials(survival: Boolean, hard: Boolean = true): List<Material> {
    val list = Material.values().filter {
        val name = it.name
        it.isItem
                && !it.isAir
                && (!name.startsWith("INFESTED_") || !survival)
                && (!name.endsWith("_SPAWN_EGG") || !survival)
                && (!name.contains("COMMAND_BLOCK") || !survival)
                && (!name.endsWith("SHULKER_BOX") || hard)
                && (!name.endsWith("HEAD") || hard)
                && (!name.contains("OXIDIZED"))
                && (name != "LIGHT" || !survival)
                && (name != "REINFORCED_DEEPSLATE" || !survival)
    }.toMutableList()
    if (survival) {
        list.removeAll(listOf(
            Material.BEDROCK,
            Material.JIGSAW,
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.DEBUG_STICK,
            Material.BARRIER,
            Material.SPAWNER,
        ))
    }
    return list
}
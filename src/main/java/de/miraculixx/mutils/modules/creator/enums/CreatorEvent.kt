package de.miraculixx.mutils.modules.creator.enums

import org.bukkit.Material

enum class CreatorEvent(val interfaces: List<EventType>, val material: Material) {
    //Move Events
    MOVE_GENERAL(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE), Material.GOLDEN_BOOTS),
    MOVE_BLOCK(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE), Material.CHAINMAIL_BOOTS),
    MOVE_CAMERA(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE), Material.ENDER_EYE),
    MOVE_JUMP(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE), Material.RABBIT_FOOT),
    DIMENSION_SWAP(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE), Material.END_PORTAL_FRAME),

    BLOCK_BREAK(listOf(EventType.CANCELABLE), Material.IRON_PICKAXE),
    BLOCK_PLACE(listOf(EventType.CANCELABLE), Material.GRASS_BLOCK),

    MOB_DEATH(listOf(EventType.ENTITY_EVENT), Material.ZOMBIE_HEAD),
    MOB_DAMAGE(listOf(EventType.ENTITY_EVENT, EventType.CANCELABLE), Material.DIAMOND_SWORD),
    MOB_KILL(listOf(EventType.ENTITY_EVENT, EventType.CANCELABLE), Material.ZOMBIE_HEAD);


    companion object {
        fun getByOrdinal(ordinal: Int): CreatorEvent? {
            return CreatorEvent.values().getOrNull(ordinal)
        }
    }
}
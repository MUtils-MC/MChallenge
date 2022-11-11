package de.miraculixx.mutils.modules.creator.enums

import de.miraculixx.mutils.modules.creator.data.ActionValueData
import org.bukkit.Material

enum class CreatorAction(val type: EventType, val inputType: List<ActionValueData>, val material: Material) {

    CANCEL(EventType.CANCELABLE, emptyList(), Material.BEDROCK),

    SPAWN_MOB(EventType.PLAYER_EVENT, listOf(ActionValueData("Mob", CreatorActionInput.MOB), ActionValueData("Amount", CreatorActionInput.INT)), Material.SPAWNER),

    INVENTORY_CLEAR(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR)), Material.BUCKET),
    TELEPORT_RADIUS(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Radius", CreatorActionInput.INT)), Material.ENDER_PEARL),
    CHANGE_TIME_WORLD(EventType.GENERAL, listOf(ActionValueData("Adding Time (Ticks)", CreatorActionInput.INT)), Material.CLOCK),
    CHANGE_TIME_PLAYER(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Final Time (Ticks)", CreatorActionInput.INT)), Material.CLOCK),
    GIVE_ITEM_PLAYER(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Item", CreatorActionInput.MATERIAL), ActionValueData("Amount", CreatorActionInput.INT)), Material.DROPPER),
    DAMAGE_ENTITY(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Damage", CreatorActionInput.DOUBLE)), Material.IRON_SWORD),
    PLAY_SOUND(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Sound", CreatorActionInput.SOUND)), Material.JUKEBOX),
    SEND_MESSAGE(EventType.GENERAL, listOf(ActionValueData("Target", CreatorActionInput.SELECTOR), ActionValueData("Message", CreatorActionInput.TEXT)), Material.BOOK);

    companion object {
        fun getByOrdinal(ordinal: Int): CreatorAction? {
            return CreatorAction.values().getOrNull(ordinal)
        }
    }
}
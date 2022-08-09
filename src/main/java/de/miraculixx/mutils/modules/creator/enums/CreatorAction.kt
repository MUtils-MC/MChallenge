package de.miraculixx.mutils.modules.creator.enums

enum class CreatorAction(val type: EventType) {
    // Player Events
    GIVE_ITEM_TARGET_PLAYER(EventType.PLAYER_EVENT),
    DAMAGE_TARGET_PLAYER(EventType.PLAYER_EVENT),
    DAMAGE_ALL_PLAYERS(EventType.PLAYER_EVENT),

    // Cancelable Events
    CANCEL(EventType.CANCELABLE),
}
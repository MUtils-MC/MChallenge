package de.miraculixx.mutils.modules.creator.enums

enum class CreatorEvent(val interfaces: List<EventType>) {
    MOVE_GENERAL(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE)),
    MOVE_BLOCK(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE)),
    MOVE_CAMERA(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE)),
    MOVE_JUMP(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE)),
    DIMENSION_SWAP(listOf(EventType.PLAYER_EVENT, EventType.CANCELABLE))
}
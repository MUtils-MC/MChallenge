package de.miraculixx.mutils.enums.modules.creator

enum class Events {
    //Damage
    DAMAGE_DEAL,
    DAMAGE_ABSORB,

    //Travel
    CHANGE_WORLD,
    MOVE,
    MOVE_ONE_BLOCK,
    TOGGLE_SNEAK,
    TOGGLE_SPRINT,
    TELEPORT,
    JUMP,

    //Items
    ITEM_DROP,
    ITEM_COLLECT,
    ITEM_CONSUME,
    ITEM_DAMAGE,
    ITEM_MENDING,
    ITEM_SWITCH_HAND,
    ITEM_SWITCH_HELD,

    //Interact
    INTERACT,
    CHAT_EVENT,
    BED_ENTER,

    //General
    EXP_CHANGE,
    LEVEL_CHANGE,
    FISHING,
    ADVANCEMENT_GET,
}
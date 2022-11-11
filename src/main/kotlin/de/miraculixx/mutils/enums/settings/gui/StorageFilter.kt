package de.miraculixx.mutils.enums.settings.gui

enum class StorageFilter {
    NO_FILTER,
    HIDE,

    //CHALLENGE FILTER
    FUN,
    MEDIUM,
    HARD,
    RANDOMIZER,
    FORCE,
    COMPLEX,
    VERSION_BOUND,
    BETA,
    MULTIPLAYER,

    //WORLD FILTER
    OVERWORLD,
    NETHER,
    END,

    //CHALLENGE CREATOR - EVENTS
    PLAYER_EVENT,
    ENTITY_EVENT,
    CANCELABLE,

    //CHALLENGE CREATOR - ACTIONS
    ACTIVE_ACTIONS,
    ACTION_LIBRARY,


    GAMERULES
}
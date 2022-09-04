package de.miraculixx.mutils.modules.creator.enums

enum class CreatorActionSelector {
    EVERY_PLAYER,
    RANDOM_PLAYER,
    SOURCE_PLAYER,

    ONLY_MOBS,
    MOBS_AND_PLAYERS;

    companion object {
        operator fun get(name: String): CreatorActionSelector? {
            return try {
                valueOf(name)
            } catch (_: IllegalArgumentException) { null }
        }
    }
}
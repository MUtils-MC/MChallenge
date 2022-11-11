package de.miraculixx.mutils.enums.modules

import de.miraculixx.mutils.enums.settings.gui.StorageFilter

enum class Modules(b: Boolean, l: List<StorageFilter>) {
    //Challenges
    FLY(true, listOf(StorageFilter.FUN)),
    IN_TIME(true, listOf(StorageFilter.MEDIUM)),
    MOB_RANDOMIZER(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    CHECKPOINTS(true, listOf(StorageFilter.FUN)),
    DIM_SWAP(true, listOf(StorageFilter.MEDIUM)),
    SNAKE(true, listOf(StorageFilter.HARD, StorageFilter.COMPLEX)),
    REALISTIC(true, listOf(StorageFilter.HARD, StorageFilter.COMPLEX, StorageFilter.BETA)),
    CAPTIVE(true, listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND)),
    GHOST(true, listOf(StorageFilter.FUN)),
    BLOCK_ASYNC(true, listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER)),
    NO_SAME_ITEM(true, listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER)),
    LIMITED_SKILLS(true, listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER)),
    RUN_RANDOMIZER(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    SPLIT_HP(true, listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER)),
    DAMAGE_DUELL(true, listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER)),
    ONE_BIOME(true, listOf(StorageFilter.MEDIUM)),
    BOOST_UP(true, listOf(StorageFilter.MEDIUM)),
    RIGHT_TOOL(true, listOf(StorageFilter.MEDIUM)),
    CHUNK_BLOCK_BREAK(true, listOf(StorageFilter.MEDIUM)),
    SNEAK_SPAWN(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    WORLD_PEACE(true, listOf(StorageFilter.MEDIUM)),
    STAY_AWAY(true, listOf(StorageFilter.HARD)),
    RANDOMIZER_BLOCK(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_ENTITY(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_BIOMES(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_MOBS(true, listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    FORCE_COLLECT(true, listOf(StorageFilter.MEDIUM, StorageFilter.FORCE)),
    RANDOMIZER_ENTITY_DAMAGE(true, listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER)),
    NO_DOUBLE_KILL(true, listOf(StorageFilter.MEDIUM)),
    DAMAGER(true, listOf(StorageFilter.MEDIUM, StorageFilter.HARD)),
    GRAVITY(true, listOf(StorageFilter.MEDIUM, StorageFilter.COMPLEX)),
    RIVALS_COLLECT(true, listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER)),

    //Utilitys
    SPEEDRUN(false, listOf()),
    TIMER(false, listOf()),
    BACK(false, listOf()),
    BACKPACK(false, listOf()),
    PLAYER_WORLD(false, listOf()),

    //System
    CUSTOM_CHALLENGE(false, listOf()),
    NONE(false, listOf());

    private var isChallenge = b
    fun isChallenge(): Boolean {
        return this.isChallenge
    }

    private var filters = l
    fun matchingFilter(filter: StorageFilter): Boolean {
        return filters.contains(filter)
    }
    fun getFilterLore(): List<String> {
        return filters.map { "   §7- ${it.name}" }
    }
}
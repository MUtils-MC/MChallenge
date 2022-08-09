@file:Suppress("SpellCheckingInspection")

package de.miraculixx.mutils.enums.modules

import de.miraculixx.mutils.enums.settings.gui.StorageFilters

enum class Modules(b: Boolean, l: List<StorageFilters>) {
    //Challenges
    FLY(true, listOf(StorageFilters.FUN)),
    IN_TIME(true, listOf(StorageFilters.MEDIUM)),
    MOB_RANDOMIZER(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    CHECKPOINTS(true, listOf(StorageFilters.FUN)),
    DIM_SWAP(true, listOf(StorageFilters.MEDIUM)),
    SNAKE(true, listOf(StorageFilters.HARD, StorageFilters.COMPLEX)),
    REALISTIC(true, listOf(StorageFilters.HARD, StorageFilters.COMPLEX, StorageFilters.BETA)),
    CAPTIVE(true, listOf(StorageFilters.MEDIUM, StorageFilters.VERSION_BOUND)),
    GHOST(true, listOf(StorageFilters.FUN)),
    BLOCK_ASYNC(true, listOf(StorageFilters.FUN, StorageFilters.MULTIPLAYER)),
    NO_SAME_ITEM(true, listOf(StorageFilters.MEDIUM, StorageFilters.MULTIPLAYER)),
    LIMITED_SKILLS(true, listOf(StorageFilters.HARD, StorageFilters.MULTIPLAYER)),
    RUN_RANDOMIZER(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    SPLIT_HP(true, listOf(StorageFilters.MEDIUM, StorageFilters.MULTIPLAYER)),
    DAMAGE_DUELL(true, listOf(StorageFilters.FUN, StorageFilters.MULTIPLAYER)),
    ONE_BIOME(true, listOf(StorageFilters.MEDIUM)),
    BOOST_UP(true, listOf(StorageFilters.MEDIUM)),
    RIGHT_TOOL(true, listOf(StorageFilters.MEDIUM)),
    CHUNK_BLOCK_BREAK(true, listOf(StorageFilters.MEDIUM)),
    SNEAK_SPAWN(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    WORLD_PEACE(true, listOf(StorageFilters.MEDIUM)),
    STAY_AWAY(true, listOf(StorageFilters.HARD)),
    RANDOMIZER_BLOCK(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    RANDOMIZER_ENTITY(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    RANDOMIZER_BIOMES(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    RANDOMIZER_MOBS(true, listOf(StorageFilters.FUN, StorageFilters.RANDOMIZER)),
    FORCE_COLLECT(true, listOf(StorageFilters.MEDIUM, StorageFilters.FORCE)),
    RANDOMIZER_ENTITY_DAMAGE(true, listOf(StorageFilters.MEDIUM, StorageFilters.RANDOMIZER)),
    NO_DOUBLE_KILL(true, listOf(StorageFilters.MEDIUM)),
    DAMAGER(true, listOf(StorageFilters.MEDIUM, StorageFilters.HARD)),
    GRAVITY(true, listOf(StorageFilters.MEDIUM, StorageFilters.COMPLEX)),
    RIVALS_COLLECT(true, listOf(StorageFilters.FUN, StorageFilters.FORCE, StorageFilters.MULTIPLAYER)),

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
    fun matchingFilter(filter: StorageFilters): Boolean {
        return filters.contains(filter)
    }
    fun getFilterLore(): List<String> {
        return filters.map { "   §7- ${it.name}" }
    }
}
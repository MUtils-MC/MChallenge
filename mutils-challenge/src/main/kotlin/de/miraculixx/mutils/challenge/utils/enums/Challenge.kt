package de.miraculixx.mutils.challenge.utils.enums

import de.miraculixx.mutils.challenge.utils.enums.gui.StorageFilter

enum class Challenge(l: List<StorageFilter>) {
    //Challenges
    FLY(listOf(StorageFilter.FUN)),
    IN_TIME(listOf(StorageFilter.MEDIUM)),
    MOB_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    CHECKPOINTS(listOf(StorageFilter.FUN)),
    DIM_SWAP(listOf(StorageFilter.MEDIUM)),
    SNAKE(listOf(StorageFilter.HARD, StorageFilter.COMPLEX)),
    REALISTIC(listOf(StorageFilter.HARD, StorageFilter.COMPLEX, StorageFilter.BETA)),
    CAPTIVE(listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND)),
    GHOST(listOf(StorageFilter.FUN)),
    BLOCK_ASYNC(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER)),
    NO_SAME_ITEM(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER)),
    LIMITED_SKILLS(listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER)),
    RUN_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    SPLIT_HP(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER)),
    DAMAGE_DUELL(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER)),
    ONE_BIOME(listOf(StorageFilter.MEDIUM)),
    BOOST_UP(listOf(StorageFilter.MEDIUM)),
    RIGHT_TOOL(listOf(StorageFilter.MEDIUM)),
    CHUNK_BLOCK_BREAK(listOf(StorageFilter.MEDIUM)),
    SNEAK_SPAWN(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    WORLD_PEACE(listOf(StorageFilter.MEDIUM)),
    STAY_AWAY(listOf(StorageFilter.HARD)),
    RANDOMIZER_BLOCK(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_ENTITY(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_BIOMES(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    RANDOMIZER_MOBS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER)),
    FORCE_COLLECT(listOf(StorageFilter.MEDIUM, StorageFilter.FORCE)),
    RANDOMIZER_ENTITY_DAMAGE(listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER)),
    NO_DOUBLE_KILL(listOf(StorageFilter.MEDIUM)),
    DAMAGER(listOf(StorageFilter.MEDIUM, StorageFilter.HARD)),
    GRAVITY(listOf(StorageFilter.MEDIUM, StorageFilter.COMPLEX)),
    RIVALS_COLLECT(listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER));

  
    private var filters = l
    fun matchingFilter(filter: StorageFilter): Boolean {
        return filters.contains(filter)
    }
}
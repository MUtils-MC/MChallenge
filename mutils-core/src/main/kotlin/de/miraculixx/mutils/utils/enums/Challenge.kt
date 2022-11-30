package de.miraculixx.mutils.utils.enums

import de.miraculixx.mutils.utils.enums.gui.StorageFilter
import de.miraculixx.mutils.utils.gui.data.items.SettingsData

enum class Challenge(val filter: List<StorageFilter>, private val settings: List<Pair<String, String>>) {
    //Challenges
    FLY(listOf(StorageFilter.FUN), listOf("power" to "2.0")),
    IN_TIME(listOf(StorageFilter.MEDIUM), listOf("pTime" to "120s", "eTime" to "120s", "hpTime" to "5s")),
    MOB_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("rnd" to "false")),
    CHECKPOINTS(listOf(StorageFilter.FUN), listOf("onlyTP" to "false")),
    DIM_SWAP(listOf(StorageFilter.MEDIUM), listOf("starter" to "false")),
    SNAKE(listOf(StorageFilter.HARD, StorageFilter.COMPLEX), listOf("speed" to "1")),
    REALISTIC(listOf(StorageFilter.HARD, StorageFilter.COMPLEX, StorageFilter.BETA), emptyList()),
    CAPTIVE(listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND), listOf("base" to "1b", "amplifier" to "1b", "mode" to "true")),
    GHOST(listOf(StorageFilter.FUN), listOf("radius" to "7b", "adventure" to "false", "mode" to "true")),
    BLOCK_ASYNC(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), emptyList()),
    NO_SAME_ITEM(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), listOf("lives" to "5", "sync" to "false", "info" to "EVERYTHING")),
    LIMITED_SKILLS(listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER), listOf("random" to "true")),
    RUN_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("goal" to "500b")),
    SPLIT_HP(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), emptyList()),
    DAMAGE_DUELL(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), listOf("percent" to "50%")),
    ONE_BIOME(listOf(StorageFilter.MEDIUM), listOf("delay" to "300s")),
    BOOST_UP(listOf(StorageFilter.MEDIUM), listOf("radius" to "5", "boost" to "5", "mode" to "true")),
    RIGHT_TOOL(listOf(StorageFilter.MEDIUM), emptyList()),
    CHUNK_BLOCK_BREAK(listOf(StorageFilter.MEDIUM), listOf("bundle" to "true")),
    SNEAK_SPAWN(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("onlyMob" to "true")),
    WORLD_PEACE(listOf(StorageFilter.MEDIUM), emptyList()),
    GRAVITY(listOf(StorageFilter.MEDIUM, StorageFilter.COMPLEX), listOf("delay" to "180s")),
    STAY_AWAY(listOf(StorageFilter.HARD), listOf("distance" to "3.0")),
    RANDOMIZER_BLOCK(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("random" to "false")),
    RANDOMIZER_ENTITY(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("random" to "false")),
    RANDOMIZER_BIOMES(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("random" to "false")),
    RANDOMIZER_MOBS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("random" to "false")),
    FORCE_COLLECT(listOf(StorageFilter.MEDIUM, StorageFilter.FORCE), listOf("min" to "180s", "max" to "360s", "cooldown" to "300")),
    RANDOMIZER_ENTITY_DAMAGE(listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER), listOf("random" to "false")),
    NO_DOUBLE_KILL(listOf(StorageFilter.MEDIUM), listOf("global" to "true")),
    DAMAGER(listOf(StorageFilter.MEDIUM, StorageFilter.HARD), listOf("mode" to "SLOT_CHANGE", "damage" to "1hp")),
    RIVALS_COLLECT(listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER), listOf("mode" to "ITEMS", "joker" to "3"));

  
    fun matchingFilter(filter: StorageFilter): Boolean {
        return this.filter.contains(filter)
    }

    fun getSettings(values: List<Any>): List<SettingsData> {
        var i = -1
        return settings.map {
            i++
            SettingsData(it.first, it.second, values.getOrNull(i)?.toString() ?: "unknown")
        }
    }
}
package de.miraculixx.mutils.enums

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.mutils.config.Config
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.data.SettingsData
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.utils.gui.items.skullTexture
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * @param filter List of filter categories the challenges owns
 * @param settings List of setting data **Pair<ID, Default>**
 * @param icon Material with possible meta data
 */
enum class Challenge(private val filter: List<StorageFilter>, private val settings: List<Pair<String, String>>, private val icon: Pair<Material, Head64?>) {
    //Challenges
    FLY(listOf(StorageFilter.FUN), listOf("power" to "2.0"), Material.ELYTRA to null),
    IN_TIME(listOf(StorageFilter.MEDIUM), listOf("pTime" to "120s", "eTime" to "120s", "hpTime" to "5s"), Material.CLOCK to null),
    MOB_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), listOf("rnd" to "false"), Material.PLAYER_HEAD to Head64.DUMMY),
    CHECKPOINTS(listOf(StorageFilter.FUN), listOf("onlyTP" to "false"), Material.PLAYER_HEAD to Head64.DUMMY),
    DIM_SWAP(listOf(StorageFilter.MEDIUM), listOf("starter" to "false"), ),
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

    fun getSettings(config: Config): List<SettingsData> {
        return settings.map {
            SettingsData(it.first, it.second, config.getString(name + "." + it.first).ifEmpty { "unknown" })
        }
    }

    fun hasSettings(): Boolean {
        return settings.isNotEmpty()
    }

    fun getIcon(): ItemStack {
        return itemStack(icon.first) {
            icon.second?.let { (itemMeta as? SkullMeta)?.skullTexture(it.value) }
        }
    }
}
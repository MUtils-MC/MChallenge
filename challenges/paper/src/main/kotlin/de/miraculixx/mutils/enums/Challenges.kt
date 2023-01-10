package de.miraculixx.mutils.enums

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.data.SettingsData
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.gui.items.skullTexture
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * @param filter List of filter categories the challenges owns
 * @param settings List of setting data **Pair<ID, Default>**
 * @param icon Material with possible metadata **Pair<Icon, HeadTexture>**
 */
enum class Challenges(val filter: List<StorageFilter>, private val settings: List<Pair<String, String>>, private val icon: Icon) {
    //Challenges
    FLY(                listOf(StorageFilter.FUN),                                  listOf("power" to "2.0"),                                               Icon(Material.ELYTRA)),
    IN_TIME(            listOf(StorageFilter.MEDIUM),                               listOf("pTime" to "120s", "eTime" to "120s", "hpTime" to "5s"),         Icon(Material.CLOCK)),
    MOB_BLOCKS(         listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("rnd" to "false"),                                               Icon(Material.ZOMBIE_HEAD)),
    CHECKPOINTS(        listOf(StorageFilter.FUN),                                  listOf("onlyTP" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.BACKWARD_QUARTZ)),
    DIM_SWAP(           listOf(StorageFilter.MEDIUM),                               listOf("starter" to "false"),                                           Icon(Material.END_PORTAL_FRAME)),
    SNAKE(              listOf(StorageFilter.HARD, StorageFilter.COMPLEX),          listOf("speed" to "1b/s"),                                              Icon(Material.RED_CONCRETE_POWDER)),
    REALISTIC(          listOf(StorageFilter.HARD, StorageFilter.COMPLEX),          emptyList(),                                                            Icon(Material.OAK_SAPLING)),
    //CAPTIVE(            listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND),  listOf("base" to "1b", "amplifier" to "1b", "mode" to "true"),          Icon(Material.IRON_BARS)),
    GHOST(              listOf(StorageFilter.FUN),                                  listOf("radius" to "7b", "adventure" to "false", "mode" to "true"),     Icon(Material.PLAYER_HEAD, Head64.GHAST)),
    BLOCK_ASYNC(        listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER),       emptyList(),                                                            Icon(Material.RED_STAINED_GLASS)),
    NO_SAME_ITEM(       listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER),    listOf("lives" to "5", "sync" to "false", "info" to "EVERYTHING"),      Icon(Material.WITHER_ROSE)),
    LIMITED_SKILLS(     listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER),      listOf("random" to "true"),                                             Icon(Material.TURTLE_HELMET)),
    RUN_RANDOMIZER(     listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("goal" to "500b"),                                               Icon(Material.GOLDEN_BOOTS)),
    SPLIT_HP(           listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER),    emptyList(),                                                            Icon(Material.BEETROOT)),
    DAMAGE_DUELL(       listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER),       listOf("percent" to "50%"),                                             Icon(Material.IRON_SWORD)),
    ONE_BIOME(          listOf(StorageFilter.MEDIUM),                               listOf("delay" to "300s"),                                              Icon(Material.FILLED_MAP)),
    BOOST_UP(           listOf(StorageFilter.MEDIUM),                               listOf("radius" to "5", "boost" to "5", "mode" to "true"),              Icon(Material.SHULKER_SHELL)),
    RIGHT_TOOL(         listOf(StorageFilter.MEDIUM),                               emptyList(),                                                            Icon(Material.WOODEN_AXE)),
    CHUNK_BLOCK_BREAK(  listOf(StorageFilter.MEDIUM),                               listOf("bundle" to "true"),                                             Icon(Material.TNT)),
    SNEAK_SPAWN(        listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("onlyMob" to "true"),                                            Icon(Material.HOPPER)),
    WORLD_PEACE(        listOf(StorageFilter.MEDIUM),                               emptyList(),                                                            Icon(Material.CORNFLOWER)),
    GRAVITY(            listOf(StorageFilter.MEDIUM, StorageFilter.COMPLEX),        listOf("delay" to "180s"),                                              Icon(Material.SAND)),
    STAY_AWAY(          listOf(StorageFilter.HARD),                                 listOf("distance" to "3.0"),                                            Icon(Material.TNT)),
    RANDOMIZER_BLOCK(   listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("random" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.DICE_GREEN)),
    RANDOMIZER_ENTITY(  listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("random" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.DICE_ORANGE)),
    RANDOMIZER_BIOMES(  listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("random" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.DICE_PURPLE)),
    RANDOMIZER_MOBS(    listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER),        listOf("random" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.DICE_BLACK)),
    FORCE_COLLECT(      listOf(StorageFilter.MEDIUM, StorageFilter.FORCE),          listOf("min" to "180s", "max" to "360s", "cooldown" to "300"),          Icon(Material.CHEST)),
    RANDOMIZER_DAMAGE(  listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER),     listOf("random" to "false"),                                            Icon(Material.PLAYER_HEAD, Head64.DICE_RED)),
    NO_DOUBLE_KILL(     listOf(StorageFilter.MEDIUM),                               listOf("global" to "true"),                                             Icon(Material.REPEATER)),
    DAMAGER(            listOf(StorageFilter.MEDIUM, StorageFilter.HARD),           listOf("mode" to "SLOT_CHANGE", "damage" to "1hp"),                     Icon(Material.DIAMOND_SWORD)),
    RIVALS_COLLECT(     listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER), listOf("mode" to "ITEMS", "joker" to "3"),               Icon(Material.CHEST_MINECART)),
    ROCKET(             listOf(StorageFilter.MEDIUM),                               listOf("capacity" to "5s"),                                             Icon(Material.FIREWORK_ROCKET));

  
    fun matchingFilter(filter: StorageFilter): Boolean {
        return this.filter.contains(filter)
    }

    fun getSettings(config: FileConfiguration): List<SettingsData> {
        return settings.map {
            SettingsData(it.first, it.second, config.getString(name + "." + it.first)?.ifEmpty { "unknown" } ?: "unknown")
        }
    }

    fun hasSettings(): Boolean {
        return settings.isNotEmpty()
    }

    fun getIcon(): ItemStack {
        return itemStack(icon.material) {
            icon.texture?.let { itemMeta = (itemMeta as? SkullMeta)?.skullTexture(it.value) }
        }
    }

    data class Icon(val material: Material, val texture: Head64? = null)
}
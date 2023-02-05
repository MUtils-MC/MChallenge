package de.miraculixx.mutils.enums

import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.modules.mods.damager.ChDamager
import de.miraculixx.mutils.modules.mods.noSameItem.NoSameItemEnum
import de.miraculixx.mutils.modules.mods.rivalsCollect.RivalCollectMode
import de.miraculixx.mutils.utils.settings.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * @param filter List of filter categories the challenges owns
 * @param icon Material with possible metadata **Pair<Icon, HeadTexture>**
 */
enum class Challenges(val filter: List<StorageFilter>, private val icon: Icon, val status: Boolean = false) {
    FLY(listOf(StorageFilter.FUN), Icon(Material.ELYTRA)),
    IN_TIME(listOf(StorageFilter.MEDIUM), Icon(Material.CLOCK)),
    MOB_BLOCKS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.ZOMBIE_HEAD)),
    CHECKPOINTS(listOf(StorageFilter.FUN), Icon(Material.PLAYER_HEAD, Head64.BACKWARD_QUARTZ)),
    DIM_SWAP(listOf(StorageFilter.MEDIUM), Icon(Material.END_PORTAL_FRAME)),
    SNAKE(listOf(StorageFilter.HARD), Icon(Material.RED_CONCRETE_POWDER)),
    REALISTIC(listOf(StorageFilter.HARD), Icon(Material.OAK_SAPLING)),
    //CAPTIVE(listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND), Icon(Material.IRON_BARS)),
    GHOST(listOf(StorageFilter.FUN), Icon(Material.PLAYER_HEAD, Head64.GHAST)),
    BLOCK_ASYNC(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), Icon(Material.RED_STAINED_GLASS)),
    NO_SAME_ITEM(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), Icon(Material.WITHER_ROSE)),
    LIMITED_SKILLS(listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER), Icon(Material.TURTLE_HELMET)),
    RUN_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.GOLDEN_BOOTS)),
    SPLIT_HP(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), Icon(Material.BEETROOT)),
    DAMAGE_DUELL(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), Icon(Material.IRON_SWORD)),
    ONE_BIOME(listOf(StorageFilter.MEDIUM), Icon(Material.FILLED_MAP)),
    BOOST_UP(listOf(StorageFilter.MEDIUM), Icon(Material.SHULKER_SHELL)),
    RIGHT_TOOL(listOf(StorageFilter.MEDIUM), Icon(Material.WOODEN_AXE)),
    CHUNK_BLOCK_BREAK(listOf(StorageFilter.MEDIUM), Icon(Material.TNT)),
    SNEAK_SPAWN(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.HOPPER)),
    WORLD_PEACE(listOf(StorageFilter.MEDIUM), Icon(Material.CORNFLOWER)),
    GRAVITY(listOf(StorageFilter.MEDIUM), Icon(Material.SAND)),
    STAY_AWAY(listOf(StorageFilter.HARD), Icon(Material.TNT)),
    RANDOMIZER_BLOCK(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.PLAYER_HEAD, Head64.DICE_GREEN)),
    RANDOMIZER_ENTITY(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.PLAYER_HEAD, Head64.DICE_ORANGE)),
    RANDOMIZER_BIOMES(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.PLAYER_HEAD, Head64.DICE_PURPLE)),
    RANDOMIZER_MOBS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon(Material.PLAYER_HEAD, Head64.DICE_BLACK)),
    FORCE_COLLECT(listOf(StorageFilter.MEDIUM, StorageFilter.FORCE), Icon(Material.CHEST)),
    RANDOMIZER_DAMAGE(listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER), Icon(Material.PLAYER_HEAD, Head64.DICE_RED)),
    NO_DOUBLE_KILL(listOf(StorageFilter.MEDIUM), Icon(Material.REPEATER)),
    DAMAGER(listOf(StorageFilter.MEDIUM, StorageFilter.HARD), Icon(Material.DIAMOND_SWORD)),
    RIVALS_COLLECT(listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER), Icon(Material.CHEST_MINECART)),
    ROCKET(listOf(StorageFilter.MEDIUM), Icon(Material.FIREWORK_ROCKET)),

    VAMPIRE(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon(Material.GHAST_TEAR), true),
    TRAFFIC_LIGHT(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon(Material.REDSTONE_LAMP), true),

    ;


    fun matchingFilter(filter: StorageFilter): Boolean {
        return this.filter.contains(filter)
    }

    fun getIcon(): ItemStack {
        return itemStack(icon.material) {
            icon.texture?.let { itemMeta = (itemMeta as? SkullMeta)?.skullTexture(it.value) }
        }
    }

    fun getDefaultSettings(): Map<String, ChallengeSetting<out Any>> {
        return when (this) {
            FLY -> mapOf("power" to ChallengeDoubleSetting("FEATHER", 2.0))
            IN_TIME -> mapOf(
                "pTime" to ChallengeIntSetting("PLAYER_HEAD", 120, "s", max = 1000, min = 10, step = 10),
                "eTime" to ChallengeIntSetting("ZOMBIE_HEAD", 120, "s", max = 1000, min = 10, step = 10),
                "hpTime" to ChallengeIntSetting("BEETROOT", 5, "s", max = 100, min = 1, step = 1)
            )

            MOB_BLOCKS -> mapOf("rnd" to ChallengeBoolSetting("DROPPER", false))
            CHECKPOINTS -> mapOf("onlyTP" to ChallengeBoolSetting("ENDER_PEARL", false))
            DIM_SWAP -> mapOf("starter" to ChallengeBoolSetting("WOODEN_PICKAXE", false))
            SNAKE -> mapOf("speed" to ChallengeIntSetting("SUGAR", 1, "b/s", max = 15, min = 1, step = 1))
            REALISTIC -> emptyMap()
            GHOST -> mapOf(
                "radius" to ChallengeIntSetting("SNOWBALL", 7, "b", max = 20, min = 1),
                "adventure" to ChallengeBoolSetting("IRON_SWORD", false),
            )

            BLOCK_ASYNC -> emptyMap()
            NO_SAME_ITEM -> mapOf(
                "lives" to ChallengeIntSetting("BEETROOT", 5, max = 10, min = 1),
                "sync" to ChallengeBoolSetting("REDSTONE", false),
                "info" to ChallengeEnumSetting("WRITABLE_BOOK", NoSameItemEnum.EVERYTHING.name, options = NoSameItemEnum.values().map { it.name })
            )

            LIMITED_SKILLS -> mapOf("random" to ChallengeBoolSetting("DROPPER", true))
            RUN_RANDOMIZER -> mapOf("goal" to ChallengeIntSetting("CHEST", 500, "b", max = 5000, min = 50, step = 50))
            SPLIT_HP -> emptyMap()
            DAMAGE_DUELL -> mapOf("percent" to ChallengeIntSetting("IRON_SWORD", 50, "%", max = 100, min = 5, step = 5))
            ONE_BIOME -> mapOf("delay" to ChallengeIntSetting("CLOCK", 300, "s", max = 1000, min = 30, step = 15))
            BOOST_UP -> mapOf(
                "radius" to ChallengeIntSetting("SNOWBALL", 5, "b", max = 15, min = 1),
                "boost" to ChallengeIntSetting("ARROW", 5, max = 20, min = 1),
                "mode" to ChallengeBoolSetting("POLAR_BEAR_SPAWN_EGG", true)
            )

            RIGHT_TOOL -> emptyMap()
            CHUNK_BLOCK_BREAK -> mapOf("bundle" to ChallengeBoolSetting("HOPPER", true))
            SNEAK_SPAWN -> mapOf("onlyMob" to ChallengeBoolSetting("POLAR_BEAR_SPAWN_EGG", true))
            WORLD_PEACE -> emptyMap()
            GRAVITY -> mapOf("delay" to ChallengeIntSetting("CLOCK", 180, "s", max = 500, min = 30, step = 10))
            STAY_AWAY -> mapOf("distance" to ChallengeDoubleSetting("SNOWBALL", 3.0, "b", max = 10.0, min = 0.5))
            RANDOMIZER_BLOCK -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            RANDOMIZER_ENTITY -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            RANDOMIZER_BIOMES -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            RANDOMIZER_MOBS -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            FORCE_COLLECT -> mapOf(
                "minCooldown" to ChallengeIntSetting("GOLD_NUGGET", 250, "s", max = 900, min = 15, step = 15),
                "maxCooldown" to ChallengeIntSetting("GOLD_INGOT", 350, "s", max = 900, min = 15, step = 15),
                "minTime" to ChallengeIntSetting("IRON_NUGGET", 180, "s", max = 900, min = 15, step = 15),
                "maxTime" to ChallengeIntSetting("IRON_INGOT", 360, "s", max = 900, min = 15, step = 15),
            )

            RANDOMIZER_DAMAGE -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            NO_DOUBLE_KILL -> mapOf("global" to ChallengeBoolSetting("POPPED_CHORUS_FRUIT", true))
            DAMAGER -> mapOf(
                "mode" to ChallengeEnumSetting("KNOWLEDGE_BOOK", ChDamager.SLOT_CHANGE.name, options = ChDamager.values().map { it.name }),
                "damage" to ChallengeIntSetting("BEETROOT", 1, "hp", max = 20, min = 1)
            )

            RIVALS_COLLECT -> mapOf(
                "mode" to ChallengeEnumSetting("KNOWLEDGE_BOOK", RivalCollectMode.ITEMS.name, options = RivalCollectMode.values().map { it.name }),
                "joker" to ChallengeIntSetting("ENDER_CHEST", 3, max = 64, min = 0)
            )

            ROCKET -> mapOf("capacity" to ChallengeIntSetting("REDSTONE_TORCH", 5, "s", max = 60, min = 1))
            VAMPIRE -> mapOf(
                "startBlood" to ChallengeIntSetting("REDSTONE", 250, max = 1000, min = 10, step = 10),
                "maxBlood" to ChallengeIntSetting("REDSTONE_BLOCK", 500, max = 1000, min = 50, step = 10),
                "healthToBlood" to ChallengeDoubleSetting("GOLDEN_SWORD", 2.0, max = 10.0, min = 0.5),
                "bloodLoose" to ChallengeDoubleSetting("GHAST_TEAR", 2.5, max = 20.0, min = 0.5)
            )

            TRAFFIC_LIGHT -> mapOf(
                "green" to ChallengeSectionSetting(
                    "LIME_CONCRETE", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 30, "s", max = 500, min = 0, step = 5),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 90, "s", max = 500, min = 5, step = 5),
                    )
                ),
                "yellow" to ChallengeSectionSetting(
                    "YELLOW_CONCRETE", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 2, "s", max = 500, min = 0, step = 1),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 4, "s", max = 500, min = 1, step = 1),
                    )
                ),
                "red" to ChallengeSectionSetting(
                    "RED_CONCRETE", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 3, "s", max = 500, min = 1, step = 1),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 15, "s", max = 500, min = 2, step = 1),
                    )
                ),
                "damage" to ChallengeDoubleSetting("BEETROOT", 10.0, "hp", max = 50.0, min = 0.5)
            )
        }
    }

    data class Icon(val material: Material, val texture: Head64? = null)
}
package de.miraculixx.api.modules.challenges

import de.miraculixx.api.modules.mods.damager.ChDamager
import de.miraculixx.api.modules.mods.noSameItem.NoSameItemEnum
import de.miraculixx.api.modules.mods.rivalsCollect.RivalCollectMode
import de.miraculixx.api.settings.*
import de.miraculixx.mutils.gui.Head64
import de.miraculixx.mutils.gui.StorageFilter

/**
 * @param filter List of filter categories the challenges owns
 * @param icon Material with possible metadata **Pair<Icon, HeadTexture>**
 */
enum class Challenges(val filter: List<StorageFilter>, val icon: Icon, val status: Boolean = false) {
    // Global Challenges
    VAMPIRE(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("GHAST_TEAR"), true),
    TRAFFIC_LIGHT(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("REDSTONE_LAMP"), true),
    TRON(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("LIGHT_BLUE_CONCRETE"), true),
    DISABLED(listOf(StorageFilter.FUN, StorageFilter.FREE), Icon("IRON_BARS"), true),
    MOB_HUNT(listOf(StorageFilter.FUN, StorageFilter.FREE, StorageFilter.FORCE), Icon("PHANTOM_SPAWN_EGG"), true),
    ITEM_HUNT(listOf(StorageFilter.FUN, StorageFilter.FREE, StorageFilter.FORCE), Icon("ENDER_CHEST"), true),
    MIRROR(listOf(StorageFilter.FUN, StorageFilter.FREE, StorageFilter.MULTIPLAYER), Icon("GLASS"), true),
    CHUNK_FLATTENER(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("IRON_TRAPDOOR"), true),
    CHUNK_BLOCK_BREAK(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("TNT"), true),
    CHUNK_DECAY(listOf(StorageFilter.MEDIUM, StorageFilter.FREE), Icon("OAK_LEAVES"), true),
    CHUNK_CLEARER(listOf(StorageFilter.HARD, StorageFilter.FREE), Icon("DRAGON_BREATH"), true),
    ANVIL_CRUSHER(listOf(StorageFilter.HARD, StorageFilter.FREE), Icon("ANVIL"), true),
    ITEM_DECAY(listOf(StorageFilter.HARD, StorageFilter.FREE), Icon("COMPARATOR"), true),


    FLY(listOf(StorageFilter.FUN), Icon("ELYTRA")),
    IN_TIME(listOf(StorageFilter.MEDIUM), Icon("CLOCK")),
    MOB_BLOCKS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("ZOMBIE_HEAD")),
    CHECKPOINTS(listOf(StorageFilter.FUN), Icon("PLAYER_HEAD", Head64.BACKWARD_QUARTZ)),
    DIM_SWAP(listOf(StorageFilter.MEDIUM), Icon("END_PORTAL_FRAME")),
    SNAKE(listOf(StorageFilter.HARD), Icon("RED_CONCRETE_POWDER")),
    REALISTIC(listOf(StorageFilter.HARD), Icon("OAK_SAPLING")),
    //CAPTIVE(listOf(StorageFilter.MEDIUM, StorageFilter.VERSION_BOUND), Icon("IRON_BARS)),
    GHOST(listOf(StorageFilter.FUN), Icon("PLAYER_HEAD", Head64.GHAST)),
    BLOCK_ASYNC(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), Icon("RED_STAINED_GLASS")),
    NO_SAME_ITEM(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), Icon("WITHER_ROSE")),
    LIMITED_SKILLS(listOf(StorageFilter.HARD, StorageFilter.MULTIPLAYER), Icon("TURTLE_HELMET")),
    RUN_RANDOMIZER(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("GOLDEN_BOOTS")),
    SPLIT_HP(listOf(StorageFilter.MEDIUM, StorageFilter.MULTIPLAYER), Icon("BEETROOT")),
    DAMAGE_DUELL(listOf(StorageFilter.FUN, StorageFilter.MULTIPLAYER), Icon("IRON_SWORD")),
    ONE_BIOME(listOf(StorageFilter.MEDIUM), Icon("FILLED_MAP")),
    BOOST_UP(listOf(StorageFilter.MEDIUM), Icon("SHULKER_SHELL")),
    RIGHT_TOOL(listOf(StorageFilter.MEDIUM), Icon("WOODEN_AXE")),
    SNEAK_SPAWN(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("HOPPER")),
    WORLD_PEACE(listOf(StorageFilter.MEDIUM), Icon("CORNFLOWER")),
    GRAVITY(listOf(StorageFilter.MEDIUM), Icon("SAND")),
    STAY_AWAY(listOf(StorageFilter.HARD), Icon("TNT")),
    RANDOMIZER_BLOCK(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("PLAYER_HEAD", Head64.DICE_GREEN)),
    RANDOMIZER_ENTITY(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("PLAYER_HEAD", Head64.DICE_ORANGE)),
    RANDOMIZER_BIOMES(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("PLAYER_HEAD", Head64.DICE_PURPLE)),
    RANDOMIZER_MOBS(listOf(StorageFilter.FUN, StorageFilter.RANDOMIZER), Icon("PLAYER_HEAD", Head64.DICE_BLACK)),
    FORCE_COLLECT(listOf(StorageFilter.MEDIUM, StorageFilter.FORCE), Icon("CHEST")),
    RANDOMIZER_DAMAGE(listOf(StorageFilter.MEDIUM, StorageFilter.RANDOMIZER), Icon("PLAYER_HEAD", Head64.DICE_RED)),
    NO_DOUBLE_KILL(listOf(StorageFilter.MEDIUM), Icon("REPEATER")),
    DAMAGER(listOf(StorageFilter.HARD), Icon("DIAMOND_SWORD")),
    RIVALS_COLLECT(listOf(StorageFilter.FUN, StorageFilter.FORCE, StorageFilter.MULTIPLAYER), Icon("CHEST_MINECART")),
    ROCKET(listOf(StorageFilter.MEDIUM), Icon("FIREWORK_ROCKET")),

    ;


    fun matchingFilter(filter: StorageFilter): Boolean {
        return this.filter.contains(filter)
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
                "glide" to ChallengeBoolSetting("FEATHER", true),
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
            TRON -> mapOf("visible" to ChallengeBoolSetting("GLASS_BOTTLE", true))
            DISABLED -> mapOf(
                "block" to ChallengeSectionSetting(
                    "STONE_BRICKS",
                    mapOf(
                        "break" to ChallengeBoolSetting("IRON_PICKAXE", false),
                        "place" to ChallengeBoolSetting("GRAVEL", false)
                    )
                ),
                "interact" to ChallengeSectionSetting(
                    "BREAD",
                    mapOf(
                        "craft" to ChallengeBoolSetting("CRAFTING_TABLE", false),
                        "trade" to ChallengeBoolSetting("EMERALD", false)
                    )
                ),
                "misc" to ChallengeSectionSetting(
                    "WHEAT_SEEDS",
                    mapOf(
                        "xp" to ChallengeBoolSetting("EXPERIENCE_BOTTLE", false),
                        "items" to ChallengeBoolSetting("HOPPER", false)
                    )
                ),
                "damage" to ChallengeIntSetting("BEETROOT", 1)
            )
            MOB_HUNT -> emptyMap()
            ITEM_HUNT -> emptyMap()
            MIRROR -> mapOf(
                "hearts" to ChallengeBoolSetting("BEETROOT"),
                "food" to ChallengeBoolSetting("BREAD"),
                "potions" to ChallengeBoolSetting("POTION"),
                "hotbar" to ChallengeBoolSetting("WHITE_SHULKER_BOX")
            )
            CHUNK_FLATTENER -> mapOf(
                "delay" to ChallengeIntSetting("CLOCK", 15, "s", max = 600, min = 1),
                "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", false)
            )
            CHUNK_DECAY -> mapOf(
                "delay" to ChallengeIntSetting("CLOCK", 15, "s", max = 600, min = 1),
                "percentage" to ChallengeIntSetting("COMPARATOR", 5, "%", max = 100, min = 1),
                "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", false)
            )
            CHUNK_CLEARER -> mapOf(
                "breakAll" to ChallengeBoolSetting("GRASS_BLOCK", true),
                "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", true),
                "bundle" to ChallengeBoolSetting("HOPPER", true)
            )
            ANVIL_CRUSHER -> mapOf(
                "delay" to ChallengeSectionSetting("CLOCK", mapOf(
                    "startDelay" to ChallengeIntSetting("REDSTONE", 200, "t", max = 1200, min = 20, step = 20),
                    "amplifierDelay" to ChallengeIntSetting("COMPARATOR", 5, "t", max = 60, min = 0, step = 5)
                )),
                "density" to ChallengeSectionSetting("ANVIL", mapOf(
                    "startDensity" to ChallengeIntSetting("REDSTONE", 5, "%", max = 100, min = 1),
                    "amplifierDensity" to ChallengeIntSetting("COMPARATOR", 1, "%", max = 10, min = 0)
                )),
                "radius" to ChallengeSectionSetting("ARROW", mapOf(
                    "startRadius" to ChallengeIntSetting("REDSTONE", 5, "b", max = 15, min = 1),
                    "amplifierRadius" to ChallengeIntSetting("COMPARATOR", 5, "b", max = 5, min = 0),
                )),
                "height" to ChallengeIntSetting("RABBIT_FOOT", 5, "b", max = 15, min = 2)
            )
            ITEM_DECAY -> mapOf(
                "time" to ChallengeIntSetting("CLOCK", 300, "s", max = 1800, min = 10, step = 10)
            )
        }
    }

    data class Icon(val material: String, val texture: Head64? = null)
}
package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.settings.*
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.mchallenge.modules.mods.misc.areaTimer.AreaTimerMode
import de.miraculixx.mchallenge.modules.mods.multiplayer.noSameItems.NoSameItemEnum
import de.miraculixx.mchallenge.modules.mods.simple.damager.DamagerType
import de.miraculixx.mchallenge.modules.mods.worldChanging.border.BorderMode
import de.miraculixx.mcommons.statics.KHeads

/**
 * @param filter List of filter categories the challenges owns
 * @param icon Material with possible metadata **Pair<Icon, HeadTexture>**
 */
enum class Challenges(val filter: Set<ChallengeTags>, val icon: Icon, val status: Boolean = false) {
    // Global Challenges
//    HALLOWEEN(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("JACK_O_LANTERN"), true),
    VAMPIRE(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("GHAST_TEAR"), true),
    TRAFFIC_LIGHT(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("REDSTONE_LAMP"), true),
    TRON(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("LIGHT_BLUE_CONCRETE"), true),
    DISABLED(setOf(ChallengeTags.FUN, ChallengeTags.FREE), Icon("IRON_BARS"), true),
    MOB_HUNT(setOf(ChallengeTags.FUN, ChallengeTags.FREE, ChallengeTags.FORCE), Icon("PHANTOM_SPAWN_EGG"), true),
    ITEM_HUNT(setOf(ChallengeTags.FUN, ChallengeTags.FREE, ChallengeTags.FORCE), Icon("ENDER_CHEST"), true),
    MIRROR(setOf(ChallengeTags.FUN, ChallengeTags.FREE, ChallengeTags.MULTIPLAYER), Icon("GLASS"), true),
    CHUNK_FLATTENER(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("IRON_TRAPDOOR"), true),
    CHUNK_BLOCK_BREAK(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("TNT"), true),
    CHUNK_DECAY(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("OAK_LEAVES"), true),
    CHUNK_CLEARER(setOf(ChallengeTags.HARD, ChallengeTags.FREE), Icon("DRAGON_BREATH"), true),
    ANVIL_CRUSHER(setOf(ChallengeTags.HARD, ChallengeTags.FREE), Icon("ANVIL"), true),
    ITEM_DECAY(setOf(ChallengeTags.HARD, ChallengeTags.FREE), Icon("COMPARATOR"), true),
    AREA_TIMER(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("GRASS_BLOCK"), true),
    DAMAGE_MULTIPLIER(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("BEETROOT"), true),
    FLY(setOf(ChallengeTags.FUN, ChallengeTags.FREE), Icon("ELYTRA"), true),
    GRAVITY(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("SAND"), true),
    HP_DRAIN(setOf(ChallengeTags.MEDIUM, ChallengeTags.FREE), Icon("SWEET_BERRIES"), true),
    STACK_LIMIT(setOf(ChallengeTags.HARD, ChallengeTags.FREE), Icon("BUNDLE"), true),

    COLLECT_BATTLE(setOf(ChallengeTags.FUN, ChallengeTags.FORCE), Icon("HEART_OF_THE_SEA")),
    IN_TIME(setOf(ChallengeTags.MEDIUM), Icon("CLOCK")),
    MOB_BLOCKS(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("ZOMBIE_HEAD")),
    CHECKPOINTS(setOf(ChallengeTags.FUN), Icon("PLAYER_HEAD", KHeads.BACKWARDS_WHITE)),
    DIM_SWAP(setOf(ChallengeTags.MEDIUM), Icon("END_PORTAL_FRAME")),
    SNAKE(setOf(ChallengeTags.HARD), Icon("RED_CONCRETE_POWDER")),
    REALISTIC(setOf(ChallengeTags.HARD, ChallengeTags.BETA), Icon("OAK_SAPLING")),
    GHOST(setOf(ChallengeTags.FUN), Icon("PLAYER_HEAD", KHeads.GHAST)),
    BLOCK_ASYNC(setOf(ChallengeTags.FUN, ChallengeTags.MULTIPLAYER), Icon("RED_STAINED_GLASS")),
    NO_SAME_ITEM(setOf(ChallengeTags.MEDIUM, ChallengeTags.MULTIPLAYER), Icon("WITHER_ROSE")),
    LIMITED_SKILLS(setOf(ChallengeTags.HARD, ChallengeTags.MULTIPLAYER), Icon("TURTLE_HELMET")),
    RUN_RANDOMIZER(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("GOLDEN_BOOTS")),
    DAMAGE_DUELL(setOf(ChallengeTags.FUN, ChallengeTags.MULTIPLAYER), Icon("IRON_SWORD")),
    ONE_BIOME(setOf(ChallengeTags.MEDIUM), Icon("FILLED_MAP")),
    BOOST_UP(setOf(ChallengeTags.MEDIUM), Icon("SHULKER_SHELL")),
    RIGHT_TOOL(setOf(ChallengeTags.MEDIUM), Icon("WOODEN_AXE")),
    SNEAK_SPAWN(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("HOPPER")),
    STAY_AWAY(setOf(ChallengeTags.HARD), Icon("TNT")),
    RANDOMIZER_BLOCK(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_GREEN)),
    RANDOMIZER_ENTITY(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_ORANGE)),
    RANDOMIZER_BIOMES(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_PURPLE)),
    RANDOMIZER_MOBS(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_BLACK)),
    RANDOMIZER_DAMAGE(setOf(ChallengeTags.MEDIUM, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_RED)),
    RANDOMIZER_CHESTS(setOf(ChallengeTags.FUN, ChallengeTags.RANDOMIZER), Icon("PLAYER_HEAD", KHeads.DICE_BLUE)),
    FORCE_COLLECT(setOf(ChallengeTags.MEDIUM, ChallengeTags.FORCE), Icon("CHEST")),
    NO_DOUBLE_KILL(setOf(ChallengeTags.MEDIUM), Icon("REPEATER")),
    DAMAGER(setOf(ChallengeTags.HARD), Icon("DIAMOND_SWORD")),
    RIVALS_COLLECT(setOf(ChallengeTags.FUN, ChallengeTags.FORCE, ChallengeTags.MULTIPLAYER), Icon("CHEST_MINECART")),
    ROCKET(setOf(ChallengeTags.MEDIUM), Icon("FIREWORK_ROCKET")),
    BLOCK_WORLD(setOf(ChallengeTags.FUN), Icon("DIAMOND_BLOCK")),
    MINEFIELD_WORLD(setOf(ChallengeTags.HARD), Icon("LIGHT_WEIGHTED_PRESSURE_PLATE")),
    BLOCK_WALL(setOf(ChallengeTags.MEDIUM), Icon("BEDROCK")),
    WORLD_DECAY(setOf(ChallengeTags.HARD), Icon("WHITE_STAINED_GLASS")),
    LOW_VISION(setOf(ChallengeTags.HARD), Icon("TINTED_GLASS")),
    CHUNK_SYNC(setOf(ChallengeTags.MEDIUM), Icon("MAGENTA_SHULKER_BOX")),
    HIT_ORDER(setOf(ChallengeTags.MEDIUM), Icon("DIAMOND_AXE")),
    TICK_RATE(setOf(ChallengeTags.MEDIUM, ChallengeTags.BETA), Icon("CLOCK")),
    RHYTHM_CRAFT(setOf(ChallengeTags.HARD, ChallengeTags.BETA), Icon("NOTE_BLOCK")),
    DEATH_HUNT(setOf(ChallengeTags.FUN, ChallengeTags.FORCE), Icon("TOTEM_OF_UNDYING")),
    MLG(setOf(ChallengeTags.HARD), Icon("WATER_BUCKET")),
    BORDER(setOf(ChallengeTags.MEDIUM, ChallengeTags.FORCE), Icon("IRON_BARS"))
    ;


    fun matchingFilter(filter: ChallengeTags): Boolean {
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

            BLOCK_ASYNC -> mapOf("hide" to ChallengeBoolSetting("GLASS_BOTTLE", true))
            NO_SAME_ITEM -> mapOf(
                "lives" to ChallengeIntSetting("BEETROOT", 5, max = 10, min = 1),
                "sync" to ChallengeBoolSetting("REDSTONE", false),
                "info" to ChallengeEnumSetting("WRITABLE_BOOK", NoSameItemEnum.EVERYTHING.name, options = NoSameItemEnum.entries.map { it.name })
            )

            LIMITED_SKILLS -> mapOf("random" to ChallengeBoolSetting("DROPPER", true))
            RUN_RANDOMIZER -> mapOf(
                "goal" to ChallengeIntSetting("CHEST", 500, "b", max = 5000, min = 50, step = 50), "global" to ChallengeBoolSetting("POPPED_CHORUS_FRUIT", true)
            )

            DAMAGE_DUELL -> mapOf("percent" to ChallengeIntSetting("IRON_SWORD", 50, "%", max = 100, min = 5, step = 5))
            ONE_BIOME -> mapOf("delay" to ChallengeIntSetting("CLOCK", 300, "s", max = 1000, min = 30, step = 15))
            BOOST_UP -> mapOf(
                "radius" to ChallengeDoubleSetting("SNOWBALL", 4.0, "b", max = 15.0, min = 0.5),
                "boost" to ChallengeIntSetting("ARROW", 5, max = 20, min = 1),
                "mode" to ChallengeBoolSetting("POLAR_BEAR_SPAWN_EGG", true)
            )

            RIGHT_TOOL -> mapOf("starter" to ChallengeBoolSetting("WOODEN_PICKAXE", true))
            CHUNK_BLOCK_BREAK -> mapOf("bundle" to ChallengeBoolSetting("HOPPER", true))
            SNEAK_SPAWN -> mapOf("onlyMob" to ChallengeBoolSetting("POLAR_BEAR_SPAWN_EGG", true))
            GRAVITY -> mapOf(
                "delay" to ChallengeIntSetting("CLOCK", 180, "s", max = 600, min = 20, step = 10), "duration" to ChallengeIntSetting("REPEATER", 120, "s", max = 600, min = 20, step = 10)
            )

            HP_DRAIN -> mapOf(
                "percentage" to ChallengeIntSetting("BEETROOT", 50, "%", max = 90, min = 5, step = 5),
                "interval" to ChallengeIntSetting("CLOCK", 60 * 10, "s", max = 60 * 60, min = 15, step = 15)
            )

            STAY_AWAY -> mapOf(
                "distance" to ChallengeDoubleSetting("SNOWBALL", 3.0, "b", max = 10.0, min = 0.5), "warning" to ChallengeBoolSetting("CRIMSON_FUNGUS", true)
            )

            RANDOMIZER_BLOCK -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))

            RANDOMIZER_ENTITY -> mapOf(
                "random" to ChallengeBoolSetting("DROPPER", false),
                "itemMode" to ChallengeBoolSetting("CHEST", false),
            )

            RANDOMIZER_BIOMES -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            RANDOMIZER_MOBS -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            FORCE_COLLECT -> mapOf(
                "times" to ChallengeSectionSetting(
                    "CLOCK", mapOf(
                        "minCooldown" to ChallengeIntSetting("GOLD_NUGGET", 255, "s", max = 900, min = 15, step = 15),
                        "maxCooldown" to ChallengeIntSetting("GOLD_INGOT", 345, "s", max = 900, min = 15, step = 15),
                        "minTime" to ChallengeIntSetting("IRON_NUGGET", 180, "s", max = 900, min = 15, step = 15),
                        "maxTime" to ChallengeIntSetting("IRON_INGOT", 360, "s", max = 900, min = 15, step = 15),
                    )
                ), "objects" to ChallengeSectionSetting(
                    "CHEST", mapOf(
                        "items" to ChallengeBoolSetting("HOPPER", true), "biomes" to ChallengeBoolSetting("MAP", false), "height" to ChallengeBoolSetting("LADDER", false)
                    )
                )
            )

            RANDOMIZER_DAMAGE -> mapOf("random" to ChallengeBoolSetting("DROPPER", false))
            NO_DOUBLE_KILL -> mapOf("global" to ChallengeBoolSetting("POPPED_CHORUS_FRUIT", true))
            DAMAGER -> mapOf(
                "mode" to ChallengeEnumSetting("KNOWLEDGE_BOOK", DamagerType.SLOT_CHANGE.name, options = DamagerType.entries.map { it.name }),
                "damage" to ChallengeIntSetting("BEETROOT", 1, "hp", max = 20, min = 1),
                "interval" to ChallengeIntSetting("CLOCK", 1, "s", max = 600, min = 1)
            )

            RIVALS_COLLECT -> mapOf(
                "mode" to ChallengeSectionSetting(
                    "KNOWLEDGE_BOOK", mapOf(
                        "items" to ChallengeBoolSetting("CHEST", true), "biomes" to ChallengeBoolSetting("OAK_SAPLING", false), "mobs" to ChallengeBoolSetting("PHANTOM_SPAWN_EGG", false)
                    )
                ), "joker" to ChallengeIntSetting("ENDER_CHEST", 3, max = 64, min = 0)
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
                ), "yellow" to ChallengeSectionSetting(
                    "YELLOW_CONCRETE", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 2, "s", max = 500, min = 0, step = 1),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 4, "s", max = 500, min = 1, step = 1),
                    )
                ), "red" to ChallengeSectionSetting(
                    "RED_CONCRETE", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 3, "s", max = 500, min = 1, step = 1),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 15, "s", max = 500, min = 2, step = 1),
                    )
                ), "damage" to ChallengeDoubleSetting("BEETROOT", 10.0, "hp", max = 50.0, min = 0.5)
            )

            TRON -> mapOf("visible" to ChallengeBoolSetting("GLASS_BOTTLE", true))
            DISABLED -> mapOf(
                "block" to ChallengeSectionSetting(
                    "STONE_BRICKS", mapOf(
                        "break" to ChallengeBoolSetting("IRON_PICKAXE", false), "place" to ChallengeBoolSetting("GRAVEL", false)
                    )
                ), "interact" to ChallengeSectionSetting(
                    "BREAD", mapOf(
                        "craft" to ChallengeBoolSetting("CRAFTING_TABLE", false), "trade" to ChallengeBoolSetting("EMERALD", false)
                    )
                ), "misc" to ChallengeSectionSetting(
                    "WHEAT_SEEDS", mapOf(
                        "xp" to ChallengeBoolSetting("EXPERIENCE_BOTTLE", false), "items" to ChallengeBoolSetting("HOPPER", false)
                    )
                ), "damage" to ChallengeIntSetting("BEETROOT", 1, "hp")
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
                "delay" to ChallengeIntSetting("CLOCK", 15, "s", max = 600, min = 1), "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", false)
            )

            CHUNK_DECAY -> mapOf(
                "delay" to ChallengeIntSetting("CLOCK", 15, "s", max = 600, min = 1),
                "percentage" to ChallengeIntSetting("COMPARATOR", 5, "%", max = 100, min = 1),
                "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", false)
            )

            CHUNK_CLEARER -> mapOf(
                "breakAll" to ChallengeBoolSetting("GRASS_BLOCK", false), "shouldBreak" to ChallengeBoolSetting("IRON_PICKAXE", true), "bundle" to ChallengeBoolSetting("HOPPER", true)
            )

            ANVIL_CRUSHER -> mapOf(
                "delay" to ChallengeSectionSetting(
                    "CLOCK", mapOf(
                        "startDelay" to ChallengeIntSetting("REDSTONE", 200, "t", max = 1200, min = 20, step = 20),
                        "amplifierDelay" to ChallengeIntSetting("COMPARATOR", 5, "t", max = 60, min = 0, step = 5)
                    )
                ), "density" to ChallengeSectionSetting(
                    "ANVIL", mapOf(
                        "startDensity" to ChallengeIntSetting("REDSTONE", 5, "%", max = 100, min = 1), "amplifierDensity" to ChallengeIntSetting("COMPARATOR", 1, "%", max = 10, min = 0)
                    )
                ),
                "radius" to ChallengeIntSetting("ARROW", 5, "b", max = 15, min = 2),
                "height" to ChallengeIntSetting("RABBIT_FOOT", 10, "b", max = 15, min = 2)
            )

            ITEM_DECAY -> mapOf(
                "time" to ChallengeIntSetting("CLOCK", 300, "s", max = 1800, min = 10, step = 10)
            )

            AREA_TIMER -> mapOf(
                "time" to ChallengeIntSetting("CLOCK", 600, "s", max = 3600, min = 60, step = 10),
                "global" to ChallengeBoolSetting("POPPED_CHORUS_FRUIT", true),
                "mode" to ChallengeEnumSetting("CRAFTING_TABLE", "BIOMES", options = AreaTimerMode.entries.map { it.name })
            )

            COLLECT_BATTLE -> mapOf(
                "maxSetTime" to ChallengeIntSetting("CLOCK", 600, "s", max = 1200, min = 60, step = 10),
                "cooldown" to ChallengeIntSetting("HOPPER", 180, "s", max = 600, min = 30, step = 10),
                "bufferTime" to ChallengeIntSetting("NETHER_STAR", 10, "s", max = 120, min = 0, step = 5)
            )

            BLOCK_WORLD -> mapOf()

            MINEFIELD_WORLD -> mapOf(
                "density" to ChallengeIntSetting("REPEATER", 50, "%", max = 100, min = 1, step = 5)
            )

            BLOCK_WALL -> mapOf(
                "material" to ChallengeEnumSetting("CRAFTING_TABLE", "BEDROCK", options = listOf("BEDROCK", "BARRIER", "LAVA", "COBWEB", "RED_CONCRETE_POWDER")),
                "delay" to ChallengeDoubleSetting("CLOCK", 3.0, "s", max = 30.0, min = 0.5, step = 0.5)
            )

            DAMAGE_MULTIPLIER -> mapOf(
                "multiplier" to ChallengeDoubleSetting("BEETROOT", 2.0, "x", max = 10.0, min = 0.5, step = 0.5)
            )

            RANDOMIZER_CHESTS -> mapOf(
                "enchanting" to ChallengeBoolSetting("ENCHANTED_BOOK", false)
            )

            WORLD_DECAY -> mapOf(
                "delay" to ChallengeIntSetting("CLOCK", 6 * 60, "s", max = 30 * 60, min = 30, step = 15),
                "steps" to ChallengeIntSetting("BOOKSHELF", 10, max = 20, min = 3)
            )

            LOW_VISION -> mapOf(
                "amount" to ChallengeIntSetting("CHEST", 5, "b", min = 1)
            )

            CHUNK_SYNC -> mapOf(
                "env" to ChallengeBoolSetting("TNT", false)
            )

            HIT_ORDER -> mapOf(
                "wrongDamage" to ChallengeDoubleSetting("BEETROOT", 10.0, "hp", max = 50.0, min = 1.0, step = 1.0),
                "randomOrder" to ChallengeBoolSetting("DROPPER", false),
                "visual" to ChallengeBoolSetting("WRITABLE_BOOK", true)
            )

            TICK_RATE -> mapOf(
                "ticks" to ChallengeSectionSetting(
                    "RECOVERY_COMPASS", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 3, "t", max = 100, min = 1, step = 1),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 80, "t", max = 500, min = 10, step = 1)
                    )
                ),
                "timings" to ChallengeSectionSetting(
                    "CLOCK", mapOf(
                        "min" to ChallengeIntSetting("GOLD_NUGGET", 20*8, "t", max = 20*120, min = 30, step = 10),
                        "max" to ChallengeIntSetting("GOLD_INGOT", 820*30, "t", max = 20*120, min = 30, step = 10)
                    )
                )
            )

            RHYTHM_CRAFT -> mapOf()

            DEATH_HUNT -> mapOf()

            MLG -> mapOf(
                "delay" to ChallengeSectionSetting(
                    "CLOCK", mapOf(
                        "minDelay" to ChallengeIntSetting("GOLD_NUGGET", 120, "s", max = 600, min = 30, step = 10),
                        "maxDelay" to ChallengeIntSetting("GOLD_INGOT", 240, "s", max = 600, min = 60, step = 10)
                    )
                ),
                "height" to ChallengeSectionSetting(
                    "RABBIT_FOOT", mapOf(
                        "minHeight" to ChallengeIntSetting("IRON_NUGGET", 50, "b", max = 200, min = 30, step = 5),
                        "maxHeight" to ChallengeIntSetting("IRON_INGOT", 100, "b", max = 200, min = 40, step = 5)
                    )
                ),
                "hardMLGs" to ChallengeBoolSetting("OAK_BOAT", true)
            )

            STACK_LIMIT -> mapOf(
                "limit" to ChallengeIntSetting("BUNDLE", 1, max = 99, min = 1)
            )

            BORDER -> mapOf(
                "radius" to ChallengeSectionSetting(
                    "SPECTRAL_ARROW", mapOf(
                        "start" to ChallengeDoubleSetting("GOLD_BLOCK", 5.0, "b", max = 200.0, min = 0.5, step = 0.5),
                        "step" to ChallengeDoubleSetting("GOLD_INGOT", 1.0, "b", max = 200.0, min = 0.1, step = 0.1)
                    )
                ),
                "mode" to ChallengeEnumSetting("CRAFTING_TABLE", BorderMode.ACHIEVEMENT.name, options = BorderMode.entries.map { it.name }),
                "extra" to ChallengeIntSetting("DIAMOND", 1, max = 100, min = 0, step = 1)
            )
        }
    }
}
package de.miraculixx.mchallenge.modules.competition

enum class CompetitionPointRule(val defaultPoints: Int, val sortOrder: Int) {
    //play through tasks
    FIRST_NETHER_IN(20, 3),
    FIRST_ENDER_EYE(20, 4),
    ENTER_STRONGHOLD(10, 7),
    ENTER_END(15, 95),
    KILL_DRAGON(50, 100),

    //item tasks
    FIRST_IRON_TOOL(5, 1),
    FIRST_GOLD_TOOL(10, 2),
    FIRST_DIAMOND_TOOL(20, 5),
    FIRST_NETHERITE_TOOL(40, 6),
    ELYTRA(50, 96),
}
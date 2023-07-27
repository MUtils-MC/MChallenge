package de.miraculixx.mchallenge.modules.competition

enum class CompetitionPointRule(val defaultPoints: Int, val sortOrder: Int, val display: String) {
    //play through tasks
    FIRST_NETHER_IN(20, 3, "Nether In"),
    FIRST_ENDER_EYE(20, 4, "Ender Eye"),
    ENTER_END(30, 95, "End In"),
    KILL_DRAGON(50, 100, "Kill Dragon"),

    //item tasks
    FIRST_IRON_TOOL(5, 1, "Iron Tools"),
    FIRST_GOLD_TOOL(10, 2, "Gold Tools"),
    FIRST_DIAMOND_TOOL(20, 20, "Diamond Tools"),
    FIRST_NETHERITE_TOOL(40, 40, "Netherite Tools"),
    ELYTRA(50, 96, "Elytra"),

    //misc
    KILL_SNOWMAN(15, 21, "Kill Snowgolem")
}
package de.miraculixx.mutils.enums.settings.gui

enum class GUI(s: String, i: Int) {
    //General
    SELECT_MENU("§9§lMain Menu", 4),
    CUSTOM("",0),

    //Challenges
    CHALLENGE("§9Challenge", 4),
    SETTINGS_IN_TIME("§9Challenge ∙ In Time", 3),
    SETTINGS_CAPTIVE("§9Challenge ∙ Captive", 3),
    SETTINGS_GHOST("§9Challenge ∙ Ghost", 3),
    SETTINGS_NO_SAME_ITEM("§9Challenge ∙ No Same Item", 3),
    SETTINGS_BOOST_UP("§9Challenge ∙ Boost Up", 3),
    SETTINGS_CHUNK_BREAKER("§9Challenge ∙ Chunk Breaker", 3),
    SETTINGS_FORCE_COLLECT("§9Challenge ∙ Force Item", 3),
    SETTINGS_DAMAGER("§9Challenge ∙ Damager", 3),
    SETTINGS_RIVAL_COLLECT("§9Challenge ∙ Rival Collect", 3),

    //Timer
    TIMER_SETTINGS("§9Timer", 4),
    TIMER_DESIGN("§9Timer ∙ Design", 3),
    TIMER_RULES("§9Timer ∙ Settings", 4),
    TIMER_GOALS("§9Timer ∙ Goals", 4),

    //Spectator
    SPEC_SETTINGS("§9Spectator ∙ Settings",3),
    SPEC_TROLL("§9Trolling",3),
    SPEC_TROLL_SOUNDS("§9Trolling ∙ Fake Sounds", 6),
    SPEC_TROLL_BLOCKS("§9Trolling ∙ Fake Blocks", 6),
    SPEC_HOTBAR("",1),
    SPEC_HOTBAR_QUICK("",1),

    //World Manager
    WORLD_MAIN("§9World Manager", 3),
    WORLD_OVERVIEW("§9World ∙ Overview", 6),
    WORLD_GLOBAL_SETTINGS("§9World ∙ Global Settings", 6),

    //Server Settings
    SERVER_SETTINGS("§9Server Settings", 4),
    BANNED_PLAYERS("§9Server Settings ∙ Bans",6),
    WHITELIST_PLAYERS("§9Server Settings ∙ Whitelist", 6),

    //Speedrun
    SPEEDRUN_SETTINGS("§9Speedrun ∙ Settings", 4),

    //Challenge Creator
    CREATOR_MAIN("§9Challenge Creator", 3),
    CREATOR_DELETE("§9Challenge Creator ∙ Delete", 3),
    CREATOR_MODIFY("§9Challenge Creator ∙ Edit", 3),
    CREATOR_LIST("§9Challenge Creator ∙ List", 3);

    val title = s
    val size = i
}
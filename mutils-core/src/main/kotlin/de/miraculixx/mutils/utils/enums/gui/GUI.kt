package de.miraculixx.mutils.utils.enums.gui

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

    //Spectator
    SPEC_SETTINGS("§9Spectator ∙ Settings",3),
    SPEC_TROLL("§9Trolling",3),
    SPEC_TROLL_SOUNDS("§9Trolling ∙ Fake Sounds", 6),
    SPEC_TROLL_BLOCKS("§9Trolling ∙ Fake Blocks", 6),
    SPEC_HOTBAR("",1),
    SPEC_HOTBAR_QUICK("",1);

    val title = s
    val size = i
}
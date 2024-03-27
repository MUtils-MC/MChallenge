package de.miraculixx.challenge.api.data

enum class CustomGameRule(val default: Any, val key: String) {
    PVP(true, "pvpDamage"),
    BLOCK_UPDATES(false, "doBlockUpdates");
}

data class MergedGameRule(
    val name: String,
    val sourceEnum: Any,
    val key: String
)
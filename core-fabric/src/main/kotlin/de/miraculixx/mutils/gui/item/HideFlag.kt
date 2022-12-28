package de.miraculixx.mutils.gui.item

enum class HideFlag(val value: Int) {
    HIDE_ENCHANTS(1),
    HIDE_MODIFIERS(2),
    HIDE_UNBREAKABLE(4),
    HIDE_CAN_DESTROY(8),
    HIDE_CAN_PLACE(16),
    HIDE_OTHERS(32),
    HIDE_DYE(64)
}
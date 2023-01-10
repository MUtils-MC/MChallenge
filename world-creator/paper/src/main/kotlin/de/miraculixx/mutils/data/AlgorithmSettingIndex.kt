package de.miraculixx.mutils.data

import de.miraculixx.mutils.extensions.msg
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component

enum class AlgorithmSettingIndex {
    X1, X2, X3, MODE, RND, INVERT;

    fun getInt(settings: GeneratorData): Int? {
        return when (this) {
            X1 -> settings.x1
            X2 -> settings.x2
            X3 -> settings.x3
            else -> null
        }
    }

    fun getBoolean(settings: GeneratorData): Boolean? {
        return when (this) {
            MODE -> settings.mode
            RND -> settings.rnd
            INVERT -> settings.invert
            else -> null
        }
    }

    fun getString(settings: GeneratorData): String {
        return when (this) {
            X1 -> settings.x1?.toString()
            X2 -> settings.x2?.toString()
            X3 -> settings.x3?.toString()
            MODE -> settings.mode?.msg()
            RND -> settings.rnd?.msg()
            INVERT -> settings.invert?.msg()
        } ?: msgNone
    }

    fun getClickLore(): List<Component> {
        return when (this) {
            X1, X2, X3 -> listOf(msgClickLeft + cmp("+1b"), msgClickRight + cmp("-1b"))
            MODE, RND, INVERT -> listOf(msgClick + msg("Toggle"))
        }
    }
}
package de.miraculixx.api.data.enums

import de.miraculixx.api.data.GeneratorData
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mutils.messages.*
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component

enum class AlgorithmSettingIndex {
    X1, X2, X3, MODE, RND, INVERT, KEY;

    fun <T> set(value: T, settings: GeneratorData) {
        when (this) {
            X1 -> settings.x1 = value as? Int ?: return
            X2 -> settings.x2 = value as? Int ?: return
            X3 -> settings.x3 = value as? Int ?: return
            MODE -> settings.mode = value as? Boolean ?: return
            RND -> settings.rnd = value as? Boolean ?: return
            INVERT -> settings.invert = value as? Boolean ?: return
            KEY -> settings.key = value as? String ?: return
        }
    }

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
            KEY -> settings.key
        } ?: msgNone
    }

    fun getClickLore(): List<Component> {
        return when (this) {
            X1, X2, X3 -> listOf(msgClickLeft + cmp("+1b"), msgClickRight + cmp("-1b"))
            MODE, RND, INVERT -> listOf(msgClick + cmp("Toggle"))
            KEY -> listOf(msgClick + cmp("Change"))
        }
    }
}
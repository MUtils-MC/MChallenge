package de.miraculixx.mtimer.data

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

data class ColorBuilder(
    var type: ColorType,
    var input: String,
    var r: Int,
    var g: Int,
    var b: Int
) {
    fun getColor(): TextColor {
        return when (type) {
            ColorType.RGB -> TextColor.color(r, g, b)
            ColorType.VANILLA -> NamedTextColor.NAMES.valueOr(input, NamedTextColor.WHITE)
            ColorType.HEX_CODE -> TextColor.fromHexString(input) ?: NamedTextColor.WHITE
        }
    }
}

data class GradientBuilder(
    var isAnimated: Boolean,
    val colors: MutableList<ColorBuilder>
)

enum class ColorType {
    VANILLA,
    RGB,
    HEX_CODE
}
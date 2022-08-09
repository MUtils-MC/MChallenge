package de.miraculixx.mutils.utils.tools

import org.bukkit.Material

class ColorTools {

    fun codeToColor(name: Char?) : String{
        return when (name) {
            '0' -> "Black"
            '1' -> "Blue"
            '2' -> "Green"
            '3' -> "Aqua"
            '4' -> "Red"
            '5' -> "Purple"
            '6' -> "Orange"
            '7' -> "Light Gray"
            '8' -> "Gray"
            '9' -> "Light Blue"
            'a' -> "Light Green"
            'b' -> "Light Aqua"
            'c' -> "Light Red"
            'd' -> "Light Purple"
            'e' -> "Yellow"
            'f' -> "White"

            'k' -> "Obfuscated"
            'l' -> "Bold"
            'm' -> "Strikethrough"
            'n' -> "Underlined"
            'o' -> "Italic"
            else -> "Error"
        }
    }

    fun codeToMaterial(name: Char?) : Material {
        return when (name) {
            '0' -> Material.BLACK_DYE
            '1' -> Material.BLUE_DYE
            '2' -> Material.GREEN_DYE
            '3' -> Material.CYAN_DYE
            '4' -> Material.RED_DYE
            '5' -> Material.PURPLE_DYE
            '6' -> Material.ORANGE_DYE
            '7' -> Material.LIGHT_GRAY_DYE
            '8' -> Material.GRAY_DYE
            '9' -> Material.LIGHT_BLUE_DYE
            'a' -> Material.LIME_DYE
            'b' -> Material.LIGHT_BLUE_DYE
            'c' -> Material.PINK_DYE
            'd' -> Material.MAGENTA_DYE
            'e' -> Material.YELLOW_DYE
            'f' -> Material.WHITE_DYE
            else -> Material.BARRIER
        }
    }
}
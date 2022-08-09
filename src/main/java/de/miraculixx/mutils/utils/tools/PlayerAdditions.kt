package de.miraculixx.mutils.utils.tools

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.click() {
    playSound(location, Sound.UI_BUTTON_CLICK, 0.8f, 1f)
}

fun Player.error() {
    playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.1f, 1f)
}

package de.miraculixx.mutils.utils.tools

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.click() {
    playSound(this, Sound.UI_BUTTON_CLICK, 0.7f, 1f)
}

fun Player.soundError() {
    playSound(this, Sound.ENTITY_ENDERMAN_TELEPORT, 1.1f, 1f)
}

fun Player.soundEnable() {
    playSound(this, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
}

fun Player.soundDisable() {
    playSound(this, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
}

fun Player.soundDelete() {
    playSound(this, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 1.2f)
}
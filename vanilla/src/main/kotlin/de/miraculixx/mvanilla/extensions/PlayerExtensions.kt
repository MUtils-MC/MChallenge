package de.miraculixx.mvanilla.extensions

import org.bukkit.entity.Player


fun Player.click() {
    playSound(this, org.bukkit.Sound.UI_BUTTON_CLICK, 0.7f, 1f)
}

fun Player.soundError() {
    playSound(this, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1f)
}

fun Player.soundEnable() {
    playSound(this, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1f)
}

fun Player.soundDisable() {
    playSound(this, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 1f)
}

fun Player.soundDelete() {
    playSound(this, org.bukkit.Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.7f, 1f)
}

fun Player.soundStone() {
    playSound(this, org.bukkit.Sound.BLOCK_STONE_HIT, 0.7f, 1f)
}

fun Player.soundUp() {
    playSound(this, org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1f)
}

fun Player.soundDown() {
    playSound(this, org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1f)
}
package de.miraculixx.mvanilla.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound


fun Audience.click() {
    playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.7f, 1f))
}

fun Audience.soundError() {
    playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 1f, 1.1f))
}

fun Audience.soundEnable() {
    playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f))
}

fun Audience.soundDisable() {
    playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1f, 0.4f))
}

fun Audience.soundDelete() {
    playSound(Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.MASTER, 1f, 1.2f))
}

fun Audience.soundStone() {
    playSound(Sound.sound(Key.key("block.stone.hit"), Sound.Source.MASTER, 1f, 1f))
}

fun Audience.soundUp() {
    playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1.2f))
}

fun Audience.soundDown() {
    playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 0.5f))
}
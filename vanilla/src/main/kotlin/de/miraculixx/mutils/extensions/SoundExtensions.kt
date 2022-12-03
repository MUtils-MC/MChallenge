package de.miraculixx.mutils.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound


fun Audience.click() {
    playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1f, 1f))
}
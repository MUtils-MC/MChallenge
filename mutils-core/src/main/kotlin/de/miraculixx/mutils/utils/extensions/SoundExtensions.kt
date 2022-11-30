package de.miraculixx.mutils.utils.extensions

import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player

fun Player.click() {
    playSound(SoundEvents.UI_BUTTON_CLICK)
}
package de.miraculixx.mutils.gui.utils

import net.kyori.adventure.audience.Audience
import net.minecraft.world.entity.player.Player

fun Player.adv(): Audience {
    return this as Audience
}
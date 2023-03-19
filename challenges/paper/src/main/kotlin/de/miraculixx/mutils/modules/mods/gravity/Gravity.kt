package de.miraculixx.mutils.modules.mods.gravity

import de.miraculixx.kpaper.event.SingleListener
import de.miraculixx.kpaper.event.unregister
import org.bukkit.entity.Player

interface Gravity {
    val classes: List<SingleListener<*>>
    var active: Boolean
    fun unregisterAll() {
        classes.forEach {
            it.unregister()
        }
    }

    fun start() {}

    fun modifyPlayer(player: Player)
}
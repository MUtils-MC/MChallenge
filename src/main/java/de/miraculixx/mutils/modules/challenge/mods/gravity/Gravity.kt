package de.miraculixx.mutils.modules.challenge.mods.gravity

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.unregister

interface Gravity {
    val classes: List<SingleListener<*>>
    var active: Boolean
    fun unregisterAll() {
        classes.forEach {
            it.unregister()
        }
    }
    fun start() {}
}
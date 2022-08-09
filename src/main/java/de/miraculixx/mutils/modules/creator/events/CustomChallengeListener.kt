package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.challenge.Challenge
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.unregister
import org.bukkit.event.Event

interface CustomChallengeListener<T: Event>: Challenge {
    override fun unregister() {
        listener.unregister()
    }

    val listener: SingleListener<T>
}
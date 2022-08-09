package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Cancellable
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerPortalEvent

class DimensionSwap(private val actions: List<(PlayerEvent, Cancellable) -> Unit>) : CustomChallengeListener<PlayerPortalEvent> {
    override fun register() {
        listener.register()
    }

    override val listener: SingleListener<PlayerPortalEvent> = listen {
        actions.forEach { action ->
            action.invoke(it, it)
        }
    }
}
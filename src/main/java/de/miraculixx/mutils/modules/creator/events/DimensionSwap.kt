package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerPortalEvent

class DimensionSwap(actions: List<(Event) -> Unit>) : CustomChallengeListener<PlayerPortalEvent> {
    override fun register() {
        listener.register()
    }

    override val listener: SingleListener<PlayerPortalEvent> = listen(register = false) {
        actions.forEach { action ->
            action.invoke(it)
        }
    }
}
package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
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
        if (it.isCancelled) return@listen
        actions.forEach { action ->
            action.invoke(it)
        }
    }
}
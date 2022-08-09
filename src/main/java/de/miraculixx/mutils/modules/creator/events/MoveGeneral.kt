package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import org.bukkit.event.Cancellable
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerMoveEvent

class MoveGeneral(private val actions: List<((PlayerEvent, Cancellable) -> Unit)>) : CustomChallengeListener<PlayerMoveEvent> {
    override fun register() {
        listener.unregister()
    }

    override val listener: SingleListener<PlayerMoveEvent> = listen {
        actions.forEach { action ->
            action.invoke(it, it)
        }
    }
}
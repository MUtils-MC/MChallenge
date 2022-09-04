package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class MoveGeneral(actions: List<(Event) -> Unit>) : CustomChallengeListener<PlayerMoveEvent> {
    override fun register() {
        listener.register()
    }

    override val listener: SingleListener<PlayerMoveEvent> = listen(register = false) {
        actions.forEach { action ->
            action.invoke(it)
        }
    }
}
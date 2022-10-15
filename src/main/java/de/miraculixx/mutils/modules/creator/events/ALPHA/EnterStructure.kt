package de.miraculixx.mutils.modules.creator.events.ALPHA

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class EnterStructure(actions: List<(Event) -> Unit>) : CustomChallengeListener<PlayerMoveEvent> {
    override val listener: SingleListener<PlayerMoveEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
            actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
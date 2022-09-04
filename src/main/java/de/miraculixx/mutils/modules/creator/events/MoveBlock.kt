package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent

class MoveBlock(actions: List<((Event) -> Unit)>) : CustomChallengeListener<PlayerMoveEvent> {
    override fun register() {
        listener.register()
    }

    override val listener: SingleListener<PlayerMoveEvent> = listen(register = false) {
        if (it.from.block == it.to.block) return@listen
        actions.forEach { action ->
            action.invoke(it)
        }
    }
}
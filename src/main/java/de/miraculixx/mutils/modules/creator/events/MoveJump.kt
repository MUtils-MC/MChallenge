package de.miraculixx.mutils.modules.creator.events

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import org.bukkit.event.Cancellable
import org.bukkit.event.player.PlayerEvent

class MoveJump(private val actions: List<(PlayerEvent, Cancellable) -> Unit>) : CustomChallengeListener<PlayerJumpEvent> {
    override fun register() {
        listener.unregister()
    }

    override val listener: SingleListener<PlayerJumpEvent> = listen {
        actions.forEach { action ->
            action.invoke(it, it)
        }
    }
}
package de.miraculixx.mutils.modules.creator.events

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event

class MoveJump(actions: List<(Event) -> Unit>) : CustomChallengeListener<PlayerJumpEvent> {
    override fun register() {
        listener.register()
    }

    override val listener: SingleListener<PlayerJumpEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        actions.forEach { action ->
            action.invoke(it)
        }
    }
}
package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlace(actions: List<(Event) -> Unit>): CustomChallengeListener<BlockPlaceEvent> {
    override val listener: SingleListener<BlockPlaceEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        actions.forEach { action -> action.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
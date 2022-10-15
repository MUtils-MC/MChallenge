package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerDropItemEvent

class DropItem(actions: List<(Event) -> Unit>): CustomChallengeListener<PlayerDropItemEvent> {
    override val listener: SingleListener<PlayerDropItemEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
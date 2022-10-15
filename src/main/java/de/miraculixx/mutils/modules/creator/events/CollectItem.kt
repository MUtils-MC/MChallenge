package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityPickupItemEvent

class CollectItem(actions: List<(Event) -> Unit>): CustomChallengeListener<EntityPickupItemEvent> {
    override val listener: SingleListener<EntityPickupItemEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        if (it.entity !is Player) return@listen
        actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
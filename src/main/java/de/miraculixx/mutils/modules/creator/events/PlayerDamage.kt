package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent

class PlayerDamage(actions: List<(Event) -> Unit>): CustomChallengeListener<EntityDamageEvent> {
    override val listener: SingleListener<EntityDamageEvent> = listen {
        if (it.isCancelled) return@listen
        if (it.entity !is Player) return@listen
        actions.forEach {  action ->
            action.invoke(it)
        }
    }

    override fun register() {
        listener.register()
    }
}
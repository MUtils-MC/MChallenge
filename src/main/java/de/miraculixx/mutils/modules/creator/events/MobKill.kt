package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MobKill(actions: List<(Event) -> Unit>) : CustomChallengeListener<EntityDamageByEntityEvent> {
    override val listener: SingleListener<EntityDamageByEntityEvent> = listen {
        if (it.entity !is LivingEntity) return@listen
        if ((it.entity as LivingEntity).health - it.finalDamage <= 0.0) {
            actions.forEach { action ->
                action.invoke(it)
            }
        }
    }

    override fun register() {
        listener.register()
    }
}
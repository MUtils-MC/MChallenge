package de.miraculixx.mutils.modules.creator.events

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent

class MobDamage(actions: List<(Event) -> Unit>): CustomChallengeListener<EntityDamageEvent> {
    override val listener: SingleListener<EntityDamageEvent> = listen(register = false) {
        if (it !is LivingEntity) return@listen
        actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MobDamageByPlayer(actions: List<(Event) -> Unit>) : CustomChallengeListener<EntityDamageByEntityEvent> {
    override val listener: SingleListener<EntityDamageByEntityEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        if (it.entity !is LivingEntity) return@listen
        if (it.damager !is Player) return@listen
        actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
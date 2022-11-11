package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDeathEvent

class MobDeath(actions: List<(Event) -> Unit>) : CustomChallengeListener<EntityDeathEvent> {
    override val listener = listen<EntityDeathEvent>(register = false) {
        if (it.isCancelled) return@listen
        actions.forEach { action ->
            action.invoke(it)
        }
    }

    override fun register() {
        listener.register()
    }
}
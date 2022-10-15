package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.entity.FoodLevelChangeEvent

class PlayerHunger(actions: List<(Event) -> Unit>): CustomChallengeListener<FoodLevelChangeEvent> {
    override val listener: SingleListener<FoodLevelChangeEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        if (it.entity.foodLevel <= it.foodLevel) return@listen
        actions.forEach { event -> event.invoke(it) }
    }

    override fun register() {
        listener.register()
    }
}
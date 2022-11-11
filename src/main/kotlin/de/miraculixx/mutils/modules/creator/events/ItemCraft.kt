package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.inventory.CraftItemEvent

class ItemCraft(actions: List<(Event) -> Unit>): CustomChallengeListener<CraftItemEvent> {
    override val listener: SingleListener<CraftItemEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        actions.forEach { action ->
            action.invoke(it)
        }
    }

    override fun register() {
        listener.register()
    }
}
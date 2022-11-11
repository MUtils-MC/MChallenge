package de.miraculixx.mutils.modules.creator.events

import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType

class OpenContainer(actions: List<(Event) -> Unit>): CustomChallengeListener<InventoryOpenEvent> {
    override val listener: SingleListener<InventoryOpenEvent> = listen(register = false) {
        if (it.isCancelled) return@listen
        val type = it.inventory.type
        if (type != InventoryType.PLAYER && type != InventoryType.CRAFTING)
        actions.forEach { action ->
            action.invoke(it)
        }
    }

    override fun register() {
        listener.register()
    }
}
package de.miraculixx.mtimer.events

import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.event.EventScope
import net.silkmc.silk.core.event.Events


@Suppress("UnusedReceiverParameter") // receiver is for namespacing only
val Events.CustomWorld get() = CustomWorldEvents

object CustomWorldEvents {

    open class MobEvent(val entity: LivingEntity)

    val afterMobDeath = Event.onlySync<MobEvent, EventScope.Empty> {
        EventScope.Empty
    }
}
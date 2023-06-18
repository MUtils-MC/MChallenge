package de.miraculixx.mtimer.events

import net.minecraft.server.level.ServerLevel
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.event.EventScope
import net.silkmc.silk.core.event.Events

@Suppress("UnusedReceiverParameter") // receiver is for namespacing only
val Events.CustomServer get() = CustomServerEvents

object CustomServerEvents {

    open class WorldEvent(val level: ServerLevel, var isCancelled: Boolean)

    val preWorldTick = Event.onlySync<WorldEvent, EventScope.Cancellable> {
        EventScope.Cancellable()
    }
}
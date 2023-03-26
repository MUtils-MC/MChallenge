package de.miraculixx.mutils.modules.challenges

import de.miraculixx.api.MChallengeAPI
import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.SingleListener
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

abstract class AbstractPaperChallenge : Challenge {
    protected val listeners = mutableListOf<Listener>()

    //TODO wozu überhaupt hä
    override fun register() {
    }

    override fun unregister() {
        listeners.forEach(Listener::unregister)
    }

    protected inline fun <reified T : Event> onEvent(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        register: Boolean = true,
        crossinline onEvent: (event: T) -> Unit,
    ): SingleListener<T> {
        val listener = object : SingleListener<T>(priority, ignoreCancelled) {
            override fun onEvent(event: T) {
                //TODO gg anscheinend kann man challenges auch pausieren???
                if (isActive() && !isPaused()) {
                    onEvent.invoke(event)
                } else {
                    Unit
                }
            }
        }
        if (register) listener.register()
        return listener
    }

    protected open fun isActive(): Boolean {
        return MChallengeAPI.instance?.getChallenges()?.contains(this) == true
    }

    //TODO
    protected open fun isPaused(): Boolean {
        return false
    }
}

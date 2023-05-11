@file:Suppress("unused")

package de.miraculixx.mtimer.module

import de.miraculixx.mtimer.events.CustomPlayer
import de.miraculixx.mtimer.events.CustomServer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mutils.gui.await.AwaitChatMessage
import net.silkmc.silk.core.event.Events

object TimerListener {
    val worldTick = Events.CustomServer.preWorldTick.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val blockPlace = Events.CustomPlayer.preBlockPlace.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val blockBreak = Events.CustomPlayer.preBlockBreak.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onDamage = Events.CustomPlayer.onFinalDamage.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onHunger = Events.CustomPlayer.preHungerChange.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onItemUse = Events.CustomPlayer.preUseItem.listen { event ->
        event.isCancelled = rules.freezeWorld && !TimerManager.globalTimer.running
    }

    val onChat = Events.CustomPlayer.preSendChatMessage.listen { event ->
        val message = event.message.message()
        val sender = event.player
        AwaitChatMessage.awaitingMessages.forEach { (_, awaiter) -> awaiter.triggerChat(sender, message) }
    }
}
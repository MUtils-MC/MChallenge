package de.miraculixx.mutils.gui.await

import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.title.Title
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import java.time.Duration

class AwaitChatMessage(
    private val sync: Boolean,
    private val player: ServerPlayer,
    name: String,
    maxSeconds: Int,
    private val before: String?,
    private val advancedMode: Boolean,
    initMessage: Component,
    private val onChat: (String) -> Unit,
    private val callback: () -> Unit
) {
    companion object {
        val awaitingMessages: MutableMap<ServerPlayer, AwaitChatMessage> = mutableMapOf()
    }

    private var counter = maxSeconds
    fun triggerChat(sender: ServerPlayer, message: String): Boolean {
        if (sender != player) return false
        val final = if (advancedMode) {
            when (message) {
                "#exit" -> before ?: ""
                "#clear" -> ""
                else -> message.replace('_', ' ')
            }
        } else message

        if (sync) mcCoroutineTask(true) { onChat.invoke(final) }
        else onChat.invoke(final)
        stop()
        return true
    }

    private val scheduler = mcCoroutineTask(sync, period = 20.ticks, howOften = counter + 1L) run@{
        if (counter <= 0) {
            stop()
            return@run
        }
        player.showTitle(Title.title(cmp("Enter $name", cHighlight), cmp("${counter}s"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)))
        player.addEffect(MobEffectInstance(MobEffects.BLINDNESS, -1, 1, false, false, false))
        counter--
    }

    private fun stop() {
        scheduler.cancel()
        awaitingMessages.remove(player)
        player.showTitle(Title.title(emptyComponent(), emptyComponent()))
        player.removeEffect(MobEffects.BLINDNESS)
        callback.invoke()
    }

    init {
        awaitingMessages[player]?.stop()
        awaitingMessages[player] = this
        player.closeContainer()
        player.sendMessage(initMessage)
        if (advancedMode && before != null) {
            val realBefore = before.replace(' ', '_')
            player.sendMessage(
                prefix + (cmp(realBefore) + cmp(" (copy)", cMark)).addHover(msg("event.clickToCopy", listOf(realBefore)))
                    .clickEvent(ClickEvent.suggestCommand(realBefore))
            )
        }
    }
}
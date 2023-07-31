package de.miraculixx.mcore.await

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.extensions.native
import de.miraculixx.mvanilla.extensions.sendMessage
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class AwaitChatMessage(
    sync: Boolean,
    private val player: Player,
    name: String,
    maxSeconds: Int,
    before: String?,
    private val advancedMode: Boolean,
    initMessage: Component,
    onChat: (String) -> Unit,
    private val callback: () -> Unit
) {
    private var counter = maxSeconds

    private val onChat = listen<AsyncPlayerChatEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
        val message = it.message
        val final = if (advancedMode) {
            when (message) {
                "#exit" -> before ?: ""
                "#clear" -> ""
                else -> message.replace('_', ' ')
            }
        } else message
        if (sync) sync { onChat.invoke(final) }
        else onChat.invoke(final)
        stop()
    }

    private val scheduler = task(sync, 0, 20) run@{
        if (counter <= 0) {
            stop()
            return@run
        }
        player.sendTitle(cmp("Enter $name", cHighlight).native(), cmp("${counter}s").native(), 0, 5, 0)
        sync {
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
        }
        counter--
    }

    private fun stop() {
        scheduler?.cancel()
        onChat.unregister()
        player.sendMessage("", "")
        sync {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            callback.invoke()
        }
    }

    init {
        player.closeInventory()
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
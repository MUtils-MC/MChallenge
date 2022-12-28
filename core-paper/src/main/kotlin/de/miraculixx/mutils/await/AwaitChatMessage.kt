package de.miraculixx.mutils.await

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.messages.*
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

class AwaitChatMessage(sync: Boolean, private val player: Player, name: String, maxSeconds: Int, before: String?, onChat: (String) -> Unit, private val callback: () -> Unit) {
    var counter = maxSeconds

    private val onChat = listen<AsyncChatEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
        val message = plainSerializer.serialize(it.message()).replace('_', ' ')
        val final = when (message) {
            "#exit" -> before ?: ""
            "#clear" -> ""
            else -> message
        }
        if (sync) sync { onChat.invoke(final) }
        else onChat.invoke(final)
        stop()
    }

    private val scheduler = task(sync, 0, 20) run@{
        if (counter <= 0) {
            stop()
            return@run
        }
        player.showTitle(Title.title(cmp("Enter $name", cHighlight), cmp("${counter}s"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)))
        sync {
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
        }
        counter--
    }

    private fun stop() {
        scheduler?.cancel()
        onChat.unregister()
        player.showTitle(Title.title(emptyComponent(), emptyComponent()))
        sync {
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            callback.invoke()
        }
    }

    init {
        player.closeInventory()
        player.sendMessage(prefix + msg("event.spaceInfo"))
        if (!before.isNullOrBlank())
            player.sendMessage(prefix + (cmp(before) + cmp(" (copy)", cMark)).addHover(msg("event.clickToCopy", listOf(before)))
                .clickEvent(ClickEvent.suggestCommand(buildString {
                    if (before[0] == ' ') {
                        append("_${before.substring(1)}")
                    }
                    if (length > 1 && last() == ' ') {
                        this[lastIndex] = '_'
                    }
                })))
    }
}
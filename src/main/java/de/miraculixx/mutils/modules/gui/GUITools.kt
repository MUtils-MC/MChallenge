package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.utils.challengeOfTheMonth
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.plainSerializer
import de.miraculixx.mutils.utils.premium
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.error
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class GUITools(private val c: FileConfiguration?) {

    fun enumRotate(enum: List<*>, current: Any, player: Player? = null): Any? {
        player?.click()
        val currentValue = enum.lastIndexOf(current)
        val lastValue = enum.size - 1
        return if (currentValue < lastValue) enum[currentValue + 1]
        else enum[0]
    }

    fun navigate(p: Player, i: Int, g: GUI, s: GUIState, items: Map<ItemStack, Boolean>? = null) {
        val b = GUIBuilder(p, g)
        if (s == GUIState.SCROLL) b.scroll(i, items) else b.storage(null, items)
        b.open()
        p.click()
    }

    fun colorRotate(code: Char?): Char {
        return when (code) {
            '0' -> '1'
            '1' -> '2'
            '2' -> '3'
            '3' -> '4'
            '4' -> '5'
            '5' -> '6'
            '6' -> '7'
            '7' -> '8'
            '8' -> '9'
            '9' -> 'a'
            'a' -> 'b'
            'b' -> 'c'
            'c' -> 'd'
            'd' -> 'e'
            'e' -> 'f'
            'f' -> '0'
            else -> '6'
        }
    }

    fun styleRotate(code: Char?): Char {
        return when (code) {
            'k' -> 'l'
            'l' -> 'm'
            'm' -> 'n'
            'n' -> 'o'
            'o' -> 'k'
            else -> 'l'
        }
    }

    fun toggleSetting(p: Player, s: String) {
        if (c == null) return
        if (c.getBoolean(s)) {
            c[s] = false
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
        } else {
            c[s] = true
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }
    }

    fun numberChanger(p: Player, t: ClickType, s: String, step: Int = 1, min: Int = 0, max: Int = 100) {
        val b = when (t) {
            ClickType.LEFT -> true
            ClickType.RIGHT -> false
            else -> return
        }
        settings(b, p, s, step.toDouble(), max.toDouble(), min.toDouble())
    }

    fun numberChangerShift(p: Player, t: ClickType, s: String, step: Int = 1, min: Int = 0, max: Int = 100) {
        val b = when (t) {
            ClickType.RIGHT -> true
            ClickType.SHIFT_RIGHT -> false
            else -> return
        }
        settings(b, p, s, step.toDouble(), max.toDouble(), min.toDouble())
    }

    fun numberChangerShift(p: Player, t: ClickType, s: String, step: Double = 1.0, min: Double = 0.0, max: Double = 100.0) {
        val b = when (t) {
            ClickType.RIGHT -> true
            ClickType.SHIFT_RIGHT -> false
            else -> return
        }
        settings(b, p, s, step, max, min)
    }

    private fun settings(up: Boolean, p: Player, s: String, step: Double, max: Double, min: Double) {
        if (c == null) return
        if (up) {
            if (c.getDouble(s) >= max) {
                p.playSound(p.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                return
            }
            c[s] = c.getDouble(s) + step
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.2f)
        } else {
            if (c.getDouble(s) <= min) {
                c[s] = min
                p.playSound(p.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                return
            }
            c[s] = c.getDouble(s) - step
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 0.5f)
        }
    }

    fun verify(module: Modules, player: Player?): Boolean {
        return if (!premium && module != challengeOfTheMonth) {
            player?.closeInventory()
            player?.error()
            player?.sendMessage(msg("command.verify.noPremium"))
            false
        } else true
    }


    class AwaitChat(
        private val config: FileConfiguration?,
        private val destination: String,
        private val player: Player,
        private val callback: (() -> Unit)? = null
    ) {
        private var counter = 60
        private val counterTop = msg("modules.serverSettings.counter", pre = false)
        private val counterSub = msg("modules.serverSettings.counterSub", pre = false)

        private val onChat = listen<AsyncChatEvent> {
            if (it.player != player) return@listen
            it.isCancelled = true
            val message = plainSerializer.serialize(it.message())
            if (message.lowercase().contains("exit")) {
                finish(null)
                return@listen
            }
            finish(message)
        }

        val task = task(false, 20, 20) {
            player.title(counterTop, counterSub.replace("<INPUT>", counter.toString()), 0, 30, 0)
            if (counter == 0) {
                it.cancel()
                finish(null)
                return@task
            }
            counter--
        }

        private fun finish(value: String?) {
            val finalValue = ChatColor.translateAlternateColorCodes('&', value ?: "null")
            task?.cancel()
            if (finalValue != "null") config?.set(destination, finalValue)
            onChat.unregister()
            sync {
                callback?.invoke()
            }
            player.sendMessage(msg("command.awaitFinish", player))
        }

        init {
            player.closeInventory()
            player.sendMessage(msg("command.awaitString", player))
        }
    }
}
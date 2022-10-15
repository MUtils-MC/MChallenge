package de.miraculixx.mutils.commands.tools

import de.miraculixx.mutils.utils.TPS
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.toLag
import de.miraculixx.mutils.utils.tools.toMemPercent
import de.miraculixx.mutils.utils.tools.toMemory
import de.miraculixx.mutils.utils.tools.toTPS
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*

object GlobalOverride {

    val onCommand = listen<PlayerCommandPreprocessEvent> {
        val message = it.message.lowercase(Locale.getDefault())
        val player = it.player
        when {
            message == "/reload" || message == "/rl" -> {
                it.isCancelled = true
                if (!player.hasPermission("mutils.command.reload")) return@listen
                player.sendMessage(msg("command.reload.reload", player))
                Bukkit.reload()
                player.sendMessage(msg("command.reload.success", player))
            }

            it.message == ("/tps") -> {
                val r = Runtime.getRuntime()
                val memUsed: Long = (r.totalMemory() - r.freeMemory()) / 1048576L
                val memMax: Long = r.maxMemory() / 1048576L
                it.player.sendMessage("$prefix §6Sync TPS ≫ ${TPS.getTPS().toTPS()} §8§l|§6 Sync Lag ≫ ${TPS.getTPS().toLag()}")
                it.player.sendMessage("$prefix §6Memory Usage ≫ ${memUsed.toMemory(memMax)} §6(${memUsed.toMemPercent(memMax)}%§6)")
            }
        }
    }
}
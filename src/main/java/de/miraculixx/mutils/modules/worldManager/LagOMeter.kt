package de.miraculixx.mutils.modules.worldManager

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.utils.TPS
import de.miraculixx.mutils.utils.tools.toLag
import de.miraculixx.mutils.utils.tools.toTPS
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.util.*

class LagOMeter {
    private val key = NamespacedKey(Main.INSTANCE, "lagometer-${UUID.randomUUID()}")
    private val bossBar = Bukkit.createBossBar(key, "§c§o...", BarColor.RED, BarStyle.SOLID)
    private var active = true

    init {
        bossBar.isVisible = true
        bossBar.progress = 1.0
        run()
    }

    private fun run() {
        task(true, 1, 5) {
            if (!active) {
                it.cancel()
                return@task
            }
            var tpsI = TPS.getTPS()
            if (tpsI > 20.0) tpsI = 20.0
            if (tpsI < 0) tpsI = 0.0
            for (player in onlinePlayers) {
                bossBar.addPlayer(player)
                bossBar.setTitle("§6Sync TPS §7≫ ${tpsI.toTPS()} §8§o§l|§6 Lag §7≫ ${tpsI.toLag()}")
            }
        }
    }

    fun stop() {
        if (!active) return
        active = false
        bossBar.isVisible = false
        bossBar.removeAll()
        Bukkit.removeBossBar(key)
    }
}
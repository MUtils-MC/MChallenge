package de.miraculixx.mchallenge.modules.mods.areaTimer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.modules.mods.areaTimer.AreaTimerMode
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mchallenge.modules.challenges.getFormatted
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AreaTimer : Challenge {
    // Max Time
    // Global?
    private val maxTime: Int
    private val globalCount: Boolean
    private val mode: AreaTimerMode
    private val startRed: Int
    private val startYellow: Int
    private val msgRemaining = msgString("event.timer.remaining")

    private var paused = true
    private var stopped = false
    private val barMap = mutableMapOf<UUID, BossBar>()
    private val timerMap = mutableMapOf<String, Duration>()

    init {
        val settings = challenges.getSetting(Challenges.AREA_TIMER).settings
        maxTime = settings["time"]?.toInt()?.getValue() ?: 600
        globalCount = settings["global"]?.toBool()?.getValue() ?: true
        mode = enumOf<AreaTimerMode>(settings["mode"]?.toEnum()?.getValue() ?: "BIOMES") ?: AreaTimerMode.BIOMES

        val segments = maxTime / 3
        startRed = segments
        startYellow = startRed * 2
    }

    override fun register() {
        paused = false
    }

    override fun unregister() {
        paused = true
    }

    override fun stop() {
        paused = true
        stopped = true
        barMap.forEach { (uuid, bar) ->
            Bukkit.getPlayer(uuid)?.hideBossBar(bar)
        }
        barMap.clear()
        timerMap.clear()
    }

    val scheduler = task(false, 0, 20) {
        if (stopped) it.cancel()
        if (paused) return@task
        val timerSet = mutableSetOf<Duration>()
        onlinePlayers.forEach { player ->
            val uuid = player.uniqueId

            val loc = player.location
            val key = when (mode) {
                AreaTimerMode.BIOMES -> loc.block.biome.name.replace('_', ' ')
                AreaTimerMode.CHUNKS -> {
                    val chunk = loc.chunk
                    "Chunk ${chunk.x} ${chunk.z}"
                }
                AreaTimerMode.HEIGHT -> "Y: ${loc.blockY}"
                AreaTimerMode.WORLD -> loc.world.environment.name.replace('_', ' ')
            }
            val timer = timerMap.getOrPut(key) { maxTime.seconds }
            if (!Spectator.isSpectator(uuid) && player.gameMode == GameMode.SURVIVAL) {
                if (timer.inWholeSeconds <= 0) {
                    player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "timeRunOut")
                    sync { player.damage(999.0) }
                }

                if (!timerSet.contains(timer)) {
                    val newTime = timer - 1.seconds
                    timerMap[key] = newTime
                    timerSet.add(newTime)
                }
            }

            val bar = barMap.getOrPut(uuid) { BossBar.bossBar(cmp("Waiting for server...", cError), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS) }
            bar.modifyBar(timer, key)
            player.showBossBar(bar)
        }
    }

    private fun BossBar.modifyBar(time: Duration, key: String) {
        name(cmp("$msgRemaining: ") + cmp(time.getFormatted(), cHighlight) + cmp(" ($key)"))
        val remaining = time.inWholeSeconds
        progress(remaining.toFloat() / maxTime)
        val color = when (remaining) {
            in 0 until startRed -> BossBar.Color.RED
            in startRed until startYellow -> BossBar.Color.YELLOW
            else -> BossBar.Color.GREEN
        }
        color(color)
    }
}
package de.miraculixx.mchallenge.modules.mods.misc.trafficlight

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

class TrafficLight : Challenge {
    private val bar = BossBar.bossBar(cmp("Waiting for server...", cHighlight), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    private val damageCooldown: MutableList<Player> = mutableListOf()
    private var running = true

    private val damage: Double
    private val minGreen: Int
    private val maxGreen: Int
    private val minYellow: Int
    private val maxYellow : Int
    private val minRed: Int
    private val maxRed: Int

    private var state = TrafficLightState.RED
    private var timeToNext = 0

    private val msgConnector = cmp("  ", strikethrough = true)
    private val msgContainerOff = cmp("[⬜⬜⬜⬜]")
    private val msgHangRight = cmp("└")
    private val msgHangLeft = cmp("┘")

    init {
        val settings = challenges.getSetting(Challenges.TRAFFIC_LIGHT).settings
        val greenSection = settings["green"]?.toSection()?.getValue()
        minGreen = greenSection?.get("min")?.toInt()?.getValue() ?: (30)
        maxGreen = greenSection?.get("max")?.toInt()?.getValue() ?: (90)

        val yellowSection = settings["yellow"]?.toSection()?.getValue()
        minYellow = yellowSection?.get("min")?.toInt()?.getValue() ?: (2)
        maxYellow = yellowSection?.get("max")?.toInt()?.getValue() ?: (4)

        val redSection = settings["red"]?.toSection()?.getValue()
        minRed = redSection?.get("min")?.toInt()?.getValue() ?: (3)
        maxRed = redSection?.get("max")?.toInt()?.getValue() ?: (10)

        damage = settings["damage"]?.toDouble()?.getValue() ?: (10.0)
    }

    override fun register() {
        onMove.register()
        onJoin.register()
        running = true
    }

    override fun unregister() {
        onMove.unregister()
        onJoin.unregister()
        running = false
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { player -> player.showBossBar(bar) }
        return true
    }

    override fun stop() {
        scheduler?.cancel()
        onlinePlayers.forEach { player -> player.hideBossBar(bar) }
    }

    private val scheduler = task(false, 0, 20) {
        if (!running) return@task
        if (timeToNext <= 0) {
            state = when (state) {
                TrafficLightState.RED -> {
                    bar.name(msgHangRight + getContainerOn(NamedTextColor.DARK_GREEN) + msgConnector + msgContainerOff + msgConnector + msgContainerOff + msgHangLeft)
                    bar.color(BossBar.Color.GREEN)
                    bar.progress(1f)
                    onlinePlayers.forEach { player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1.5f) }
                    timeToNext = Random.nextInt(minGreen..maxGreen)
                    TrafficLightState.GREEN
                }

                TrafficLightState.YELLOW -> {
                    bar.name(msgHangRight + msgContainerOff + msgConnector + msgContainerOff + msgConnector + getContainerOn(NamedTextColor.RED) + msgHangLeft)
                    bar.color(BossBar.Color.RED)
                    bar.progress(0f)
                    onlinePlayers.forEach { player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.8f) }
                    timeToNext = Random.nextInt(minRed..maxRed)
                    TrafficLightState.RED
                }

                TrafficLightState.GREEN -> {
                    bar.name(msgHangRight + msgContainerOff + msgConnector + getContainerOn(NamedTextColor.YELLOW) + msgConnector + msgContainerOff + msgHangLeft)
                    bar.color(BossBar.Color.YELLOW)
                    bar.progress(0.5f)
                    onlinePlayers.forEach { player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1f, 0.8f) }
                    timeToNext = Random.nextInt(minYellow..maxYellow)
                    TrafficLightState.YELLOW
                }
            }
        } else timeToNext--
    }

    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
//        if (Spectator.isSpectator(player.uniqueId)) return@listen TODO
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return@listen
        val from = it.from
        val to = it.to
        if (abs(from.x - to.x) < 0.06 && abs(from.y - to.y) < 0.06 && abs(from.z - to.z) < 0.06) return@listen
        if (state == TrafficLightState.RED && !damageCooldown.contains(player)) {
            player.damage(damage)
            damageCooldown.add(player)
            taskRunLater(30) { damageCooldown.remove(player) }
        }
    }

    private val onJoin = listen<PlayerJoinEvent> {
        it.player.showBossBar(bar)
    }

    private fun getContainerOn(color: TextColor): Component {
        return cmp("[") + cmp("⬛⬛⬛⬛", color) + cmp("]")
    }
}

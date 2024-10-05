package de.miraculixx.mchallenge.modules.mods.worldChanging.border

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.KPaperRunnable
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mchallenge.utils.serializer.Vector2dSerializer
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.text.addCommand
import de.miraculixx.mcommons.text.defaultLocale
import de.miraculixx.mcommons.text.msg
import kotlinx.serialization.Serializable
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
import net.minecraft.world.level.border.WorldBorder
import org.bukkit.World
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.joml.Vector2d
import java.io.File

class BorderChallenge : Challenge {
    private val radiusStep: Double
    private val mode: BorderMode
    private val extra: Int

    private var radius: Double
    private val center: MutableMap<World.Environment, Vector2d> = mutableMapOf()
    private var running = false

    private val dataFile = File("${MChallenge.configFolder.path}/data/border.json")

    init {
        val settings = challenges.getSetting(Challenges.BORDER).settings
        val radiusSection = settings["radius"]?.toSection()?.getValue()
        radius = (radiusSection?.get("start")?.toDouble()?.getValue() ?: 5.0)
        radiusStep = (radiusSection?.get("step")?.toDouble()?.getValue() ?: 1.0)
        mode = enumOf<BorderMode>(settings["mode"]?.toEnum()?.getValue() ?: BorderMode.ACHIEVEMENT.name) ?: BorderMode.ACHIEVEMENT
        extra = settings["extra"]?.toInt()?.getValue() ?: 0
    }

    override fun register() {
        onJoin.register()
        onDimSwitch.register()
        when (mode) {
            BorderMode.ACHIEVEMENT -> onAchievement.register()
            BorderMode.TIMED -> Unit
        }
        running = true
    }

    override fun unregister() {
        onJoin.unregister()
        onAchievement.unregister()
        onDimSwitch.unregister()
        running = false

        dataFile.saveConfig(BorderData(radius, center))
    }

    override fun start(): Boolean {
        center.clear()

        if (dataFile.exists()) {
            val data = dataFile.loadConfig(BorderData())
            radius = data.radius
            center.putAll(data.center)
        }

        onlinePlayers.forEach {
            center[it.world.environment] = Vector2d(it.location.blockX + 0.5, it.location.blockZ + 0.5)
        }
        when (mode) {
            BorderMode.ACHIEVEMENT -> broadcast(defaultLocale.msg("event.border.start").addCommand("/advancement revoke @a everything"))
            BorderMode.TIMED -> startTimed()
        }
        updateBorder(0.0, null)
        return true
    }

    override fun stop() {
        task?.cancel()
        broadcast(defaultLocale.msg("event.border.stop"))
        if (onlinePlayers.isNotEmpty()) dataFile.delete()
    }


    private val onJoin = listen<PlayerJoinEvent> { event ->
        task(true, 0, 10, 2) {
            updateBorder(0.0, event.player)
        }
    }

    private val onDimSwitch = listen<PlayerChangedWorldEvent> {
        val player = it.player
        val env = player.world.environment
        if (!center.containsKey(env)) {
            center[env] = Vector2d(player.location.blockX + 0.5, player.location.blockZ + 0.5)
        }
        updateBorder(0.0, player)
    }

    // Advancement mode only
    private val onAchievement = listen<PlayerAdvancementDoneEvent> {
        if (it.advancement.display == null) return@listen
        updateBorder(radiusStep, it.player)
    }

    private var task: KPaperRunnable? = null
    private fun startTimed() {
        // (set-minutes) * 60(seconds) * 20(ticks)
        val delay = extra * 60 * 20L
        task = task(true, delay, delay) {
            if (!running) return@task
            updateBorder(radiusStep, null)
        }
    }


    /**
     * Updates the border for all players or a specific player.
     * The Border will be animated from radius to radius + change.
     */
    private fun updateBorder(change: Double, player: Player?) {
        val targets = if (player == null) onlinePlayers else setOf(player)
        targets.forEach {
            val dummyBorder = WorldBorder().apply {
                size = radius
                val center = center[it.world.environment] ?: Vector2d(0.0, 0.0)
                setCenter(center.x, center.y)
                world = (it.world as CraftWorld).handle

                lerpSizeBetween(size, radius + change, 1000 * 3)
            }
            val serverPlayer = (it as CraftPlayer).handle
            serverPlayer.connection.send(ClientboundInitializeBorderPacket(dummyBorder))
        }

        radius += change
    }

    @Serializable
    private data class BorderData(
        val radius: Double = 5.0,
        val center: Map<World.Environment, @Serializable(with = Vector2dSerializer::class) Vector2d> = emptyMap()
    )
}
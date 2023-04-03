package de.miraculixx.mchallenge.modules.mods.gravity

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.modules.mods.gravity.GravityState
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.msg
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffectType

class GravityManager : Challenge {
    override val challenge = Challenges.GRAVITY
    private val bar = BossBar.bossBar(cmp("⇔ Normal Gravity ⇔", NamedTextColor.YELLOW), 1f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS)
    private val delayDefault: Int
    private val durationCustom: Int
    private var current = GravityState.DEFAULT
    private var gClass: Gravity? = null

    private var running = false
    private var stop = false

    init {
        val settings = challenges.getSetting(challenge).settings
        delayDefault = settings["delay"]?.toInt()?.getValue() ?: 180
        durationCustom = settings["duration"]?.toInt()?.getValue() ?: 120
    }

    override fun start(): Boolean {
        running = true
        run()
        onlinePlayers.forEach { p -> p.showBossBar(bar) }
        return true
    }

    override fun stop() {
        running = false
        stop = true
        gClass?.active = false
        gClass?.unregisterAll()
        onlinePlayers.forEach { p -> p.hideBossBar(bar) }

        resetGravity()
    }

    override fun register() {
        running = true
        onJoin.register()
        if (current != GravityState.DEFAULT) {
            gClass = when (current) {
                GravityState.HIGH -> HighGravity()
                GravityState.LOW -> LowGravity()
                GravityState.ANTI -> AntiGravity()
                GravityState.NO -> NoGravity()
                else -> null
            }
            gClass?.start()
        }
    }

    override fun unregister() {
        running = false
        onJoin.unregister()
        gClass?.unregisterAll()
        gClass = null
    }

    private fun run() {
        var last = GravityState.DEFAULT
        var counter = delayDefault
        task(false, 0, 20) {
            if (stop) it.cancel()
            if (!running) return@task

            //WARNING 10 to 0
            if (current == GravityState.DEFAULT && counter <= 10) {
                bar.name(msg("event.gravity.switch", listOf(counter.toString())))
                bar.color(BossBar.Color.RED)
                val pitch = (counter / 10.0 + 1).toFloat()
                if (counter == 0) {
                    onlinePlayers.forEach { p -> p.playSound(p, Sound.BLOCK_BEACON_POWER_SELECT, .8f, .1f) }
                    val pair = newGravity(last)
                    last = pair.first
                    current = last
                    gClass = pair.second
                    counter = durationCustom
                } else onlinePlayers.forEach { p -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, .3f, pitch) }

            } else if (current != GravityState.DEFAULT && counter == 0) { //RESET TO NORMAL
                gClass?.unregisterAll()
                gClass?.active = false
                gClass = null
                sync { resetGravity() }
                bar.name(cmp("⇔ Normal Gravity ⇔", NamedTextColor.YELLOW))
                bar.color(BossBar.Color.YELLOW)
                onlinePlayers.forEach { p -> p.playSound(p, Sound.ENTITY_ENDER_EYE_DEATH, .8f, .1f) }
                counter = delayDefault
            }

            counter--
        }
    }

    private fun newGravity(last: GravityState): Pair<GravityState, Gravity?> {
        val newGravity = GravityState.values().filter { it != last && it != GravityState.DEFAULT }.random()
        val gravityClass = when (newGravity) {
            GravityState.NO -> {
                bar.name(cmp("〰 No Gravity 〰", NamedTextColor.YELLOW))
                NoGravity()
            }

            GravityState.HIGH -> {
                bar.name(cmp("⬇ High Gravity ⬇", NamedTextColor.YELLOW))
                HighGravity()
            }

            GravityState.LOW -> {
                bar.name(cmp("✴ Low Gravity ✴", NamedTextColor.YELLOW))
                LowGravity()
            }

            GravityState.ANTI -> {
                bar.name(cmp("⬆ Anti Gravity ⬆", NamedTextColor.YELLOW))
                AntiGravity()
            }

            else -> null
        }
        bar.color(BossBar.Color.BLUE)
        broadcast("- Start ${newGravity.name}")
        sync { gravityClass?.start() ?: broadcast("Null Class") }
        return Pair(newGravity, gravityClass)
    }

    private fun resetGravity() {
        current = GravityState.DEFAULT
        worlds.forEach {
            it.entities.forEach { entity ->
                entity.setGravity(true)
                if (entity is LivingEntity) removeEffects(entity)
            }
        }
    }

    private fun removeEffects(entity: LivingEntity) {
        entity.removePotionEffect(PotionEffectType.LEVITATION)
        entity.removePotionEffect(PotionEffectType.SLOW_FALLING)
        entity.removePotionEffect(PotionEffectType.JUMP)
        entity.removePotionEffect(PotionEffectType.SLOW)
        entity.setGravity(true)
    }

    //Listener
    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val player = it.player
        player.showBossBar(bar)
        player.setGravity(true)
        if (current == GravityState.DEFAULT) removeEffects(player)
        else gClass?.modifyPlayer(player)
    }
}
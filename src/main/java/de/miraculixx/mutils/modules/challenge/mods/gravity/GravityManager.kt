package de.miraculixx.mutils.modules.challenge.mods.gravity

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChGravity
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.broadcastSound
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffectType
import java.util.*

class GravityManager : Challenge {
    override val challenge = Modules.GRAVITY
    private val barKey = NamespacedKey("mutils_gravity", UUID.randomUUID().toString())
    private val bar = Bukkit.createBossBar(barKey, "§e⇔ Normal Gravity ⇔", BarColor.YELLOW, BarStyle.SOLID)
    private var delay = 0
    private var current = ChGravity.NONE
    private var gClass: Gravity? = null

    private var running = false
    private var stop = false

    override fun start(): Boolean {
        val conf = ConfigManager.getConfig(Configs.MODULES)
        delay = conf.getInt("GRAVITY.Delay")
        running = true
        run()
        onlinePlayers.forEach {
            bar.addPlayer(it)
        }
        bar.isVisible = true
        bar.progress = 1.0
        return true
    }

    override fun stop() {
        running = false
        stop = true
        gClass?.unregisterAll()
        gClass?.active = false
        gClass = null
        bar.isVisible = false
        Bukkit.removeBossBar(barKey)

        resetGravity()
    }

    override fun register() {
        running = true
        onJoin.register()
        if (current != ChGravity.NONE) {
            gClass = when (current) {
                ChGravity.HIGH -> HighGravity()
                ChGravity.LOW -> LowGravity()
                ChGravity.ANTI -> AntiGravity()
                ChGravity.NO -> NoGravity()
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
        var last = ChGravity.NONE
        var counter = delay
        task(false, 0, 20) {
            if (stop) it.cancel()
            if (!running) return@task

            //WARNING 10 to 0
            if (current == ChGravity.NONE && counter <= 10) {
                bar.setTitle("§c⚠ Gravity switch in §l$counter§c Seconds ⚠")
                bar.color = BarColor.RED
                val pitch = (counter / 10.0 + 1).toFloat()
                if (counter == 0) {
                    broadcastSound(Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, .8f, .1f)
                    val pair = newGravity(last)
                    last = pair.first
                    current = last
                    gClass = pair.second
                } else broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, .3f, pitch)
            } else

            //RESET TO NORMAL
            if (current != ChGravity.NONE && counter == 0) {
                gClass?.unregisterAll()
                gClass?.active = false
                gClass = null
                sync {
                    resetGravity()
                }
                bar.setTitle("§e⇔ Normal Gravity ⇔")
                bar.color = BarColor.YELLOW
                broadcastSound(Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.MASTER, .8f, .1f)
            }

            if (counter <= 0) {
                counter = delay
            } else counter--
        }
    }

    private fun newGravity(last: ChGravity): Pair<ChGravity, Gravity?> {
        var newGravity = last
        while (last == newGravity) newGravity = listOf(ChGravity.NO, ChGravity.ANTI, ChGravity.LOW, ChGravity.HIGH).random()
        val gravityClass = when (newGravity) {
            ChGravity.NO -> {
                bar.setTitle("§e〰 No Gravity 〰")
                NoGravity()
            }
            ChGravity.HIGH -> {
                bar.setTitle("§e⬇ High Gravity ⬇")
                HighGravity()
            }
            ChGravity.LOW -> {
                bar.setTitle("§e✴ Low Gravity ✴")
                LowGravity()
            }
            ChGravity.ANTI -> {
                bar.setTitle("§e⬆ Anti Gravity ⬆")
                AntiGravity()
            }
            else -> null
        }
        bar.color = BarColor.BLUE
        sync {
            gravityClass?.start()
        }
        return Pair(newGravity, gravityClass)
    }

    private fun resetGravity() {
        current = ChGravity.NONE
        onlinePlayers.forEach { p ->
            removeEffects(p)
            p.getNearbyEntities(300.0, 200.0, 300.0).forEach { entity ->
                entity.setGravity(true)
                if (entity is LivingEntity) {
                    removeEffects(entity)
                }
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
        bar.addPlayer(it.player)
    }
}
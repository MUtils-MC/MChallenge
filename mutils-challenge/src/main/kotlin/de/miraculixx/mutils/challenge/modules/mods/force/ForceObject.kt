package de.miraculixx.mutils.modules.challenge.mods.force

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.challenge.utils.enums.challenges.ForceChallenge
import de.miraculixx.mutils.utils.text.broadcastSound
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class ForceObject(
    private val type: ForceChallenge,
    private val goals: List<String>,
    private val minSecs: Int,
    private val maxSecs: Int,
    private val cooldown: Int,
    private var goalString: String,
) {
    private val key = NamespacedKey(Main.INSTANCE, "FORCE_CH_${Random.nextFloat()}")
    private val bar = Bukkit.createBossBar(key, msg("modules.ch.force.waiting", pre = false), BarColor.BLUE, BarStyle.SOLID)

    private var goalObject: String = "null"

    private val players = HashMap<UUID, Boolean>()
    private var stopped = false
    private var running = true

    fun pauseChallenge() {
        running = false
    }
    fun continueChallenge() {
        running = true
    }

    fun stopChallenge() {
        bar.isVisible = false
        onlinePlayers.forEach {
            bar.removePlayer(it)
        }
        Bukkit.removeBossBar(key)
        stopped = true
    }

    fun startNewGoal() {
        goalObject = goals.random()
        broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL,SoundCategory.MASTER,1f,1f)
        run()
    }

    private fun check() {
         when (type) {
            ForceChallenge.FORCE_COLLECT -> {
                val remover = ArrayList<UUID>()
                players.forEach { (uuid, _) ->
                    val player = Bukkit.getPlayer(uuid)
                    if (player?.inventory?.contains(Material.valueOf(goalObject)) == false) {
                        sync {
                            player.damage(999.9)
                        }
                        broadcast(msg("modules.ch.force.failed", input = player.name))
                        remover.add(uuid)
                    }
                }
                remover.forEach { p ->
                    players.remove(p)
                }
            }
             else -> {}
         }

        broadcast(players.size.toString())
        if (players.isNotEmpty()) {
            //If success
            bar.setTitle(msg("modules.ch.force.waiting", pre = false))
            bar.color = BarColor.BLUE
            bar.progress = 1.0
            broadcastSound(Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 1f, 1.5f)
            broadcast(msg("modules.ch.force.success", input = cooldown.toString()))
            taskRunLater(20L * cooldown) {
                if (stopped) return@taskRunLater
                startNewGoal()
            }
        } else {
            bar.setTitle("§c§lFailed")
            broadcastSound(Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 1f, 1f)
        }
    }

    fun changePlayerStatus(player: Player, value: Boolean) {
        val uuid = player.uniqueId
        if (players[uuid] == value) return
        players[uuid] = value
        if (value) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, .5f, 1.2f)
        } else {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, .5f)
        }
    }

    private fun run() {
        val max = Random.nextInt(minSecs..maxSecs)
        val finalName = goalString.replace("%INPUT%", goalObject.replace("_"," "))
        var seconds = max
        task(false, 0, 20, safe = false) {
            if (stopped) {
                it.cancel()
                return@task
            }
            if (!running) {
                bar.setTitle("§c§oChallenge paused...")
                return@task
            }
            if (seconds <= 0) {
                check()
                it.cancel()
                return@task
            }
            bar.setTitle(finalName.replace("%INPUT-2%", seconds.toString()))
            bar.progress = seconds.toDouble() / max
            bar.color = when (bar.progress) {
                in .5..1.0 -> BarColor.GREEN
                in .2..0.49 -> BarColor.YELLOW
                in .0..0.19 -> BarColor.RED
                else -> bar.color
            }
            seconds--
        }
    }

    init {
        onlinePlayers.forEach {
            players[it.uniqueId] = false
            bar.addPlayer(it)
        }
        bar.isVisible = true
        bar.progress = 1.0
    }
}
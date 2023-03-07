package de.miraculixx.mutils.modules.challenge.mods.runRandomizer

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.utils.enums.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.modules.spectator.Spectator
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class RunRandomizerData(private var goal: Int) {
    private val materials: MutableList<Material> = ArrayList()
    private val bossBars = HashMap<UUID, BossBar>()
    private val keys = HashMap<BossBar, NamespacedKey>()

    init {
        fillMaterials()
        schedule()
    }

    fun removeAll() {
        bossBars.forEach { (_, bar) ->
            bar.isVisible = false
            bar.players.clear()
            Bukkit.removeBossBar(keys[bar]!!)
        }
    }

    fun resetStats(player: Player) {
        player.setStatistic(Statistic.WALK_ONE_CM, 0)
        player.setStatistic(Statistic.WALK_ON_WATER_ONE_CM, 0)
        player.setStatistic(Statistic.WALK_UNDER_WATER_ONE_CM, 0)
        player.setStatistic(Statistic.SPRINT_ONE_CM, 0)
        player.setStatistic(Statistic.CROUCH_ONE_CM, 0)
        player.setStatistic(Statistic.SWIM_ONE_CM, 0)
        player.setStatistic(Statistic.JUMP, 0)
        player.setStatistic(Statistic.FLY_ONE_CM, 0)
        player.setStatistic(Statistic.FALL_ONE_CM, 0)
    }

    private fun fillMaterials() {
        for (material in Material.values()) {
            if (material.isItem)
                materials.add(material)
        }
        materials.shuffle()
    }

    private fun schedule() {
        task(true, 20, 20) {
            if (challenges != ChallengeStatus.RUNNING) return@task
            onlinePlayers.forEach { player ->
                var i = 0
                i += player.getStatistic(Statistic.WALK_ONE_CM)
                i += player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM)
                i += player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM)
                i += player.getStatistic(Statistic.SPRINT_ONE_CM)
                i += player.getStatistic(Statistic.CROUCH_ONE_CM)
                i += player.getStatistic(Statistic.SWIM_ONE_CM)
                i += player.getStatistic(Statistic.JUMP) * 100
                i += player.getStatistic(Statistic.FLY_ONE_CM)
                i += player.getStatistic(Statistic.FALL_ONE_CM)
                i /= 100

                if (!bossBars.contains(player.uniqueId) && !Spectator.isSpectator(player.uniqueId)) {
                    val key = NamespacedKey(Main.INSTANCE, player.uniqueId.toString())
                    val bar = Bukkit.createBossBar(key, "§c...", BarColor.RED, BarStyle.SOLID)
                    bar.addPlayer(player)
                    bar.isVisible = true
                    keys[bar] = key
                    bossBars[player.uniqueId] = bar
                }

                bossBars[player.uniqueId]!!.setTitle(ChatColor.of("#2788C8").toString() + "Blocks: " + ChatColor.of("#9594B1") + i + "§7/" + ChatColor.of("#9594B1") + goal)
                if (i >= goal) {
                    bossBars[player.uniqueId]?.progress = 1.0
                    bossBars[player.uniqueId]?.color = BarColor.GREEN
                    player.inventory.addItem(ItemStack(materials[0], 64))
                    player.playSound(player.location, Sound.BLOCK_BEEHIVE_ENTER, 1f, 1.5f)

                    resetStats(player)
                    materials.shuffle()
                } else {
                    val progress = i.toDouble() / goal
                    bossBars[player.uniqueId]?.progress = progress
                    bossBars[player.uniqueId]?.color = BarColor.YELLOW
                }
            }
        }
    }
}
package de.miraculixx.mutils.modules.challenge.mods.realistic

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*

class InfoBar(private val inventoryGewicht: InventoryWeight, private val drinkLogic: DrinkLogic, private val temperatureLogic: TemperatureLogic) {

    private val barList: HashMap<UUID, BossBar> = HashMap()
    private val keyList: HashMap<UUID, NamespacedKey> = HashMap()
    private val display: String

    init {
        val config = ConfigManager.getConfig(Configs.MODULES)
        display = config.getString("REALISTIC.Info") ?: "111"
        scheduler()
    }

    private fun addPlayer(player: Player) {
        val key = NamespacedKey(Main.INSTANCE, player.uniqueId.toString())
        val bar = Bukkit.createBossBar(key, "§c...", BarColor.GREEN, BarStyle.SOLID)
        bar.addPlayer(player)
        bar.isVisible = true
        barList[player.uniqueId] = bar
        keyList[player.uniqueId] = key
    }

    fun removePlayer(player: Player) {
        barList[player.uniqueId]?.isVisible = false
        keyList[player.uniqueId]?.let { Bukkit.removeBossBar(it) }
        keyList.remove(player.uniqueId)
        barList.remove(player.uniqueId)
    }

    private fun scheduler() {
        task(true, 0, 20) {
            if (challenges != ChallengeStatus.RUNNING) return@task
            Bukkit.getOnlinePlayers().forEach { player -> if (!barList.containsKey(player.uniqueId)) addPlayer(player) }

            barList.forEach { (uuid, bar) ->
                var string = ""
                if (display[0] == '1') string += "§2⚓[§a" + inventoryGewicht.getWeight(uuid) + "§2] "
                string += if (display[1] == '1') { //10 Wassertropfen
                    when (drinkLogic.getPlayer(uuid)) {
                        10 -> "§b⚈⚈⚈⚈⚈⚈⚈⚈⚈⚈"; 9 -> "§8⚈§b⚈⚈⚈⚈⚈⚈⚈⚈⚈"; 8 -> "§8⚈⚈§b⚈⚈⚈⚈⚈⚈⚈⚈"
                        7 -> "§8⚈⚈⚈§b⚈⚈⚈⚈⚈⚈⚈"; 6 -> "§8⚈⚈⚈⚈§b⚈⚈⚈⚈⚈⚈"; 5 -> "§8⚈⚈⚈⚈⚈§b⚈⚈⚈⚈⚈"
                        4 -> "§8⚈⚈⚈⚈⚈⚈§b⚈⚈⚈⚈"; 3 -> "§8⚈⚈⚈⚈⚈⚈⚈§b⚈⚈⚈"; 2 -> "§8⚈⚈⚈⚈⚈⚈⚈⚈§b⚈⚈"
                        1 -> "§8⚈⚈⚈⚈⚈⚈⚈⚈⚈§b⚈"; 0 -> "§8⚈⚈⚈⚈⚈⚈⚈⚈⚈⚈"; null -> Bukkit.getPlayer(uuid)?.let { it1 -> drinkLogic.modify(it1, 1) }
                        else -> ""
                    }
                } else { //Compact
                    "§3☕[§b" + drinkLogic.getPlayer(uuid) + "§3]"
                }
                if (display[2] == '1') {
                    string += when (val temperature = temperatureLogic.getPlayer(uuid)) {
                        in 0..20 -> " §6❄[§e$temperature§6]"
                        in 21..79 -> " §6☀[§e$temperature§6]"
                        in 80..100 -> " §6☠[§e$temperature§6]"
                        else -> " §6☀[§e$temperature§6]"
                    }
                }

                //Update
                Bukkit.getPlayer(uuid)?.let { it1 -> drinkLogic.rechner(it1) }
                bar.setTitle(string)
            }
        }
    }
}
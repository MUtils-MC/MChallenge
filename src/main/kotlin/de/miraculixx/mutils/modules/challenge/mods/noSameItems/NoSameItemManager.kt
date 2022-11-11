@file:Suppress("UNCHECKED_CAST")

package de.miraculixx.mutils.modules.challenge.mods.noSameItems

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*

class NoSameItemManager {
    private val map = HashMap<Material, ArrayList<Player>>()
    private val map2 = HashMap<Player, ArrayList<Material>>()
    private val death = ArrayList<Player>()

    private val barList = HashMap<UUID, BossBar>()
    private val keyList = HashMap<BossBar, NamespacedKey>()

    private var infoMode: NoSameItemEnum
    private var lives: Int
    private var sync: Boolean

    init {
        val config = ConfigManager.getConfig(Configs.MODULES)
        infoMode = NoSameItemEnum.valueOf(config.getString("NO_SAME_ITEM.Info") ?: "EVERYTHING")
        lives = config.getInt("NO_SAME_ITEM.Lives")
        sync = config.getBoolean("NO_SAME_ITEM.SyncHeart")
    }

    fun start() {
        onlinePlayers.forEach { player ->
            if (player.gameMode == GameMode.SURVIVAL) createBossBar(player)
        }

        for (value in Material.values()) {
            map[value] = ArrayList()
        }
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) {
                player.inventory.clear()
                if (sync) {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = lives * 2.0
                } else {
                    visible()
                    modifyBar(player, lives)
                }
            }
        }
    }

    fun removePlayer(player: Player) {
        map.forEach { (_, aList) ->
            if (aList.contains(player)) aList.remove(player)
        }
        heartCalculation()
        deathBar(player)
    }

    fun addItem(player: Player, event: String, items: List<Material>) {
        items.forEach {
            if (map[it]?.contains(player) == true) return
            map[it]?.add(player)
            if (infoMode == NoSameItemEnum.EVERYTHING)
                player.sendMessage("§a+ §7${it.name} §8($event)")
        }

        heartCalculation()
    }

    fun getItems(player: Player): ArrayList<Material> {
        val list = ArrayList<Material>()
        map2[player]?.forEach {
            list.add(it)
        }
        return list
    }

    private fun heartCalculation() {
        val players = HashMap<Player, Int>() //Duplicate Counter
        for (onlinePlayer in onlinePlayers) { //Valid Players
            if (!Spectator.isSpectator(onlinePlayer.uniqueId)) players[onlinePlayer] = 0
        }

        map.forEach { (item, list) ->
            val dummy = list.clone() as ArrayList<Player>
            if (dummy.size > 0) dummy.removeAt(0)
            for (player in dummy) {
                players[player] = players.getOrDefault(player, 0).plus(1)
                if (map2[player] == null) map2[player] = ArrayList()
                if (!map2[player]!!.contains(item)) {
                    map2[player]?.add(item)
                    player.sendMessage(msg("modules.ch.noSameItem.duplicate", list[0], item.name))
                }
            }
        }

        players.forEach { (player, int) ->
            var hp = lives - int
            if (hp < 0) hp = 0
            if (hp == 0) {
                if (!death.contains(player)) {
                    player.damage(100.0)
                    player.sendMessage(msg("modules.ch.noSameItem.death", player, lives.toString()))
                    death.add(player)
                }
            }
            var differenz = 0
            if (sync) {
                if (infoMode == NoSameItemEnum.EVERYTHING || infoMode == NoSameItemEnum.ONLY_LIVES) {
                    differenz = hp - player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue?.div(2)?.toInt()!!
                }
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = hp * 2.0
            } else {
                if (infoMode == NoSameItemEnum.EVERYTHING || infoMode == NoSameItemEnum.ONLY_LIVES) {
                    differenz = hp - getAmount(player)
                }
                modifyBar(player, hp)
            }

            if (differenz != 0) {
                var title = "§4"
                if (differenz < 0) {
                    title = "§c- §4"
                    repeat(differenz * -1) {
                        title += "❤"
                    }
                    player.damage(0.1)
                }
                player.sendTitle(" ", title, 10, 10, 10)
            }
        }
    }

    fun createBossBar(player: Player) {
        if (barList[player.uniqueId] != null) return
        val key = NamespacedKey(Main.INSTANCE, "NSI-${player.uniqueId}")
        val bossBar = Bukkit.createBossBar(key, "§c...", BarColor.RED, BarStyle.SOLID)
        bossBar.progress = 1.0
        bossBar.isVisible = true
        bossBar.addPlayer(player)
        barList[player.uniqueId] = bossBar
        keyList[bossBar] = key
    }

    fun removeAll() {
        barList.forEach { (_, bar) ->
            bar.isVisible = false
            bar.removeAll()
            Bukkit.removeBossBar(keyList[bar]!!)
        }
    }

    private fun visible() {
        barList.forEach { (_, bar) ->
            bar.isVisible = true
        }
    }

    private fun modifyBar(player: Player, lives: Int) {
        if (barList[player.uniqueId]?.title == msg("modules.ch.noSameItem.dead-title", pre = false)) return
        var title = "§4"
        repeat(lives) { title += "❤" }
        if (barList[player.uniqueId] == null) createBossBar(player)
        barList[player.uniqueId]!!.setTitle(title)
    }

    private fun deathBar(player: Player) {
        val title = msg("modules.ch.noSameItem.dead-title", pre = false)
        if (barList[player.uniqueId] == null) createBossBar(player)
        barList[player.uniqueId]?.setTitle(title)
    }

    private fun getAmount(player: Player): Int {
        return barList[player.uniqueId]?.title?.split("❤")?.size!! - 1
    }
}
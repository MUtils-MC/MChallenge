package de.miraculixx.mchallenge.modules.mods.multiplayer.noSameItems

import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.namespace
import de.miraculixx.mcommons.serializer.plainSerializer
import de.miraculixx.mcommons.text.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.time.Duration
import java.util.*

@Deprecated("Outdated item calculation", ReplaceWith("Moved to NoSameItem root class"))
class NoSameItemManager {
    private val materialRanking = HashMap<Material, MutableList<Player>>()
    private val playerList = HashMap<Player, MutableList<Material>>()
    private val death = ArrayList<Player>()

    private val barList = HashMap<UUID, BossBar>()

    private val infoMode: NoSameItemEnum
    private val lives: Int
    private val sync: Boolean

    init {
        val settings = challenges.getSetting(Challenges.NO_SAME_ITEM).settings
        infoMode = enumOf<NoSameItemEnum>(settings["info"]?.toEnum()?.getValue() ?: "EVERYTHING") ?: NoSameItemEnum.EVERYTHING
        lives = settings["lives"]?.toInt()?.getValue() ?: 5
        sync = settings["sync"]?.toBool()?.getValue() ?: false
    }

    fun start() {
        onlinePlayers.forEach { player ->
            if (player.gameMode == GameMode.SURVIVAL) createBossBar(player)
        }

        Material.entries.forEach { item ->
            materialRanking[item] = mutableListOf()
        }
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) {
                player.inventory.clear()
                if (sync) {
                    player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = lives * 2.0
                } else {
                    modifyBar(player, lives)
                }
            }
        }
    }

    fun removePlayer(player: Player) {
        materialRanking.forEach { (_, aList) ->
            if (aList.contains(player)) aList.remove(player)
        }
        heartCalculation() //Update
        deathBar(player)
    }

    fun addItem(player: Player, event: String, items: List<Material>) {
        items.forEach {
            if (materialRanking[it]?.contains(player) == true) return
            materialRanking[it]?.add(player)
            if (infoMode == NoSameItemEnum.EVERYTHING)
                player.sendMessage(cmp("+ ", cSuccess) + cmp(it.name) + cmp("($event)", NamedTextColor.DARK_GRAY))
        }

        heartCalculation() //Update
    }

    fun getItems(player: Player) = playerList[player] ?: emptyList()

    private fun heartCalculation() {
        val hearts = mutableMapOf<Player, Int>() //Duplicate Counter
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) hearts[player] = 0
        }

        // Calc duplicates
        materialRanking.forEach { (item, list) ->
            list.forEachIndexed { index, player ->
                if (index == 0) return@forEachIndexed
                hearts[player] = hearts.getOrDefault(player, 0).plus(1)
                val data = playerList.getOrPut(player) { mutableListOf() }
                if (!data.contains(item)) {
                    data.add(item)
                    player.sendMessage(prefix + player.msg("event.noSameItem.duplicate", listOf(list.firstOrNull()?.name ?: "Unknown", item.name)))
                }
            }
        }


        hearts.forEach { (player, amount) ->
            var hp = lives - amount
            if (hp < 0) hp = 0
            if (hp == 0) {
                if (!death.contains(player)) { //Prevent announce twice
                    player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "noSameItem")
                    player.damage(999.0)
                    death.add(player)
                }
            }
            var differenz = 0
            if (sync) {
                if (infoMode == NoSameItemEnum.EVERYTHING || infoMode == NoSameItemEnum.ONLY_LIVES) {
                    differenz = hp - (player.getAttribute(Attribute.MAX_HEALTH)?.baseValue?.div(2)?.toInt() ?: 0)
                }
                player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = hp * 2.0
            } else {
                if (infoMode == NoSameItemEnum.EVERYTHING || infoMode == NoSameItemEnum.ONLY_LIVES) {
                    differenz = hp - getAmount(player)
                }
                modifyBar(player, hp)
            }

            if (differenz < 0) {
                val title = cmp("- ", cError) + cmp(buildString {
                    repeat(differenz * -1) {
                        append("❤")
                    }
                })
                player.damage(0.1)
                val duration = Duration.ofMillis(500)
                player.showTitle(Title.title(emptyComponent(), title, Title.Times.times(duration, duration, duration)))
            }
        }
    }

    private fun createBossBar(player: Player): BossBar {
        val current = barList[player.uniqueId]
        if (current != null) return current
        val bossBar = BossBar.bossBar(cmp("Waiting for server...", cError), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
        player.showBossBar(bossBar)
        barList[player.uniqueId] = bossBar
        return bossBar
    }

    fun removeAll() {
        barList.forEach { (player, bar) ->
            Bukkit.getPlayer(player)?.hideBossBar(bar)
        }
    }

    private fun modifyBar(player: Player, lives: Int) {
        val bar = barList[player.uniqueId] ?: createBossBar(player)
        if (death.contains(player)) {
            bar.name(player.msg("event.noSameItem.deathTitle"))
            return
        }

        val title = cmp(buildString { repeat(lives) { append("❤") } }, cError)
        bar.name(title)
    }

    private fun deathBar(player: Player) {
        val bar = barList[player.uniqueId] ?: createBossBar(player)
        bar.name(player.msg("event.noSameItem.deathTitle"))
    }

    private fun getAmount(player: Player): Int {
        return barList[player.uniqueId]?.name()?.let { plainSerializer.serialize(it).split('❤').size - 1 } ?: 0
    }
}
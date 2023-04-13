package de.miraculixx.mchallenge.modules.mods.collectBattle

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.bukkit.kill
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.InternalTimer
import de.miraculixx.mchallenge.modules.challenges.getFormatted
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.extensions.soundError
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CollectBattle : Challenge {
    override val challenge = Challenges.COLLECT_BATTLE
    private val itemGoals: MutableMap<UUID, TargetData> = mutableMapOf()
    private val itemPresets: MutableMap<UUID, TargetData> = mutableMapOf()
    private val activePlayer: MutableSet<UUID> = mutableSetOf()
    private val deathPlayer: MutableSet<UUID> = mutableSetOf()
    private val isHunting = false
    private var roundCounter = 0

    private val itemKey = NamespacedKey(namespace, "collectBattle.indicator")
    private val msgFinished = cmp(msgString("event.collectBattle.finished"), cSuccess)
    private val msgNotAllowed = cmp(msgString("event.collectBattle.notAllowed"), cError)
    private val msgTimeLeft = msgString("event.collectBattle.timeLeft")

    private val maxSetItemTime: Duration
    private val cooldownTime: Int

    init {
        val settings = challenges.getSetting(challenge).settings
        maxSetItemTime = settings["maxSetTime"]?.toInt()?.getValue()?.seconds ?: 10.minutes
        cooldownTime = settings["cooldown"]?.toInt()?.getValue() ?: 180
    }

    override fun register() {
        onCollect.register()
        onInvClick.register()
        onF.register()
    }

    override fun unregister() {
        onCollect.unregister()
        onInvClick.unregister()
        onF.unregister()
    }

    override fun start(): Boolean {
        startNewRound()
        return true
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        val entity = it.entity as? Player ?: return@listen

        val item = it.item.itemStack
        item.handleTag()

        if (!isHunting) return@listen
        item.checkCollected(entity)
    }

    private val onInvClick = listen<InventoryClickEvent>(register = false) {
        val topInv = it.view.topInventory
        val item = it.currentItem ?: return@listen
        if (!it.isCancelled) item.handleTag()

        if (!isHunting) return@listen
        item.checkCollected(it.whoClicked as? Player ?: return@listen)
    }

    private val onF = listen<PlayerSwapHandItemsEvent>(register = false) {
        if (isHunting) return@listen
        val item = it.offHandItem ?: return@listen
        val type = item.type
        if (type.isAir) return@listen
        val player = it.player
        if (item.handleTag()) {
            player.soundEnable()
            player.sendMessage(msg("event.collectBattle.register", listOf("<lang:${type.translationKey()}>")))

            //Edit state data
            val uuid = player.uniqueId
            activePlayer.remove(uuid)
            val data = itemPresets[player.uniqueId] ?: return@listen
            data.time.running = false
            data.target = type
            val bar = data.bar
            bar.color(BossBar.Color.GREEN)
            bar.progress(1f)
            bar.name(msgFinished)
        } else {
            player.soundError()
            player.sendMessage(msgNotAllowed)
        }
    }


    //TODO
    private fun ItemStack.checkCollected(player: Player) {
        val uuid = player.uniqueId
        val goal = itemGoals[uuid] ?: return
        if (type == goal.target) {
            goal.time.stopped = true
            goal.time.running = false
            val bar = goal.bar
            bar.color(BossBar.Color.GREEN)
            bar.progress(1f)
            bar.name(msgFinished)
            activePlayer.remove(uuid)
            player.soundEnable()

            //TODO check if all players finished
        }
    }

    /**
     * @return If item is new
     */
    private fun ItemStack.handleTag(): Boolean {
        val meta = itemMeta

        val dataContainer = meta.persistentDataContainer
        if (dataContainer.has(itemKey)) return false
        if (isHunting) {
            dataContainer.set(itemKey, PersistentDataType.INTEGER, roundCounter)
        } else dataContainer.set(itemKey, PersistentDataType.INTEGER, -1)
        itemMeta = meta
        return true
    }

    private fun startNewRound() {
        val playablePlayers = onlinePlayers.map { it.uniqueId }.filter { !Spectator.isSpectator(it) }.shuffled()
        activePlayer.addAll(playablePlayers)

        playablePlayers.forEach { player ->
            val bar = BossBar.bossBar(cmp(""), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
            itemPresets[player] = TargetData(Material.AIR, InternalTimer(maxSetItemTime, {
                deathPlayer.add(player)
                val onlinePlayer = Bukkit.getPlayer(player) ?: return@InternalTimer
                onlinePlayer.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "event.death.collectBattle.noPreset")
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1f)
                deathPlayer.add(player)
                onlinePlayer.kill()

            }) { _, duration ->
                bar.name(cmp("$msgTimeLeft ") + cmp(duration.getFormatted(), cHighlight) + cmp(" (Press ") + Component.keybind("key.offhand").color(cMark) + cmp(")"))
            }, bar)
            val onlinePlayer = Bukkit.getPlayer(player) ?: return@forEach
            onlinePlayer.showBossBar(bar)
            onlinePlayer.soundEnable()
        }

        val timer = InternalTimer(maxSetItemTime, {}) { _, _ -> }
        task(false, 0, 20) {
            //All players done or timer zero
            if (activePlayer.isEmpty() || timer.getTime() == ZERO) {
                //Start next phase
                val players = itemPresets.map { it.key }
                val objects = itemPresets.map { it.value }
                players.forEachIndexed { index, uuid ->
                    val data = objects.getOrNull(index + 1) ?: objects[0]
                    data.time.setTime(maxSetItemTime - data.time.getTime())
                    val player = Bukkit.getPlayer(uuid)
                    data.bar.name(cmp("$msgTimeLeft ") + cmp(data.time.getTime().getFormatted(), cHighlight) + cmp(" - ") + Component.translatable(data.target.translationKey()).color(cMark))
                    player?.soundEnable()
                    itemGoals[uuid] = data
                }
            }
        }
    }

    private data class TargetData(
        var target: Material,
        val time: InternalTimer,
        val bar: BossBar
    )
}
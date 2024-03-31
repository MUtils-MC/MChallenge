package de.miraculixx.mchallenge.modules.mods.multiplayer.collectBattle

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.kill
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.challenges.InternalTimer
import de.miraculixx.mchallenge.modules.challenges.getFormatted
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.extensions.soundError
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CollectBattle : Challenge {
    private val itemGoals: MutableMap<UUID, TargetData> = mutableMapOf()
    private val itemPresets: MutableMap<UUID, TargetData> = mutableMapOf()
    private val activePlayer: MutableSet<UUID> = mutableSetOf()
    private val deathPlayer: MutableSet<UUID> = mutableSetOf()
    private var state = CollectBattleState.COOLDOWN
    private var roundCounter = 0

    private val itemKey = NamespacedKey(namespace, "collectbattle.indicator")
    private val msgFinished = cmp(msgString("event.collectBattle.finished"), cSuccess)
    private val msgNotAllowed = cmp(msgString("event.collectBattle.notAllowed"), cError)
    private val msgTimeLeft = msgString("event.collectBattle.timeLeft")

    private val maxSetItemTime: Duration
    private val cooldownTime: Int
    private val bufferTime: Int
    private val cooldownBar: BossBar = BossBar.bossBar(msg("event.collectBattle.cooldown"), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)

    private var stopped = false
    private var running = false

    init {
        val settings = challenges.getSetting(Challenges.COLLECT_BATTLE).settings
        maxSetItemTime = settings["maxSetTime"]?.toInt()?.getValue()?.seconds ?: 10.minutes
        cooldownTime = settings["cooldown"]?.toInt()?.getValue() ?: 180
        bufferTime = settings["bufferTime"]?.toInt()?.getValue() ?: 10
    }

    override fun register() {
        onCollect.register()
        onInvClick.register()
        onF.register()
        onJoin.register()
        onCraft.register()

        running = true
    }

    override fun unregister() {
        onCollect.unregister()
        onInvClick.unregister()
        onF.unregister()
        onJoin.unregister()
        onCraft.unregister()

        running = false
    }

    override fun start(): Boolean {
        startNewRound()
        return true
    }

    override fun stop() {
        itemGoals.forEach { (uuid, data) ->
            data.time.stopped = true
            val player = Bukkit.getPlayer(uuid) ?: return@forEach
            player.hideBossBar(data.bar)
        }
        itemPresets.forEach { (uuid, data) ->
            data.time.stopped = true
            val player = Bukkit.getPlayer(uuid) ?: return@forEach
            player.hideBossBar(data.bar)
        }
        onlinePlayers.forEach { player -> player.hideBossBar(cooldownBar) }
        running = false
        stopped = true
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        val entity = it.entity as? Player ?: return@listen

        val item = it.item.itemStack
        item.handleTag()

        if (state != CollectBattleState.HUNTING) return@listen
        item.checkCollected(entity)
    }

    private val onInvClick = listen<InventoryClickEvent>(register = false) {
        val item = it.currentItem ?: return@listen
        val type = it.clickedInventory?.type
        if (!it.isCancelled && type != InventoryType.WORKBENCH && type != InventoryType.CRAFTING) item.handleTag()

        if (state != CollectBattleState.HUNTING) return@listen
        item.checkCollected(it.whoClicked as? Player ?: return@listen)
    }

    private val onF = listen<PlayerSwapHandItemsEvent>(register = false) {
        if (state != CollectBattleState.SETTING) return@listen
        val item = it.offHandItem
        val type = item.type
        if (type.isAir) return@listen
        val player = it.player
        if (!activePlayer.contains(player.uniqueId)) return@listen
        if (item.handleTag()) {
            player.soundEnable()
            player.sendMessage(prefix + msg("event.collectBattle.register", listOf("<lang:${type.translationKey()}>")))

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

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        if (deathPlayer.contains(uuid)) return@listen

        val bar = when (state) {
            CollectBattleState.HUNTING -> itemGoals[uuid]?.bar ?: return@listen
            CollectBattleState.SETTING -> itemPresets[uuid]?.bar ?: return@listen
            CollectBattleState.COOLDOWN -> cooldownBar
        }
        player.showBossBar(bar)
    }

    private val onCraft = listen<CraftItemEvent>(register = false) {
        val result = it.currentItem ?: return@listen
        it.inventory.matrix.forEach { item ->
            val tag = item?.itemMeta?.persistentDataContainer?.get(itemKey, PersistentDataType.INTEGER)
            if (tag != null && tag != roundCounter) result.editMeta { m -> m.persistentDataContainer.set(itemKey, PersistentDataType.INTEGER, -1) }
        }
    }

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
        }
    }

    /**
     * @return If item is new
     */
    private fun ItemStack.handleTag(): Boolean {
        val meta = if (hasItemMeta()) itemMeta else {
            meta { }
            itemMeta
        }

        val dataContainer = meta.persistentDataContainer
        if (dataContainer.has(itemKey)) {
            return if (state == CollectBattleState.SETTING) {
                val indicator = dataContainer.get(itemKey, PersistentDataType.INTEGER)
                indicator == roundCounter
            } else false
        }
        if (state == CollectBattleState.SETTING) {
            dataContainer.set(itemKey, PersistentDataType.INTEGER, roundCounter)
        } else dataContainer.set(itemKey, PersistentDataType.INTEGER, -1)
        itemMeta = meta
        return true
    }

    private fun startNewRound() {
        val playablePlayers = onlinePlayers.map { it.uniqueId }.filter { !Spectator.isSpectator(it) }.shuffled()
        activePlayer.addAll(playablePlayers)
        state = CollectBattleState.SETTING
        roundCounter++

        playablePlayers.forEach { player ->
            val bar = BossBar.bossBar(cmp(""), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
            itemPresets[player] = TargetData(Material.AIR, InternalTimer(maxSetItemTime, {
                deathPlayer.add(player)
                activePlayer.remove(player)
                val onlinePlayer = Bukkit.getPlayer(player) ?: return@InternalTimer
                onlinePlayer.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "event.death.collectBattle.noPreset")
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1f)
                onlinePlayer.kill()

            }) { _, duration ->
                bar.name(cmp("$msgTimeLeft ") + cmp(duration.getFormatted(), cHighlight) + cmp(" (Press ") + cmpTranslatableVanilla("key.swapOffhand", cMark) + cmp(")"))
                bar.progress(duration.inWholeSeconds.toFloat() / maxSetItemTime.inWholeSeconds + bufferTime + 1)
            }, bar)
            val onlinePlayer = Bukkit.getPlayer(player) ?: return@forEach
            onlinePlayer.showBossBar(bar)
            onlinePlayer.soundEnable()
        }

        val timer = InternalTimer(maxSetItemTime, {}) { _, _ -> }
        task(false, 0, 20) {
            if (stopped) it.cancel()
            if (!running) return@task

            when (state) {
                CollectBattleState.SETTING -> {
                    //All players done or timer zero
                    if (activePlayer.isEmpty() || timer.time == ZERO) {
                        println("Start next phase")
                        //Start next phase
                        val players = itemPresets.map { pre -> pre.key }
                        val objects = itemPresets.map { pre -> pre.value }
                        players.forEachIndexed { index, uuid ->
                            val data = objects.getOrNull(index + 1) ?: objects[0]
                            val previousPlayer = Bukkit.getOfflinePlayer(players.getOrNull(index + 1) ?: players[0])
                            val previousPlayerName = previousPlayer.name ?: "Unknown"
                            val player = Bukkit.getPlayer(uuid)
                            val timeToGather = maxSetItemTime - data.time.time + bufferTime.seconds
                            player?.sendMessage(prefix + msg("event.collectBattle.startHunt", listOf("<lang:${data.target.translationKey()}>", previousPlayerName, timeToGather.getFormatted())))
                            player?.showBossBar(data.bar)
                            previousPlayer.player?.hideBossBar(data.bar)
                            if (data.target == Material.AIR) data.target = Material.STICK

                            data.time = InternalTimer(timeToGather, {
                                player?.persistentDataContainer?.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "event.death.collectBattle.timeout")
                                player?.playSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1f)
                                deathPlayer.add(uuid)
                                activePlayer.remove(uuid)
                                player?.kill()
                            }) { _, duration ->
                                data.bar.name(cmp("$msgTimeLeft ") + cmp(duration.getFormatted(), cHighlight) + cmp(" - ") + cmpTranslatableVanilla(data.target.translationKey(), cMark))
                                data.bar.progress(timeToGather.inWholeSeconds.toFloat() / duration.inWholeSeconds)
                            }
                            data.time.running = false
                            player?.soundEnable()
                            itemGoals[uuid] = data
                        }
                        activePlayer.addAll(players)
                        state = CollectBattleState.HUNTING

                        //Start timers delayed
                        taskRunLater(20 * 5) {
                            itemGoals.forEach { (_, data) ->
                                data.time.running = true
                            }
                        }
                    }
                }

                CollectBattleState.HUNTING -> {
                    if (activePlayer.isEmpty()) {
                        itemGoals.forEach { (uuid, data) ->
                            val player = Bukkit.getPlayer(uuid)
                            player?.hideBossBar(data.bar)
                        }
                        broadcast(prefix + msg("event.collectBattle.startCooldown", listOf(roundCounter.toString())))
                        startCooldown()
                        state = CollectBattleState.COOLDOWN
                    }
                }

                CollectBattleState.COOLDOWN -> Unit
            }
        }
    }

    private fun startCooldown() {
        onlinePlayers.forEach { it.showBossBar(cooldownBar) }
        var cooldown = cooldownTime
        task(false, 0, 20) {
            if (stopped) it.cancel()
            if (!running) return@task
            if (cooldown <= 0) {
                state = CollectBattleState.SETTING
                onlinePlayers.forEach { player -> player.hideBossBar(cooldownBar) }
                startNewRound()
                it.cancel()
            }
            cooldown--
        }
    }

    private data class TargetData(
        var target: Material,
        var time: InternalTimer,
        val bar: BossBar
    )
}
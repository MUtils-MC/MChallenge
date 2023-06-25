package de.miraculixx.mchallenge.modules.mods.force.forceHunt

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.challenge.api.modules.mods.forceHunt.HuntType
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.kill
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mcore.utils.getMaterials
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType

class ForceHunt : Challenge {
    private val bar = BossBar.bossBar(cmp("Waiting for server..."), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)
    private val msgHeight = msgString("event.forceHunt.height")
    private val msgItems = msgString("event.forceHunt.item")
    private val msgBiomes = msgString("event.forceHunt.biome")
    private val msgWaiting = msgString("event.forceHunt.waiting")
    private var paused = true
    private var stopped = false

    private val activeObjects: Set<HuntType>
    private val biomes = Biome.values().filter {
        it != Biome.CUSTOM &&
                it != Biome.END_BARRENS &&
                it != Biome.END_HIGHLANDS &&
                it != Biome.END_MIDLANDS &&
                it != Biome.THE_END &&
                it != Biome.SMALL_END_ISLANDS
    }
    private val items = getMaterials(true, false)

    private val cooldownRange: IntRange
    private val huntRange: IntRange

    init {
        val settings = challenges.getSetting(Challenges.FORCE_COLLECT).settings
        val timing = settings["times"]?.toSection()?.getValue()
        cooldownRange = (timing?.get("minCooldown")?.toInt()?.getValue() ?: 250)..(timing?.get("maxCooldown")?.toInt()?.getValue() ?: 350)
        huntRange = (timing?.get("minTime")?.toInt()?.getValue() ?: 180)..(timing?.get("maxTime")?.toInt()?.getValue() ?: 360)
        val objects = settings["objects"]?.toSection()?.getValue()
        activeObjects = buildSet {
            objects?.get("items")?.toBool()?.getValue()?.let { if (it) add(HuntType.ITEM) }
            objects?.get("biomes")?.toBool()?.getValue()?.let { if (it) add(HuntType.BIOME) }
            objects?.get("height")?.toBool()?.getValue()?.let { if (it) add(HuntType.HEIGHT) }
        }
    }

    override fun start(): Boolean {
        if (activeObjects.isEmpty()) {
            broadcast(prefix + cmp("Force Hunt - No force types enabled! Please enable at least one"))
            return false
        }
        onlinePlayers.forEach { player -> player.showBossBar(bar) }
        scheduler()
        bar.name(cmp(msgWaiting, NamedTextColor.WHITE, italic = true))
        bar.color(BossBar.Color.BLUE)
        return true
    }

    override fun stop() {
        stopped = true
        onlinePlayers.forEach { player -> player.hideBossBar(bar) }
    }

    override fun register() {
        onJoin.register()
        paused = false
    }

    override fun unregister() {
        onJoin.unregister()
        paused = true
    }

    private val onJoin = listen<PlayerJoinEvent> {
        it.player.showBossBar(bar)
    }


    private fun scheduler() {
        var time = cooldownRange.random()
        var maxHuntTime = 0
        var isCooldown = true
        var goal = ""
        var goalType = HuntType.ITEM

        task(false, 0, 20) {
            if (stopped) it.cancel()
            if (paused) return@task

            if (isCooldown) {
                if (time <= 0) {
                    time = huntRange.random()
                    maxHuntTime = time
                    isCooldown = false
                    goalType = activeObjects.random()
                    goal = when (goalType) {
                        HuntType.HEIGHT -> {
                            val mainWorld = worlds.first()
                            (mainWorld.minHeight..mainWorld.maxHeight).random().toString()
                        }

                        HuntType.BIOME -> biomes.random().name
                        HuntType.ITEM -> items.random().name
                    }
                    onlinePlayers.forEach { player -> player.playSound(player, Sound.ENTITY_PUFFER_FISH_BLOW_OUT, 1f, 1.5f) }
                    modifyBar(goalType, goal, maxHuntTime, time)
                    return@task
                }
            } else {
                modifyBar(goalType, goal, maxHuntTime, time)
                if (time <= 0) {
                    onlinePlayers.forEach { player ->
                        if (player.gameMode != GameMode.SURVIVAL) return@forEach
                        if (Spectator.isSpectator(player.uniqueId)) return@forEach
                        if (!player.checkPlayer(goalType, goal)) {
                            player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "forceHunt")
                            sync { player.kill() }
                        } else player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1.3f)
                    }
                    bar.name(cmp(msgWaiting, NamedTextColor.WHITE, italic = true))
                    bar.color(BossBar.Color.BLUE)
                    time = cooldownRange.random()
                    isCooldown = true
                    return@task
                }
            }
            time--
        }
    }

    private fun Player.checkPlayer(type: HuntType, goal: String): Boolean {
        return when (type) {
            HuntType.HEIGHT -> location.blockY.toString() == goal
            HuntType.BIOME -> location.block.biome.name == goal
            HuntType.ITEM -> {
                val materialGoal = enumOf<Material>(goal) ?: Material.STONE
                inventory.forEach { item ->
                    if (item?.type == materialGoal) return true
                }
                false
            }
        }
    }

    private fun modifyBar(goalType: HuntType, goal: String, maxTime: Int, currentTime: Int) {
        val name = when (goalType) {
            HuntType.HEIGHT -> cmp(msgHeight)
            HuntType.BIOME -> cmp(msgBiomes)
            HuntType.ITEM -> cmp(msgItems)
        }
        bar.name(name + cmp(" $goal", cHighlight) + cmp(" (") + cmp("${currentTime}s", cMark) + cmp(")"))
        bar.progress(currentTime.toFloat() / maxTime)
        when (bar.progress()) {
            in 0f..0.15f -> bar.color(BossBar.Color.RED)
            in 0.16f..0.5f -> bar.color(BossBar.Color.YELLOW)
            in 0.51f..1f -> bar.color(BossBar.Color.GREEN)
        }
    }
}
package de.miraculixx.mchallenge.modules.mods.simple.damager

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mcommons.extensions.enumOf
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent

class Damager : Challenge {
    private val listener = ArrayList<Listener>()
    private val damage: Double
    private val mode: DamagerType
    private val interval: Int

    private var active = false
    private var stopped = false

    init {
        val settings = challenges.getSetting(Challenges.DAMAGER).settings
        damage = settings["damage"]?.toInt()?.getValue()?.toDouble() ?: 1.0
        mode = enumOf<DamagerType>(settings["mode"]?.toEnum()?.getValue()) ?: DamagerType.SLOT_CHANGE
        interval = settings["interval"]?.toInt()?.getValue() ?: 1
    }

    override fun start(): Boolean {
        when (mode) {
            DamagerType.SLOT_CHANGE -> {
                listener.add(onSlotSwitch)
                listener.add(onItemClick)
            }

            DamagerType.BLOCK_MOVE -> {
                listener.add(onMove)
            }

            DamagerType.INTERVAL -> {}

            DamagerType.VERTICAL_MOVE -> {
                listener.add(onMove2)
            }
        }
        return true
    }

    override fun stop() {
        active = false
        stopped = true
    }

    override fun register() {
        active = true
        when (mode) {
            DamagerType.SLOT_CHANGE -> {
                onSlotSwitch.register()
//                onItemClick.register()
            }

            DamagerType.BLOCK_MOVE -> {
                onMove.register()
            }

            DamagerType.INTERVAL -> {
                active = true
                repeat()
            }

            DamagerType.VERTICAL_MOVE -> {
                onMove2.register()
            }
        }
    }

    override fun unregister() {
        active = false
        listener.forEach { it.unregister() }
    }


    // Challenge Methods
    private val onSlotSwitch = listen<PlayerItemHeldEvent>(register = false) {
        damage(it.player)
    }
    private val onItemClick = listen<InventoryClickEvent>(register = false) {
        val player = it.whoClicked as? Player ?: return@listen

        when (it.click) {
            ClickType.DOUBLE_CLICK, ClickType.LEFT, ClickType.SHIFT_LEFT,
            ClickType.RIGHT, ClickType.SHIFT_RIGHT, ClickType.NUMBER_KEY,
            ClickType.SWAP_OFFHAND -> damage(player)

            else -> {}
        }
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val from = it.from.block
        val to = it.to.block
        if (from.x == to.x && from.z == to.z) return@listen
        damage(it.player)
    }
    private val onMove2 = listen<PlayerMoveEvent>(register = false) {
        val from = it.from.block
        val to = it.to.block
        if (from.y == to.y) return@listen
        damage(it.player)
    }

    private fun repeat() {
        val delay = interval * 20L
        task(true, delay, delay) {
            if (stopped) it.cancel()
            if (!active) return@task
            onlinePlayers.forEach { player ->
                if (player.gameMode != GameMode.SURVIVAL) return@forEach
                if (Spectator.isSpectator(player.uniqueId)) return@forEach
                damage(player)
            }
        }
    }

    private fun damage(player: Player) {
        val health = player.health
        player.health = if ((health - damage) <= 0) 0.0 else health - damage
        player.damage(0.01)
    }
}
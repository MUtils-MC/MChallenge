package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.utils.enums.challenges.ChDamager
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent

class Damager : Challenge {
    override val challenge = Challenge.DAMAGER
    private val listener = ArrayList<Listener>()
    private var damage = 1.0
    private var active = false
    private var mode = ChDamager.SLOT_CHANGE

    override fun start(): Boolean {
        val conf = ConfigManager.getConfig(Configs.MODULES)
        mode = ChDamager.valueOf(conf.getString("DAMAGER.Mode") ?: "SLOT_CHANGE")
        damage = conf.getDouble("DAMAGER.Damage")
        active = true
        when (mode) {
            ChDamager.SLOT_CHANGE -> {
                listener.add(onSlotSwitch)
                listener.add(onItemClick)
            }
            ChDamager.BLOCK_MOVE -> {
                listener.add(onMove)
            }
            ChDamager.INTERVAL -> {
                repeat()
            }
            ChDamager.VERTICAL_MOVE -> {
                listener.add(onMove2)
            }
        }
        return true
    }

    override fun stop() {
        active = false
    }

    override fun register() {
        val conf = ConfigManager.getConfig(Configs.MODULES)
        when (mode) {
            ChDamager.SLOT_CHANGE -> {
                onSlotSwitch.register()
                onItemClick.register()
            }
            ChDamager.BLOCK_MOVE -> {
                onMove.register()
            }
            ChDamager.INTERVAL -> {}
            ChDamager.VERTICAL_MOVE -> {
                onMove2.register()
            }
        }

        if (conf.getString("DAMAGER.Mode") == "INTERVAL")
            repeat()
        damage = conf.getDouble("DAMAGER.Damage")
    }

    override fun unregister() {
        listener.forEach { it.unregister() }
        damage = 0.0
    }


    // Challenge Methods
    private val onSlotSwitch = listen<PlayerItemHeldEvent>(register = false) {
        damage(it.player)
    }
    private val onItemClick = listen<InventoryClickEvent>(register = false) {
        if (it.whoClicked !is Player) return@listen
        val player = it.whoClicked as Player

        when (it.click) {
            ClickType.DOUBLE_CLICK, ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT, ClickType.NUMBER_KEY, ClickType.SWAP_OFFHAND
                -> damage(player)
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
        task(true, 20, 20) {
            if (!active) {
                it.cancel()
                return@task
            }
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
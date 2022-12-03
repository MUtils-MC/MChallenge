package de.miraculixx.mutils.modules.challenge.mods.snake

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

class Snake : Challenge {
    override val challenge = Challenge.SNAKE
    private val map = HashMap<UUID, SnakeObj?>()

    override fun start(): Boolean {
        map.clear()
        for (player in onlinePlayers) {
            if (player.gameMode == GameMode.SURVIVAL && !Spectator.isSpectator(player.uniqueId)) {
                val obj = SnakeObj(player)
                map[player.uniqueId] = obj
            }
        }
        return true
    }

    override fun stop() {
        map.forEach { (_, obj) ->
            obj?.stop()
        }
        map.clear()
    }

    override fun register() {
        onDie.register()
        onPlace.register()
        onBreak.register()
    }
    override fun unregister() {
        onDie.unregister()
        onPlace.unregister()
        onBreak.unregister()
    }

    private val onDie = listen<PlayerDeathEvent>(register = false) {
        if (it.entity.scoreboardTags.contains("SnakeDeath")) {
            it.entity.removeScoreboardTag("SnakeDeath")
            it.deathMessage = msg("modules.ch.snake.hit", it.entity, pre = false)
        } else if (it.entity.scoreboardTags.contains("SnakeLeave")) {
            it.entity.removeScoreboardTag("SnakeLeave")
            it.deathMessage = msg("modules.ch.snake.leave", it.entity, pre = false)
        }
    }

    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        if (it.blockAgainst.type.name.contains("GLAZED_TERRACOTTA")) {
            if (map[it.player.uniqueId] != null) map[it.player.uniqueId]?.addBlock(it.block)
        }
    }


    private val onBreak = listen<BlockBreakEvent>(register = false) {
        if (it.block.type == Material.RED_GLAZED_TERRACOTTA) {
            it.isCancelled = true
        }
    }
}
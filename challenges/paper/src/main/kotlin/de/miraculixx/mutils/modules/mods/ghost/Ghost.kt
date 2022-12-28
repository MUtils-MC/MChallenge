package de.miraculixx.mutils.modules.mods.ghost

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.utils.getRPPrompt
import de.miraculixx.mutils.utils.settings
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent


class Ghost : Challenge {
    override val challenge = Challenges.GHOST
    private var currentBlock = HashMap<Player, Material>()
    private var ghostObj: GhostData? = null
    private var adventure: Boolean = false

    override fun start(): Boolean {
        ghostObj = GhostData()
        adventure = settings.getBoolean("GHOST.adventure")
        onlinePlayers.forEach {
            it.setResourcePack("https://www.dropbox.com/s/idlvm997ybi8ms3/Ghost.zip?dl=1", "", true, getRPPrompt("player", "Ghost-Challenge"))
        }
        return true
    }

    override fun stop() {
        ghostObj = null
        onlinePlayers.forEach {
            it.sendGhostRP()
        }
    }

    override fun register() {
        onSelect.register()
        onDeselect.register()
        onMove.register()
        onBreakBlock.register()
        onBlockPhysics.register()
        onSandFall.register()
        onCollect.register()
        onJoin.register()
    }

    override fun unregister() {
        onSelect.unregister()
        onDeselect.unregister()
        onMove.unregister()
        onBreakBlock.unregister()
        onBlockPhysics.unregister()
        onSandFall.unregister()
        onCollect.unregister()
        onJoin.unregister()
    }

    private val onSelect = listen<PlayerInteractEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        if (it.action == Action.RIGHT_CLICK_BLOCK) {
            if (it.player.isSneaking && it.clickedBlock?.type != Material.SCAFFOLDING) {
                if (currentBlock.containsKey(it.player)) ghostObj?.reset(it.player, currentBlock[it.player]!!)

                currentBlock[it.player] = it.clickedBlock!!.type
                ghostObj?.update(it.player, it.clickedBlock!!.type)
                it.player.world.spawnParticle(Particle.END_ROD, it.player.location, 50, 0.3, 0.1, 0.3, 0.2)
                it.player.playSound(it.player.location, Sound.BLOCK_SCULK_SENSOR_CLICKING, 1f, 0.8f)
                if (adventure) it.player.gameMode = GameMode.ADVENTURE
                it.isCancelled = true
            }
        }
    }

    private val onDeselect = listen<PlayerSwapHandItemsEvent>(register = false) {
        it.isCancelled = true
        if (currentBlock.containsKey(it.player)) ghostObj?.reset(it.player, currentBlock[it.player]!!)
        currentBlock.remove(it.player)
        it.player.playSound(it.player.location, Sound.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1f, 0.8f)
        ghostObj?.removePlayer(it.player)
        if (it.player.gameMode == GameMode.ADVENTURE) it.player.gameMode = GameMode.SURVIVAL
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (!currentBlock.containsKey(it.player)) return@listen

        if (it.to.block.type == Material.SCAFFOLDING) {
            ghostObj?.addPlayer(it.player)
        } else
            ghostObj?.removePlayer(it.player)

        if (it.to.blockX == it.from.blockX && it.to.blockZ == it.from.blockZ && it.to.blockY == it.from.blockY) return@listen
        ghostObj?.update(it.player, currentBlock[it.player]!!)
    }

    private val onBreakBlock = listen<BlockBreakEvent>(register = false) {
        if (it.block.type == Material.SCAFFOLDING || it.block.type == Material.STRUCTURE_VOID) it.isCancelled = true
    }

    private val onBlockPhysics = listen<BlockPhysicsEvent>(register = false) {
        if (it.block.type == Material.SCAFFOLDING) it.isCancelled = true
        if (it.block.type == Material.STRUCTURE_VOID) it.isCancelled = true
    }

    private val onSandFall = listen<EntityChangeBlockEvent>(register = false) {
        if (it.entityType == EntityType.FALLING_BLOCK && it.to == Material.AIR) {
            if (it.block.type == Material.SCAFFOLDING || it.block.type == Material.STRUCTURE_VOID) {
                it.isCancelled = true
                it.block.state.update(false, false)
            }
        }
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.item.itemStack.type == Material.SCAFFOLDING) {
            it.isCancelled = true
            it.item.remove()
        }
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        it.player.sendGhostRP()
    }

    private fun Player.sendGhostRP() {
        setResourcePack("https://www.dropbox.com/s/idlvm997ybi8ms3/Ghost.zip?dl=1", "", true, getRPPrompt("player", "Ghost-Challenge"))
    }
}
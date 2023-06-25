package de.miraculixx.mchallenge.modules.mods.misc.checkpoints

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.spectator.Spectator
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import java.util.*

class Checkpoints : Challenge {
    private val actionList = HashMap<UUID, CheckpointsData?>()

    override fun start(): Boolean {
        return true
    }

    override fun stop() {
        onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            if (actionList.containsKey(uuid)) {
                actionList[uuid]?.reset()
                actionList.remove(uuid)
            }
        }
    }

    override fun register() {
        onSwitch.register()
        onCombust.register()
        onBreak.register()
        onPlace.register()
        onExplode.register()
        onKill.register()
        onChestOpen.register()
    }

    override fun unregister() {
        onSwitch.unregister()
        onCombust.unregister()
        onBreak.unregister()
        onPlace.unregister()
        onExplode.unregister()
        onKill.unregister()
        onChestOpen.unregister()
    }

    private val onSwitch = listen<PlayerSwapHandItemsEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        if (Spectator.isSpectator(uuid) || player.gameMode == GameMode.SPECTATOR) return@listen
        it.isCancelled = true
        if (actionList[uuid] == null) {
            //Neuer Ghost
            val zombie = player.world.spawnEntity(player.location, EntityType.ZOMBIE) as Zombie
            zombie.isInvisible = true
            zombie.isInvulnerable = true
            zombie.isPersistent = true
            zombie.setAI(false)
            zombie.isGlowing = true
            zombie.setGravity(false)
            zombie.isSilent = true
            actionList[uuid] = CheckpointsData(it.player, zombie)
        } else {
            //Teleportiere zum Ghost
            actionList[uuid]?.reset()
            actionList.remove(uuid)
        }
    }

    private val onCombust = listen<EntityCombustEvent>(register = false) {
        if (it.entity.hasGravity()) return@listen
        it.isCancelled = true
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        val uuid = it.player.uniqueId
        if (actionList[uuid] == null) return@listen
        actionList[uuid]?.addBrokenBlock(it.block)
    }

    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        val uuid = it.player.uniqueId
        if (actionList[uuid] == null) return@listen
        actionList[uuid]?.addPlacedBlock(it.block)
    }

    private val onExplode = listen<EntityExplodeEvent>(register = false) {
        onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            if (actionList[uuid] == null) return@forEach
            for (block in it.blockList()) {
                actionList[uuid]?.addBrokenBlock(block)
            }
        }
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val damager = it.damager
        if (damager !is Player) return@listen
        val taker = it.entity
        if (taker !is LivingEntity) return@listen
        if (actionList[damager.uniqueId] == null) return@listen
        if (taker.health - it.damage <= 0.0) {
            actionList[damager.uniqueId]?.addEntity(taker)
        }
    }

    private val onChestOpen = listen<PlayerInteractEvent>(register = false) {
        val uuid = it.player.uniqueId
        if (actionList[uuid] == null) return@listen
        val block = it.clickedBlock ?: return@listen
        if (block.type == Material.CHEST || block.type == Material.BARREL) {
            actionList[uuid]?.addPlacedBlock(block)
        }
    }
}
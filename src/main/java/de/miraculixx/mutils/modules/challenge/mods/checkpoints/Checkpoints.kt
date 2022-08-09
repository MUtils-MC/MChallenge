package de.miraculixx.mutils.modules.challenge.mods.checkpoints

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
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

class Checkpoints : Challenge {
    override val challenge = Modules.CHECKPOINTS
    private val actionList = HashMap<Player, CheckpointsData?>()

    override fun start(): Boolean {
        return true
    }

    override fun stop() {
        for (player in onlinePlayers) {
            if (actionList.containsKey(player)) {
                actionList[player]?.reset()
                actionList.remove(player)
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
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        if (it.player.gameMode == GameMode.SPECTATOR) return@listen
        it.isCancelled = true
        if (actionList[it.player] == null) {
            //Neuer Ghost
            val zombie = it.player.world.spawnEntity(it.player.location, EntityType.ZOMBIE) as Zombie
            zombie.isInvisible = true
            zombie.isInvulnerable = true
            zombie.isPersistent = true
            zombie.setAI(false)
            zombie.isGlowing = true
            zombie.setGravity(false)
            zombie.isSilent = true
            actionList[it.player] = CheckpointsData(it.player, zombie)
        } else {
            //Teleportiere zum Ghost
            actionList[it.player]?.reset()
            actionList.remove(it.player)
        }
    }

    private val onCombust = listen<EntityCombustEvent>(register = false) {
        if (it.entity.hasGravity()) return@listen
        it.isCancelled = true
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        if (actionList[it.player] == null) return@listen
        actionList[it.player]?.addBrokenBlock(it.block)
    }

    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        if (actionList[it.player] == null) return@listen
        actionList[it.player]?.addPlacedBlock(it.block)
    }

    private val onExplode = listen<EntityExplodeEvent>(register = false) {
        for (onlinePlayer in onlinePlayers) {
            if (actionList[onlinePlayer] == null) continue
            for (block in it.blockList()) {
                actionList[onlinePlayer]?.addBrokenBlock(block)
            }
        }
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        if (it.entity !is LivingEntity) return@listen
        if (actionList[(it.damager as Player).player] == null) return@listen
        if ((it.entity as LivingEntity).health - it.damage <= 0.0) {
            actionList[(it.damager as Player).player]?.addEntity(it.entity)
        }
    }

    private val onChestOpen = listen<PlayerInteractEvent>(register = false) {
        if (actionList[it.player] == null) return@listen
        if (it.clickedBlock == null) return@listen
        if (it.clickedBlock!!.type == Material.CHEST || it.clickedBlock!!.type == Material.BARREL) {
            actionList[it.player]?.addPlacedBlock(it.clickedBlock!!)
        }
    }
}
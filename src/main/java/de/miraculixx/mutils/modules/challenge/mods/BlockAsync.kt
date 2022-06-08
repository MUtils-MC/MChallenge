package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.*
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack


class BlockAsync : Challenge() {
    override val challenge = Modules.BLOCK_ASYNC
    private val blockList = HashMap<Location, Player>() //Location ist der fake Block - Player der Spieler, welcher den Block abgebaut hat
    private val dataList = HashMap<Location, BlockData>() //Location ist der fake Block - Material der ursprüngliche Block Type

    override fun start(): Boolean {
        onlinePlayers.forEach { first ->
            val player = first.player
            onlinePlayers.forEach { second ->
                val target = second.player
                if (target != player) player!!.hidePlayer(Main.INSTANCE, target!!)
            }
        }
        return true
    }

    override fun stop() {
        blockList
    }

    override fun register() {
        onBreak.register()
        onInteract.register()
        onJoin.register()
    }

    override fun unregister() {
        onBreak.unregister()
        onInteract.unregister()
        onJoin.unregister()
    }

    @Suppress("DEPRECATION")
    private val onBreak = listen<BlockBreakEvent>(register = false) { e ->
        e.isCancelled = true
        if (e.player.gameMode == GameMode.SURVIVAL) {
            for (drop in e.block.drops) {
                e.block.location.world?.dropItem(e.block.location.add(0.5, 0.0, 0.5), drop)
            }
            val item = e.player.inventory.itemInMainHand
            if (item.type.maxDurability > 0) {
                val dura = item.durability.plus(1).toShort()
                e.player.inventory.itemInMainHand.durability = dura
                if (dura == item.type.maxDurability) e.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            }
        }

        //Remove Shulker Block
        if (e.block.type == Material.RED_CONCRETE) {
            for (nearbyEntity in e.player.getNearbyEntities(8.0, 8.0, 8.0)) {
                if (nearbyEntity.type == EntityType.SHULKER) nearbyEntity.remove()
            }
        }

        //Block Faking
        val blockdata = e.block.blockData
        blockList[e.block.location] = e.player
        dataList[e.block.location] = blockdata
        e.block.type = Material.AIR

        task(true, 0, 1, 4) {
            onlinePlayers.forEach { player ->
                if (e.player != player) {
                    if (player.world.environment == e.player.world.environment) player.sendBlockChange(e.block.location, blockdata)
                }
            }
        }

        task(true, 0, 1, 2) {
            blockList.forEach { (loc, player) ->
                if (player != e.player) {
                    if (e.player.world.environment == loc.world?.environment) e.player.sendBlockChange(loc, dataList[loc]!!)
                }
            }
        }
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        if (it.clickedBlock == null) return@listen
        if (it.clickedBlock!!.type == Material.AIR) {
            //Setze alle Fake Blöcke zurück
            blockList.forEach { (loc, _) ->
                loc.block.type = Material.RED_CONCRETE
                loc.block.type = Material.AIR
            }

            //Fake Block geschlagen
            it.clickedBlock!!.type = Material.AIR
            it.clickedBlock!!.type = Material.RED_CONCRETE
            it.player.bedSpawnLocation = it.clickedBlock!!.location
            it.player.damage(100.0)
            val loc = it.clickedBlock!!.location
            val locString = loc.blockX.toString() + " " + loc.blockY + " " + loc.blockZ
            broadcast(msg("modules.ch.blockAsync.death", it.player, locString))

            val shulker = it.clickedBlock!!.world.spawnEntity(it.clickedBlock!!.location, EntityType.SHULKER) as Shulker
            shulker.setAI(false)
            shulker.setGravity(false)
            shulker.isSilent = true
            shulker.isPersistent = true
            shulker.isGlowing = true
            shulker.color = DyeColor.RED
            shulker.isInvulnerable = true
            shulker.isInvisible = true

            val manager = Bukkit.getScoreboardManager().mainScoreboard
            if (manager.getTeam("DARK_RED") == null) {
                val team = manager.registerNewTeam("DARK_RED")
                team.displayName = "§4DARK_RED"
                team.color = ChatColor.DARK_RED
                team.addEntry(shulker.uniqueId.toString())
            } else {
                val team = manager.getTeam("DARK_RED")
                team!!.addEntry(shulker.uniqueId.toString())
            }

            //Reset
            broadcast(msg("modules.hide.show"))
            onlinePlayers.forEach { first ->
                onlinePlayers.forEach { second ->
                    if (second != first) first.showPlayer(Main.INSTANCE, second)
                }
            }
            blockList.clear()
            dataList.clear()
        }
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) { listener ->
        task(true, 10, 1, 2) {
            blockList.forEach { (loc, player) ->
                if (player != listener.player) {
                    listener.player.sendBlockChange(loc, dataList[loc]!!)
                }
            }
        }
    }
}
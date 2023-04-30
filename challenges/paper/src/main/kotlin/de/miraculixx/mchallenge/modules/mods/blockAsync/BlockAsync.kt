package de.miraculixx.mchallenge.modules.mods.blockAsync

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mvanilla.messages.cError
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.namespace
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.Bukkit.broadcast
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Shulker
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


class BlockAsync : Challenge {
    private val blockList = HashMap<Location, Player>() //Location ist der fake Block - Player der Spieler, welcher den Block abgebaut hat
    private val dataList = HashMap<Location, BlockData>() //Location ist der fake Block - Material der ursprüngliche Block Type
    private val hidePlayers: Boolean

    init {
        val settings = challenges.getSetting(Challenges.BLOCK_ASYNC).settings
        hidePlayers = settings["hide"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        if (hidePlayers) {
            onlinePlayers.forEach { player ->
                onlinePlayers.forEach { target ->
                    if (target != player) player.hidePlayer(PluginManager, target)
                }
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
    private val onBreak = listen<BlockBreakEvent>(register = false) {
        it.isCancelled = true
        val player = it.player
        val block = it.block
        if (player.gameMode == GameMode.SURVIVAL) {
            for (drop in block.drops) {
                block.location.world?.dropItem(block.location.add(0.5, 0.0, 0.5), drop)
            }
            val item = player.inventory.itemInMainHand
            if (item.type.maxDurability > 0) {
                val dura = item.durability.plus(1).toShort()
                player.inventory.itemInMainHand.durability = dura
                if (dura == item.type.maxDurability) player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            }
        }

        //Remove Shulker Block
        if (block.type == Material.RED_CONCRETE) {
            for (nearbyEntity in it.player.getNearbyEntities(8.0, 8.0, 8.0)) {
                if (nearbyEntity.type == EntityType.SHULKER) nearbyEntity.remove()
            }
        }

        //Block Faking
        val blockdata = block.blockData
        blockList[block.location] = it.player
        dataList[block.location] = blockdata
        block.type = Material.AIR

        task(true, 0, 1, 4) {
            onlinePlayers.forEach { p ->
                if (player != p) {
                    if (p.world.environment == player.world.environment) p.sendBlockChange(block.location, blockdata)
                }
            }
        }

        task(true, 0, 1, 2) {
            blockList.forEach { (loc, p) ->
                if (p != player) {
                    if (player.world.environment == loc.world?.environment) dataList[loc]?.let { data -> player.sendBlockChange(loc, data) }
                }
            }
        }
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        val clickedBlock = it.clickedBlock ?: return@listen
        if (clickedBlock.type == Material.AIR) {
            val player = it.player
            //Setze alle Fake Blöcke zurück
            blockList.forEach { (loc, _) ->
                loc.block.type = Material.RED_CONCRETE
                loc.block.type = Material.AIR
            }

            //Fake Block geschlagen
            clickedBlock.type = Material.AIR
            clickedBlock.type = Material.RED_CONCRETE
            it.player.bedSpawnLocation = clickedBlock.location
            player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "blockAsync")
            it.player.damage(100.0)

            val shulker = clickedBlock.world.spawnEntity(clickedBlock.location, EntityType.SHULKER) as Shulker
            shulker.setAI(false)
            shulker.setGravity(false)
            shulker.isSilent = true
            shulker.isPersistent = true
            shulker.isGlowing = true
            shulker.color = DyeColor.RED
            shulker.isInvulnerable = true
            shulker.isInvisible = true

            val manager = Bukkit.getScoreboardManager().mainScoreboard
            val redTeam = manager.getTeam("DARK_RED")
            if (redTeam == null) {
                val team = manager.registerNewTeam("DARK_RED")
                team.displayName(cmp("DARK_RED", cError))
                team.color(NamedTextColor.DARK_RED)
                team.addEntry(shulker.uniqueId.toString())
            } else redTeam.addEntry(shulker.uniqueId.toString())

            //Reset
            broadcast(msg("event.hide.show"))
            onlinePlayers.forEach { first ->
                onlinePlayers.forEach { second ->
                    if (second != first) first.showPlayer(PluginManager, second)
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
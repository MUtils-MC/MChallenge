package de.miraculixx.mchallenge.modules.mods.multiplayer.blockAsync

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.challenges.interfaces.CommandChallenge
import de.miraculixx.mchallenge.utils.bc
import de.miraculixx.mchallenge.utils.command
import de.miraculixx.mcommons.namespace
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
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


class BlockAsync : Challenge, CommandChallenge {
    private val blockList = HashMap<Location, Pair<Player, BlockData>>() //Location ist der fake Block - Player der Spieler, welcher den Block abgebaut hat
    private val hidePlayers: Boolean

    override val command = command("blockasync") {
        literalArgument("show-all") {
            playerExecutor { _, _ ->
                bc(prefix, "event.hide.show")
                revealAll()
                onlinePlayers.forEach { first ->
                    onlinePlayers.forEach { second ->
                        if (second != first) first.showPlayer(PluginManager, second)
                    }
                }
            }
        }
    }

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

        if (!Bukkit.getAllowFlight()) {
            bc(prefix, "event.enable-flight")
        }

        registerCommand()
        return true
    }

    override fun stop() {
        blockList
        unregisterCommand()
    }

    override fun register() {
        onBreak.register()
        onInteract.register()
        onJoin.register()
        onRespawn.register()
    }

    override fun unregister() {
        onBreak.unregister()
        onInteract.unregister()
        onJoin.unregister()
        onRespawn.unregister()
    }

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
            it.player.location.getNearbyEntitiesByType(Shulker::class.java, 5.0).forEach { entity -> entity.remove() }
        }

        //Block Faking
        val blockdata = block.blockData
        blockList[block.location] = it.player to blockdata
        block.type = Material.AIR

        // Update for others
        task(true, 0, 1, 4) {
            onlinePlayers.forEach { otherPlayer ->
                if (otherPlayer != player) updateBlocksForPlayer(otherPlayer)
            }
        }

        updateBlocksForPlayer(player)
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        val clickedBlock = it.clickedBlock ?: return@listen
        if (clickedBlock.type == Material.AIR) {
            val player = it.player

            // hit fake block
            clickedBlock.type = Material.AIR
            clickedBlock.type = Material.RED_CONCRETE
            it.player.respawnLocation = clickedBlock.location
            player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "blockAsync")
            it.player.damage(100.0)

            // spawn indicator block
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

            // reset if timer is setuped to end
            taskRunLater(5) {
                if (onlinePlayers.none { p -> p.gameMode != GameMode.SPECTATOR }) {
                    broadcast(cmp("The game has ended. All players are in spectator mode."))
                    bc(prefix, "event.hide.show")
                    onlinePlayers.forEach { first ->
                        onlinePlayers.forEach { second ->
                            if (second != first) first.showPlayer(PluginManager, second)
                        }
                    }
                    revealAll()
                    return@taskRunLater
                }
            }
        }
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) { listener ->
        taskRunLater(20) {
            updateBlocksForPlayer(listener.player)
        }
    }

    private val onRespawn = listen<PlayerPostRespawnEvent>(register = false) {
        task(true, 20, 5, 3) { _ ->
            updateBlocksForPlayer(it.player)
        }
    }

    private fun updateBlocksForPlayer(player: Player) {
        val currentEnv = player.world.environment
        val allPackets = blockList.filter { it.value.first != player && it.key.world.environment == currentEnv }
        player.sendMultiBlockChange(buildMap { allPackets.forEach { (location, data) -> put(location, data.second) } })
    }

    private fun revealAll() {
        blockList.forEach { (loc, _) ->
            loc.block.type = Material.RED_CONCRETE
            loc.block.type = Material.AIR
        }
        blockList.clear()
    }
}
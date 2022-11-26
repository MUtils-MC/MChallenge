@file:Suppress("unused")

package de.miraculixx.mutils.modules.challenge.mods.ALPHA

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.configSettings
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket
import net.minecraft.world.level.border.WorldBorder
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class Captive : Challenge() {
    override val challenge = Challenge.CAPTIVE
    private var center: Location? = null
    private var centerN: Location? = null

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
            player.teleport(player.world.getHighestBlockAt(player.location).location.add(0.5, 1.0, 0.5))
            val craftPlayer = (player as CraftPlayer).handle
            val wb = WorldBorder()
            wb.world = (player.world as CraftWorld).handle
            wb.setCenter(player.world.getHighestBlockAt(player.location).location.add(0.5, 1.0, 0.5).x, player.world.getHighestBlockAt(player.location).location.add(0.5, 1.0, 0.5).z)
            wb.size = configSettings.getDouble("Settings.Captive.Size")
            craftPlayer!!.b.sendPacket(ClientboundInitializeBorderPacket(wb))
        }

        //Register
        onChangeDim.register()
        onChangeDim.register()
        onJoin.register()
        onDeath.register()
        onRespawn.register()
        return true
    }

    private val onChangeLevel = listen<PlayerLevelChangeEvent>(register = false) {
        if (center == null) {
            center = it.player.location.block.location.clone().add(0.5, 0.0, 0.5)
        }
        onlinePlayers.forEach { player ->
            createBorder(player, player.world.environment == World.Environment.NETHER, delay = false, red = it.newLevel < it.oldLevel)
        }
    }

    private val onChangeDim = listen<PlayerPortalEvent>(register = false) {
        if (it.to?.world?.environment == World.Environment.NETHER) {
            centerN = it.to!!.block.location.clone().add(0.5, 0.0, 0.5)
            createBorder(it.player, true, delay = true, red = false)
        } else {
            createBorder(it.player, false, delay = true, red = false)
        }
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        task(true, 20, 20, 1) { task ->
            createBorder(it.player, false, delay = false, red = true)
            task.cancel()
        }
    }

    private val onDeath = listen<PlayerDeathEvent>(register = false) {
        onlinePlayers.forEach { player ->
            if (player.world.environment == World.Environment.NETHER) createBorder(player, true, delay = false, red = true)
            else createBorder(player, false, delay = false, red = true)
        }
    }

    private val onRespawn = listen<PlayerRespawnEvent>(register = false) {
        task(true, 20, 20, 1) { task ->
            createBorder(it.player, false, delay = false, red = true)
            task.cancel()
        }
    }

    private fun createBorder(player: Player, nether: Boolean, delay: Boolean, red: Boolean) {
        val config = ConfigManager.getConfig(Configs.MODULES)
        var count = config.getInt("CAPTIVE.Size")
        onlinePlayers.forEach { playerO ->
            if (playerO.gameMode == GameMode.SURVIVAL) {
                count += playerO.level * configSettings.getInt("Settings.Captive.Amplifier") * 2
            }
        }
        count--
        val nmsPlayer = (player as CraftPlayer).handle
        val wb = WorldBorder()
        if (delay) {
            task(true, 40, 40, 1) {
                if (nether) {
                    wb.world = (centerN?.world as CraftWorld).handle
                    wb.setCenter(centerN!!.x, centerN!!.z)
                } else {
                    wb.world = (center?.world as CraftWorld).handle
                    wb.setCenter(center!!.x, center!!.z)
                }
                wb.size = count.toDouble() + configSettings.getDouble("Settings.Captive.Size")
                nmsPlayer.b.sendPacket(ClientboundInitializeBorderPacket(wb))

                it.cancel()
                return@task
            }
        } else {
            if (nether) {
                wb.world = (centerN?.world as CraftWorld).handle
                wb.setCenter(centerN!!.x, centerN!!.z)
                wb.
            } else {
                wb.world = (center?.world as CraftWorld).handle
                wb.setCenter(center!!.x, center!!.z)
            }

            val size = count.toDouble() + configSettings.getDouble("Settings.Captive.Size")
            if (red) {
                wb.transitionSizeBetween(size.plus(configSettings.getDouble("Settings.Captive.Amplifier") * 2), size, 2000)
            } else {
                wb.transitionSizeBetween(size.minus(configSettings.getDouble("Settings.Captive.Amplifier") * 2), size, 2000)
            }
            nmsPlayer.b.sendPacket(ClientboundInitializeBorderPacket(wb))
            broadcast("SetBorder for ${nmsPlayer.name}")
        }
    }
}
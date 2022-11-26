package de.miraculixx.mutils.modules.challenge.mods.dimSwap

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.challenge.utils.MobBossBar
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class DimSwap : Challenge {
    override val challenge = Challenge.DIM_SWAP

    override fun start(): Boolean {
        for (player in onlinePlayers) {
            player.sendTitle(msg("module.challenge.dimSwap.prepare", pre = false), "§cPlease wait a moment", 10, 9999, 0)
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, true))
        }
        if (DimSwapSchedule().worldGen()) {
            onlinePlayers.forEach {
                it.removePotionEffect(PotionEffectType.BLINDNESS)
                it.sendTitle(" "," ",1,1,1)
            }
            return false
        }
        return true
    }

    override fun stop() {}
    override fun register() {
        onEndportal.register()
        onPortal.register()
    }
    override fun unregister() {
        onEndportal.unregister()
        onPortal.unregister()
    }

    private val onEndportal = listen<PlayerMoveEvent>(register = false) {
        if (it.player.world.environment != World.Environment.THE_END) return@listen
        if (it.to.world == null) return@listen
        val loc = Location(it.to.world, it.to.x, it.to.y - 1, it.to.z)
        if (it.to.world?.getBlockAt(loc)?.type == Material.END_PORTAL || it.to.world?.getBlockAt(loc)?.type == Material.BEDROCK) {
            val world = worlds[0]
            it.player.teleport(world.getHighestBlockAt(0, -60).location)
            val dragon = world.spawn(
                world.getHighestBlockAt(0, 0).location.add(0.5, 10.0, 0.5),
                EnderDragon::class.java
            ) { ed: EnderDragon -> ed.phase = EnderDragon.Phase.CIRCLING }
            dragon.phase = EnderDragon.Phase.FLY_TO_PORTAL
            dragon.setAI(true)
            MobBossBar(dragon, BarColor.PINK, "Ender Dragon")
        }
    }

    private val onPortal = listen<PlayerPortalEvent>(register = false) {
        if (it.to.world == null || it.from.world == null) return@listen
        if (it.to.world!!.environment == World.Environment.NORMAL && it.from.world!!.environment == World.Environment.NETHER) {
            it.isCancelled = true
            val mainWorld = worlds[0]
            val endWorld = Bukkit.getWorld("${mainWorld.name}_the_end")
            if (endWorld == null) {
                Bukkit.broadcastMessage(msg("module.challenge.worldNotFound", input = "${mainWorld.name}_the_end"))
                return@listen
            }
            val y = endWorld.getHighestBlockYAt(0, 1130)
            if (y < 30) {
                //Plattform builder
                for (x in -2..2) {
                    for (z in 1128..1132) {
                        endWorld.getBlockAt(x, 60, z).type = Material.OBSIDIAN
                    }
                }
            }
            it.player.teleport(endWorld.getHighestBlockAt(0, 1130).location)
            try {
                endWorld.enderDragonBattle!!.enderDragon!!.remove()
            } catch (exception: NullPointerException) {
                endWorld.enderDragonBattle!!.bossBar.isVisible = false
                endWorld.enderDragonBattle!!.bossBar.removeAll()
                endWorld.enderDragonBattle!!.generateEndPortal(true)
                for (entity in endWorld.entities) {
                    if (entity is Player) continue
                    entity.remove()
                }
            }
            endWorld.enderDragonBattle!!.bossBar.isVisible = false
            endWorld.enderDragonBattle!!.bossBar.removeAll()
            endWorld.enderDragonBattle!!.generateEndPortal(true)
            for (entity in endWorld.entities) {
                if (entity is Player) continue
                entity.remove()
            }
        }
    }
}
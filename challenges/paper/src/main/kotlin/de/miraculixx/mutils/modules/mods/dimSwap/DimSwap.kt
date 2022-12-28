package de.miraculixx.mutils.modules.mods.dimSwap

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.Challenge
import de.miraculixx.mutils.utils.MobBossBar
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

class DimSwap : Challenge {
    override val challenge = Challenges.DIM_SWAP

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
            player.showTitle(
                Title.title(
                    cmp("Prepare all worlds", cHighlight), cmp("Please wait a moment", cError),
                    Title.Times.times(Duration.ofMillis(300), Duration.ofHours(1), Duration.ofMillis(0))
                )
            )
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, true))
        }
        if (DimSwapSchedule().worldGen()) {
            onlinePlayers.forEach {
                it.removePotionEffect(PotionEffectType.BLINDNESS)
                it.showTitle(Title.title(emptyComponent(), emptyComponent()))
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
        val to = it.to.world
        val from = it.from.world
        if (to == null || from == null) return@listen
        if (to.environment == World.Environment.NORMAL && from.environment == World.Environment.NETHER) {
            it.isCancelled = true
            val mainWorld = worlds[0]
            val endWorld = Bukkit.getWorld("${mainWorld.name}_the_end")
            if (endWorld == null) {
                broadcast(prefix + msg("event.worldNotFound", listOf("${mainWorld.name}_the_end")))
                return@listen
            }
            val y = endWorld.getHighestBlockYAt(0, 1130)
            if (y < 30) {
                // Plattform builder
                for (x in -2..2) {
                    for (z in 1128..1132) {
                        endWorld.getBlockAt(x, 60, z).type = Material.OBSIDIAN
                    }
                }
            }
            it.player.teleport(endWorld.getHighestBlockAt(0, 1130).location)
            endWorld.enderDragonBattle?.enderDragon?.remove()

            val battle = endWorld.enderDragonBattle
            battle?.bossBar?.isVisible = false
            battle?.bossBar?.removeAll()
            battle?.generateEndPortal(true)
            task(true, 0, 10, 3) {
                endWorld.entities.forEach { entity ->
                    if (entity.type == EntityType.ENDER_DRAGON) entity.remove()
                }
            }
        }
    }
}
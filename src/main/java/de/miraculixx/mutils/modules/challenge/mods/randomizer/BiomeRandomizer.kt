package de.miraculixx.mutils.modules.challenge.mods.randomizer

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.utils.broadcastEffect
import de.miraculixx.mutils.utils.broadcastTitle
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.World.Environment
import org.bukkit.potion.PotionEffectType

class BiomeRandomizer : Challenge {
    override val challenge = Modules.RANDOMIZER_BIOMES
    private val chWorlds = ArrayList<World>()

    override fun start(): Boolean {
        broadcastTitle("§6LOADING WORLDS", "§cPlease wait a moment...", 0, 9999, 0)
        broadcastEffect(PotionEffectType.BLINDNESS, 99999,1)
        val worldCreator = WorldTools()

        task(true, 0, 60, 4) {
            when (it.counterUp) {
                1L -> chWorlds.add(worldCreator.createWorld("CH-RNDBIOME", Environment.NORMAL, BiomeProviders.BIOME_SWITCH)?: return@task)
                2L -> chWorlds.add(worldCreator.createWorld("CH-RNDBIOME_nether", Environment.NETHER, BiomeProviders.BIOME_SWITCH) ?: return@task)
                3L -> chWorlds.add(worldCreator.createWorld("CH-RNDBIOME_the_end", Environment.THE_END, BiomeProviders.BIOME_SWITCH) ?: return@task)
                4L -> {
                    onlinePlayers.forEach { pl ->
                        pl.teleport(chWorlds.first().spawnLocation)
                        pl.removePotionEffect(PotionEffectType.BLINDNESS)
                        pl.playSound(pl, Sound.ENTITY_ENDERMAN_TELEPORT, 1f,1f )
                    }
                    broadcastTitle(" "," ",1,1,1)
                }
            }
        }
        return true
    }

    override fun stop() {
        val worldTools = WorldTools()
        chWorlds.forEach { world ->
            worldTools.deleteWorld(world)
        }
    }

    override fun register() {}
    override fun unregister() {}
}
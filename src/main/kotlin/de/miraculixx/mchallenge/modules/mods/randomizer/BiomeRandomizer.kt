package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mcommons.extensions.title
import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cMark
import de.miraculixx.mcommons.text.cmp
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.World.Environment
import org.bukkit.WorldCreator
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class BiomeRandomizer : Challenge {
    private val chWorlds = ArrayList<World>()

    override fun start(): Boolean {
        onlinePlayers.forEach {
            it.title(cmp("LOADING WORLDS", cHighlight), cmp("Please wait a moment...", cMark), Duration.ZERO, 1.hours, Duration.ZERO)
            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1) )
        }

        task(true, 0, 60, 4) {
            when (it.counterUp) {
                1L -> chWorlds.add(WorldCreator("CH-RNDBIOME").environment(Environment.NORMAL).biomeProvider(RandomBiomes()).createWorld() ?: return@task)
                2L -> chWorlds.add(WorldCreator("CH-RNDBIOME_nether").environment(Environment.NETHER).biomeProvider(RandomBiomes()).createWorld() ?: return@task)
                3L -> chWorlds.add(WorldCreator("CH-RNDBIOME_the_end").environment(Environment.THE_END).biomeProvider(RandomBiomes()).createWorld() ?: return@task)
                4L -> {
                    onlinePlayers.forEach { pl ->
                        pl.teleport(chWorlds.first().spawnLocation)
                        pl.removePotionEffect(PotionEffectType.BLINDNESS)
                        pl.playSound(pl, Sound.ENTITY_ENDERMAN_TELEPORT, 1f,1f )
                    }
                    onlinePlayers.forEach { p -> p.clearTitle() }
                }
            }
        }
        return true
    }

    override fun stop() {
        val backupSpawn = worlds.first().spawnLocation
        chWorlds.forEach { world ->
            world.players.forEach { pl ->
                pl.teleport(backupSpawn)
                pl.playSound(pl, Sound.ENTITY_ENDERMAN_TELEPORT, 1f,1f )
            }
            Bukkit.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }
    }

    override fun register() {
    }

    override fun unregister() {
    }

    class RandomBiomes() : BiomeProvider() {
        private val biomeList = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).toMutableList()
        private var random: Random? = null

        override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
            return biomeList.random(getRandom(worldInfo.seed))
        }

        override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
            return biomeList.toMutableList()
        }

        private fun getRandom(seed: Long): Random {
            return if (random == null) {
                val new = Random(seed)
                random = new
                new
            } else random!!
        }
    }
}
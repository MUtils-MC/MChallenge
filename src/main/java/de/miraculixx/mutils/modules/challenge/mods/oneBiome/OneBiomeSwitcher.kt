package de.miraculixx.mutils.modules.challenge.mods.oneBiome

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.extensions.geometry.subtract
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import kotlin.random.Random

class OneBiomeSwitcher(c: FileConfiguration) {
    private val key = NamespacedKey(Main.INSTANCE, "Challenge-OneBiome")
    private val bar = Bukkit.createBossBar(key, "§cLoading worlds...", BarColor.BLUE, BarStyle.SOLID)
    private val manager = WorldTools()
    private val delay: Int
    private val seed: Long
    private var newBiome: Biome
    private val biomeList = ArrayList<Biome>()

    init {
        delay = c.getInt("ONE_BIOME.Delay")
        onlinePlayers.forEach {
            addPlayer(it)
        }
        seed = Random.nextLong()
        newBiome = random()
        manager.createWorld("$newBiome-O", World.Environment.NORMAL, BiomeProviders.SINGLE_BIOMES, seed)
        manager.createWorld("$newBiome-N", World.Environment.NETHER, BiomeProviders.SINGLE_BIOMES, seed)
        manager.createWorld("$newBiome-E", World.Environment.THE_END, BiomeProviders.SINGLE_BIOMES, seed)
        teleport()
        run()
    }

    fun addPlayer(player: Player) {
        bar.addPlayer(player)
        bar.isVisible = true
    }

    private fun run() {
        var counter = delay
        task(false, 20, 20) {
            if (challenges != ChallengeStatus.RUNNING) {
                it.cancel()
                return@task
            }
            when (counter) {
                20 -> sync {
                    newBiome = random()
                    manager.createWorld("$newBiome-O", World.Environment.NORMAL, BiomeProviders.SINGLE_BIOMES, seed)
                }
                15 -> sync {
                    manager.createWorld("$newBiome-N", World.Environment.NETHER, BiomeProviders.SINGLE_BIOMES, seed)
                }
                10 -> sync {
                    manager.createWorld("$newBiome-E", World.Environment.THE_END, BiomeProviders.SINGLE_BIOMES, seed)
                }
                0 -> {
                    bar.setTitle("§9Next Biome Swap §9now")
                    counter = delay
                    sync {
                        teleport()
                    }
                    return@task
                }
            }
            val progress = counter.toDouble() / delay
            val left = delay - (delay - counter)
            bar.progress = progress
            bar.setTitle("§7Next Biome Swap in §9$left")

            counter--
        }
    }

    private fun teleport() {
        val locations = HashMap<Player, Location>()
        onlinePlayers.forEach {
            locations[it] = it.location
        }
        val sBiome = "§7${newBiome.name.replace('_', ' ')}"
        onlinePlayers.forEach { player ->
            val loc = locations[player]
            if (loc == null) {
                consoleMessage("$prefix §cError - No Player Location")
                return@forEach
            }
            val deS = when (loc.world?.environment) {
                World.Environment.NETHER -> "N"
                World.Environment.THE_END -> "E"
                else -> "O"
            }
            val world = Bukkit.getWorld("${newBiome.name}-$deS")
            if (world == null) {
                consoleMessage("$prefix §cError - No world destination found")
                return@forEach
            }
            val pitch = loc.pitch
            val yaw = loc.yaw
            for (x in -2..2) {
                for (y in 0..3) {
                    for (z in -2..2) {
                        val f = Location(world, loc.x + x, loc.y + y, loc.z + z)
                        world.getBlockAt(f).type = Material.AIR
                    }
                }
            }
            world.getBlockAt(loc.subtract(0, 1, 0)).type = Material.BEDROCK
            player.teleport(Location(world, loc.x, loc.y, loc.z, yaw, pitch))

            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            player.title("§9New Biome", sBiome, 0, 20, 40)
        }
        taskRunLater(20) {
            deleteOld(manager)
        }
    }

    private fun deleteOld(manager: WorldTools) {
        worlds.forEach {
            val name = it.name
            if (name.endsWith("-O") || name.endsWith("-N") || name.endsWith("-E"))
                if (!name.startsWith(newBiome.name)) manager.deleteWorld(it)
        }
    }

    private fun random(): Biome {
        if (biomeList.isEmpty()) {
            Biome.values().forEach {
                biomeList.add(it)
            }
            biomeList.remove(Biome.CUSTOM)
        }
        val biome = biomeList.random()
        biomeList.remove(biome)
        return biome
    }

    fun stop() {
        bar.isVisible = false
        Bukkit.removeBossBar(key)
        deleteOld(WorldTools())
    }
}
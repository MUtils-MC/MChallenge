package de.miraculixx.mchallenge.modules.mods.oneBiome

import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.extensions.geometry.subtract
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import java.io.File
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OneBiomeSwitcher {
    private val bar = BossBar.bossBar(cmp("Loading worlds...", cError), 1f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    private val delay: Int
    private val seed: Long
    private var newBiome: Biome
    private val biomeList = ArrayList<Biome>()

    var paused = false
    var stopped = false

    init {
        val settings = challenges.getSetting(Challenges.ONE_BIOME).settings
        delay = settings["delay"]?.toInt()?.getValue() ?: 300
        onlinePlayers.forEach {
            addPlayer(it)
        }
        seed = Random.nextLong()
        newBiome = random()
        WorldCreator("$newBiome-O").environment(World.Environment.NORMAL).biomeProvider(SingleBiomes()).seed(seed).createWorld()
        WorldCreator("$newBiome-N").environment(World.Environment.NETHER).biomeProvider(SingleBiomes()).seed(seed).createWorld()
        WorldCreator("$newBiome-E").environment(World.Environment.THE_END).biomeProvider(SingleBiomes()).seed(seed).createWorld()
        teleport()
        run()
    }

    fun addPlayer(player: Player) {
        player.showBossBar(bar)
    }

    private fun run() {
        var counter = delay
        task(false, 20, 20) {
            if (stopped) it.cancel()
            if (paused) return@task
            when (counter) {
                20 -> sync {
                    newBiome = random()
                    WorldCreator("$newBiome-O").environment(World.Environment.NORMAL).biomeProvider(SingleBiomes()).seed(seed).createWorld()
                }

                15 -> sync {
                    WorldCreator("$newBiome-N").environment(World.Environment.NETHER).biomeProvider(SingleBiomes()).seed(seed).createWorld()
                }

                10 -> sync {
                    WorldCreator("$newBiome-E").environment(World.Environment.THE_END).biomeProvider(SingleBiomes()).seed(seed).createWorld()
                }

                0 -> {
                    bar.name(cmp("Next Biome Swap ") + cmp("NOW", cHighlight))
                    counter = delay
                    sync {
                        teleport()
                    }
                    return@task
                }
            }
            val progress = counter.toFloat() / delay
            val left = delay - (delay - counter)
            bar.progress(progress)
            bar.name(cmp("Next Biome Swap in ") + cmp("$left", cHighlight))

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
                consoleAudience.sendMessage(prefix + cmp("OneBiome - No player location found", cError))
                return@forEach
            }
            val deS = when (loc.world?.environment) {
                World.Environment.NETHER -> "N"
                World.Environment.THE_END -> "E"
                else -> "O"
            }
            val world = Bukkit.getWorld("${newBiome.name}-$deS")
            if (world == null) {
                consoleAudience.sendMessage(prefix + cmp("OneBiome - No world destination found", cError))
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
            player.title(cmp("New Biome", cHighlight), cmp(sBiome), Duration.ZERO, 3.seconds, 3.seconds)
        }
        taskRunLater(20) {
            deleteOld()
        }
    }

    private fun deleteOld() {
        val worldSpawn = worlds[0].spawnLocation
        worlds.forEach {
            val name = it.name
            if (name.endsWith("-O") || name.endsWith("-N") || name.endsWith("-E"))
                if (!name.startsWith(newBiome.name)) {
                    it.players.forEach { p -> p.teleportAsync(worldSpawn) }
                    Bukkit.unloadWorld(it, false)
                    File(name).deleteRecursively()
                }
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
        onlinePlayers.forEach { it.hideBossBar(bar) }
        deleteOld()
    }
}
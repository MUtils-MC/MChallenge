package de.miraculixx.mchallenge.modules.mods.anvilCrusher

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.geometry.filledCirclePositionSet
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.spectator.Spectator
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class AnvilCrusher : Challenge {
    private val startDelay: Int
    private val amplifierDelay: Int
    private val startDens: Int
    private val amplifierDens: Int
    private val startRadius: Int
    private val amplifierRadius: Int
    private val height: Int
    private val anvilData = Bukkit.createBlockData(Material.DAMAGED_ANVIL)

    init {
        val settings = challenges.getSetting(Challenges.ANVIL_CRUSHER).settings
        val delaySection = settings["delay"]?.toSection()?.getValue()
        startDelay = delaySection?.get("startDelay")?.toInt()?.getValue() ?: 200
        amplifierDelay = delaySection?.get("amplifierDelay")?.toInt()?.getValue() ?: 5

        val densSection = settings["density"]?.toSection()?.getValue()
        startDens = densSection?.get("startDensity")?.toInt()?.getValue() ?: 5
        amplifierDens = densSection?.get("amplifierDensity")?.toInt()?.getValue() ?: 1

        val radiusSection = settings["radius"]?.toSection()?.getValue()
        startRadius = radiusSection?.get("startRadius")?.toInt()?.getValue() ?: 5
        amplifierRadius = radiusSection?.get("amplifierRadius")?.toInt()?.getValue() ?: 5

        height = settings["height"]?.toInt()?.getValue() ?: 5
        scheduler()
    }

    private var delay = startDelay
    private var density = startDens
    private var radius = startRadius
    private var paused = true
    private var stopped = false

    override fun register() {
        onDamage.register()
        paused = false
    }

    override fun unregister() {
        onDamage.unregister()
        paused = true
    }

    override fun stop() {
        paused = true
        stopped = true
    }

    private val onDamage = listen<EntityDamageEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) return@listen
        val hearts = it.finalDamage / 2
        delay = (delay - (amplifierDelay * hearts)).toInt().coerceAtLeast(20)
        density = (density + (amplifierDens * hearts)).toInt().coerceAtMost(100)
        radius = (radius + (amplifierRadius * hearts)).toInt().coerceAtMost(15)
    }

    private fun scheduler() {
        var countdown = startDelay
        task(false, 0, 1) {
            if (stopped) it.cancel()
            if (paused) return@task
            println(countdown)
            if (countdown <= 0) {
                println("spawn")
                //Spawn
                val anvilLocations: HashSet<Location> = hashSetOf()
                onlinePlayers.forEach { p ->
                    if (Spectator.isSpectator(p.uniqueId)) return@forEach
                    val world = p.world
                    val loc = p.location.toCenterLocation()
                    val center = loc.add(0.0, height.toDouble(), 0.0)
                    val blocks = center.toVector().filledCirclePositionSet(radius)

                    println("Blocks:${blocks.size} Dens:${density / 100.0}")
                    anvilLocations.addAll(blocks.shuffled()
                        .chunked((blocks.size * (density / 100.0)).toInt().coerceAtLeast(1))
                        .firstOrNull()
                        ?.map { v -> v.toLocation(world) } ?: hashSetOf())
                }
                anvilLocations.forEach { loc ->
                    sync {
                        val b = loc.world.spawnFallingBlock(loc, anvilData)
                        b.isSilent = true
                        b.dropItem = false
                    }
                }
                countdown = delay
            }
            countdown--
        }
    }
}
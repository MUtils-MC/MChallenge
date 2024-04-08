package de.miraculixx.mchallenge.modules.mods.misc.anvilCrusher

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mchallenge.utils.getDominantLocale
import de.miraculixx.mcommons.text.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.math.pow

class AnvilCrusher : Challenge {
    private val startDelay: Int
    private val amplifierDelay: Int
    private val startDens: Int
    private val amplifierDens: Int
    private val radius: Int
    private val height: Int
    private val anvilData = Bukkit.createBlockData(Material.DAMAGED_ANVIL)

    private val dominateLanguage = getDominantLocale()
    private val msgDensity = dominateLanguage.msgString("items.chS.ANVIL_CRUSHER.density.n").split(' ').last()
    private val msgDelay = dominateLanguage.msgString("items.chS.ANVIL_CRUSHER.delay.n").split(' ').last()
    private val bar = BossBar.bossBar(cmp("Loading...", cError), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)

    init {
        val settings = challenges.getSetting(Challenges.ANVIL_CRUSHER).settings
        val delaySection = settings["delay"]?.toSection()?.getValue()
        startDelay = delaySection?.get("startDelay")?.toInt()?.getValue() ?: 200
        amplifierDelay = delaySection?.get("amplifierDelay")?.toInt()?.getValue() ?: 5

        val densSection = settings["density"]?.toSection()?.getValue()
        startDens = densSection?.get("startDensity")?.toInt()?.getValue() ?: 5
        amplifierDens = densSection?.get("amplifierDensity")?.toInt()?.getValue() ?: 1

        radius = settings["radius"]?.toInt()?.getValue() ?: 5

        height = settings["height"]?.toInt()?.getValue() ?: 10
        scheduler()
    }

    private var delay = startDelay
    private var density = startDens
    private var paused = true
    private var stopped = false

    override fun register() {
        onDamage.register()
        onBlockConvert.register()
        paused = false
        onlinePlayers.forEach { p -> p.showBossBar(bar) }
    }

    override fun unregister() {
        onDamage.unregister()
        onBlockConvert.unregister()
        paused = true
    }

    override fun stop() {
        paused = true
        stopped = true
        onlinePlayers.forEach { p -> p.hideBossBar(bar) }
    }

    private val onDamage = listen<EntityDamageEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) return@listen
        val hearts = it.finalDamage / 2
        delay = (delay - (amplifierDelay * hearts)).toInt().coerceAtLeast(20)
        density = (density + (amplifierDens * hearts)).toInt().coerceAtMost(85)
    }

    private val onBlockConvert = listen<EntityChangeBlockEvent>(register = false) {
        if (it.to == Material.ANVIL || it.to == Material.DAMAGED_ANVIL || it.to == Material.CHIPPED_ANVIL) {
            it.isCancelled = true
            it.entity.remove()
            val loc = it.block.location
            it.block.getRelative(0, -1, 0).type = Material.AIR
            loc.getNearbyPlayers(0.5).forEach { p ->
                p.persistentDataContainer.set(DeathListener.key, PersistentDataType.STRING, "crushedAnvils")
                p.playSound(p, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
                p.damage(999.0)
            }
        }
    }

    private fun scheduler() {
        var countdown = startDelay
        task(false, 0, 1) {
            if (stopped) it.cancel()
            if (paused) return@task
            bar.name(cmp("$msgDelay \uD83D\uDD52 ") + cmp("${delay / 20.0}s", cHighlight) + cmp("   $msgDensity ☄ ") + cmp("$density%", cHighlight))
            if (countdown <= 0) {
                //Spawn
                val anvilLocations: HashSet<Location> = hashSetOf()
                onlinePlayers.forEach { p ->
                    p.showBossBar(bar)
                    if (Spectator.isSpectator(p.uniqueId)) return@forEach
                    val world = p.world
                    val loc = p.location.toBlockLocation()
                    val center = loc.add(0.5, height.toDouble(), 0.5)
                    val blocks = (calcCircle(center, radius).map { v -> v.toLocation(world) })

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

    private fun calcCircle(center: Location, radius: Int): Set<Location> {
        val circleBlocks = mutableSetOf<Location>()

        val centerX = center.x.toInt()
        val centerZ = center.z.toInt()
        val world = center.world
        for (x in centerX - radius..centerX + radius) {
            for (z in centerZ - radius..centerZ + radius) {
                if (distance(centerX, centerZ, x, z) <= radius) {
                    circleBlocks.add(Location(world, x + 0.5, center.y, z + 0.5))
                }
            }
        }

        return circleBlocks
    }

    fun distance(x1: Int, z1: Int, x2: Int, z2: Int): Double {
        return kotlin.math.sqrt((x2 - x1).toDouble().pow(2) + (z2 - z1).toDouble().pow(2))
    }
}
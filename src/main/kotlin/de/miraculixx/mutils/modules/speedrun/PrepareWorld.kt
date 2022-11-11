package de.miraculixx.mutils.modules.speedrun

import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.nio.file.Paths

class PrepareWorld {

    fun deleteWorld(world: World) {
        val teleport = worlds[0].spawnLocation
        world.players.forEach {
            it.teleport(teleport)
            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 1, true))
        }
        Bukkit.unloadWorld(world, false)
        val currentRelativePath = Paths.get(world.name)
        val s = currentRelativePath.toAbsolutePath().toString()
        consoleMessage("$prefix Deleting Folder at $s")
        File(s).deleteRecursively()
    }

    fun newWorld(c: FileConfiguration) {
        val village = Pair(c.getBoolean("Village Spawn"), c.getInt("Village Radius"))
        val portal = Pair(c.getBoolean("Portal Spawn"), c.getInt("Portal Radius"))
        val teleport = c.getBoolean("Village Teleport")
        var valid = false
        var world: World? = null
        task(true, 0, 20) {
            if (valid) {
                teleport(world, c)
                it.cancel()
                return@task
            }
            valid = true
            val wc = WorldCreator("speedrun")
            wc.type(WorldType.NORMAL)
            wc.environment(World.Environment.NORMAL)
            world = wc.createWorld()
            if (world == null) {
                valid = false
                return@task
            }
            val spawn = world!!.spawnLocation
            var structure = true
            if (village.first) {
                val loc = world!!.locateNearestStructure(spawn, StructureType.VILLAGE, village.second, false)
                if (teleport) world!!.spawnLocation = world!!.getHighestBlockAt(loc!!).location
                else if ((loc?.distance(spawn)?.toInt() ?: (village.second + 1)) > village.second)
                    structure = false
            }
            if (portal.first) {
                val loc = world!!.locateNearestStructure(spawn, StructureType.RUINED_PORTAL, portal.second, false)
                if ((loc?.distance(spawn)?.toInt() ?: (portal.second + 1)) > portal.second)
                    structure = false
            }
            if (!structure) {
                valid = false
                broadcast(msg("modules.speedrun.noStructure"))
                deleteWorld(world!!)
                return@task
            }
        }
    }

    private fun teleport(world: World?, c: FileConfiguration) {
        if (world == null) return
        onlinePlayers.forEach {
            it.teleport(world.spawnLocation)
            it.playSound(it.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            it.removePotionEffect(PotionEffectType.BLINDNESS)
        }
        broadcast(msg("modules.speedrun.start"))
        validWorld(world)
        if (c.getBoolean("Timer")) {
            taskRunLater(20 * c.getLong("Timer Delay")) {
                ModuleManager.setTimerStatus(true)
            }
        }
    }

    private fun validWorld(world: World) {
        val seed = world.seed
        val nether = WorldCreator("speedrun_nether")
        nether.environment(World.Environment.NETHER)
        nether.type(WorldType.NORMAL)
        nether.seed(seed)
        nether.createWorld()
        val end = WorldCreator("speedrun_the_end")
        end.environment(World.Environment.THE_END)
        end.type(WorldType.NORMAL)
        end.seed(seed)
        end.createWorld()
    }
}
package de.miraculixx.mutils.modules.challenge.mods.realistic

import de.miraculixx.mutils.utils.enums.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.challenges
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.World
import java.util.*

class TemperatureLogic(private val drinkLogic: DrinkLogic) {
    private val playerList: HashMap<UUID, Int> = HashMap()

    init {
        scheduler()
        event()
    }

    fun getPlayer(uuid: UUID): Int? {
        return playerList[uuid]
    }

    private fun scheduler() {
        task(true, 5, 60) {
            if (challenges != ChallengeStatus.RUNNING) return@task
            onlinePlayers.forEach { player ->
                val hasLeather = (player.inventory.getItem(39)?.type == Material.LEATHER_HELMET && player.inventory.getItem(38)?.type == Material.LEATHER_CHESTPLATE
                        && player.inventory.getItem(37)?.type == Material.LEATHER_LEGGINGS && player.inventory.getItem(36)?.type == Material.LEATHER_BOOTS)
                val loc = player.location
                var temp = playerList.getOrDefault(player.uniqueId, 50)
                var tempWorld = player.world.getTemperature(loc.blockX, loc.blockY, loc.blockZ)
                if (loc.world?.environment == World.Environment.THE_END) {
                    tempWorld = 0.0
                }
                //Block changes
                for (x in -2..2) {
                    for (y in -2..2) {
                        for (z in -2..2) {
                            val loc2 = loc.clone().add(x, y, z)
                            when (loc2.block.type) {
                                Material.LAVA, Material.MAGMA_BLOCK, Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER -> temp += 1
                                Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.SNOW -> temp -= 1
                                else -> continue
                            }
                        }
                    }
                }

                //Biome changes
                when (tempWorld) {
                    in -9.0..0.2 -> if (!hasLeather) temp -= 1
                    in 0.21..1.8 -> {
                        if (temp < 50) temp += 1
                        if (temp > 50) temp -= 1
                    }
                    in 1.81..9.0 -> {
                        temp += 1; if (!hasLeather) temp += 1
                    }
                }
                temp = temp.coerceIn(0..100)
                playerList[player.uniqueId] = temp
            }
        }
    }

    private fun event() {
        var counter = 0
        task(true, 1, 1) {
            if (challenges != ChallengeStatus.RUNNING) return@task
            onlinePlayers.forEach { player ->
                when (playerList[player.uniqueId]) {
                    in 85..100 -> {
                        counter++
                        if (player.location.world?.environment == World.Environment.NETHER) player.fireTicks = player.fireTicks
                        if (counter >= 40) drinkLogic.modify(player, -1)
                    }
                    in 0..15 -> {
                        if (player.freezeTicks > 119) player.freezeTicks = player.maxFreezeTicks
                        if (player.freezeTicks + 30 < 130) player.freezeTicks += 30
                    }
                }
            }
            if (counter >= 40) counter = 0
        }
    }
}
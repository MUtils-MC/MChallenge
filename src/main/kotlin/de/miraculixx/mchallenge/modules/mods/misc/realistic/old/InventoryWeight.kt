package de.miraculixx.mchallenge.modules.mods.misc.realistic.old

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*

class InventoryWeight {
    private val gewichtsMap: HashMap<UUID, Int> = HashMap()

    init {
        scheduler()
        events()
    }

    fun getWeight(uuid: UUID): Int? {
        return gewichtsMap[uuid]
    }

    private fun scheduler() {
        task(true, 0, 40) {
            onlinePlayers.forEach { player ->
                var weight = 0
                player.inventory.forEach { item ->
                    if (item != null) {
                        val name = item.type.name
                        val type = item.type
                        weight += when {
                            name.contains("GOLD") || name.contains("GOLDEN") || name.contains("CHAINMAIL") ||
                                    type == Material.IRON_CHESTPLATE || type == Material.IRON_BOOTS || type == Material.IRON_HELMET || type == Material.IRON_LEGGINGS ||
                                    name.contains("DIAMOND") || type == Material.CHEST || type == Material.BARREL || type == Material.ENCHANTING_TABLE ||
                                    name.contains("ANVIL") || type == Material.IRON_BLOCK || name.contains("_BUCKET") || name.contains("BOAT")
                                    || name.contains("MINECART") -> (5 * item.amount)

                            name.contains("LOG") || name.contains("STEM") || name.contains("_WOOD") || name.contains("HYPHAE")
                            -> (3 * item.amount)

                            else -> (1 * item.amount)
                        }
                    }
                }
                gewichtsMap[player.uniqueId] = weight
            }
        }
    }


    private fun events() {
        task(true, 5, 2) {
            onlinePlayers.forEach { player ->
                val weight = gewichtsMap.getOrDefault(player.uniqueId, 0)
                if (weight >= 120) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 5, 0, true, true, false))
                }
                if (weight >= 200) {
                    //Kein Schwimmen
                    if (player.location.block.type == Material.WATER) {
                        player.velocity = player.velocity.clone().add(Vector(0, -1, 0))
                    }
                    player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5, 0))
                }
                if (weight >= 250) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 5, 1, true))
                    player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5, 2, true))
                }
                if (weight >= 300) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 5, 199, true))
                }
            }
        }
    }
}
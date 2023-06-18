package de.miraculixx.mchallenge.modules.mods.realistic.old

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class DrinkLogic {
    private val playerList: HashMap<UUID, Int> = HashMap()

    fun getPlayer(uuid: UUID): Int? {
        return playerList[uuid]?.div(2)
    }

    fun rechner(player: Player) {
        var value = playerList[player.uniqueId]
        if (value == null) value = 20
        if (value <= 6) player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 20 * 5, 2))
        if (value <= 4) player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 5, 1))
        if (value <= 2) player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0))
        if (value <= 0) player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 20 * 5, 0))
    }

    fun modify(player: Player, amount: Int) {
        if (!playerList.containsKey(player.uniqueId)) { //No Data
            playerList[player.uniqueId] = 20
        }

        playerList[player.uniqueId] = playerList[player.uniqueId]!! + amount
        if (playerList[player.uniqueId]!! < 0) playerList[player.uniqueId] = 0
        if (playerList[player.uniqueId]!! > 20) playerList[player.uniqueId] = 20
    }
}
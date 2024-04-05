package de.miraculixx.mutils.modules.challenge.mods.ALPHA

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.utils.bossBars
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import kotlin.random.Random

class ChristmasObj {
    private val key = NamespacedKey(Main.INSTANCE, "CHRISTMAS-${Random.nextInt(11111, 99999)}")
    private val bar = Bukkit.createBossBar(key, "§cWait for Plugin", BarColor.YELLOW, BarStyle.SOLID)

    init {
        bossBars.add(key)
        bar.progress = 1.0
        bar.isVisible = true
    }

    fun changeBar(karma: Int) {
        val title = when (karma) {
            in 90..100 -> "§6§lWeihnachtliche Stimmung!"
            in 60..89 -> "§6Angespannte Weihnachten"
            in 21..59 -> "§eDas wars mit Geschenken..."
            in 1..20 -> "§cDu warst dieses Jahr nicht brav"
            0 -> "§c§lDu hast Weihnachten versaut!"
            else -> "§6§lWeihnachtliche Stimmung!"
        }
        bar.setTitle(title)
        broadcast(title)
    }

    fun addPlayer(player: Player) {
        bar.addPlayer(player)
    }

    fun newStatus(karma: Int, loc: Location) {
        val loc2 = loc.clone().add(0.0,0.7,0.0)
        val string = if (karma < 0) "§c$karma §7Karma" else "§a+ $karma §7Karma"
        val armorStand = loc.world!!.spawnEntity(loc, EntityType.ARMOR_STAND) as ArmorStand
        armorStand.isInvulnerable = true
        armorStand.isVisible = false
        armorStand.isSmall = true
        armorStand.isSilent = true
        armorStand.setGravity(false)
        armorStand.customName = string
        armorStand.isCustomNameVisible = true
        task(true, 1, 1, 30) {
            armorStand.teleport(loc2.add(0.0, 0.05, 0.0))
            if (it.counterUp == 30L) armorStand.remove()
        }
    }

}
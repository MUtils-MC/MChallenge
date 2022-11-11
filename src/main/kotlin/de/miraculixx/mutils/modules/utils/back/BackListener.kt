package de.miraculixx.mutils.modules.utils.back

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import org.bukkit.event.entity.PlayerDeathEvent

object BackListener {

    private val onDie = listen<PlayerDeathEvent> {
        val config = ConfigManager.getConfig(Configs.BACK)
        val uuid = it.player.uniqueId.toString()
        val location = it.player.location
        config.set("$uuid.Location", "${location.blockX} ${location.blockY} ${location.blockZ}")
        config.set("$uuid.World", location.world.name)
    }

}
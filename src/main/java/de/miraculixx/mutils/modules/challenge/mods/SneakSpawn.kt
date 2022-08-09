package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.challenge.utils.getLivingMobs
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack

class SneakSpawn : Challenge {
    override val challenge = Modules.SNEAK_SPAWN
    private var mobs: Boolean? = null
    private var livings: List<EntityType>? = null
    private var materials: List<Material>? = null

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        mobs = c.getBoolean("SNEAK_SPAWN.Mobs")

        //Create List of Living Entitys
        if (mobs == true) {
            val list = getLivingMobs(false)
            list.remove(EntityType.PLAYER)
            livings = list
        } else {
            val list = ArrayList<Material>()
            Material.values().forEach { material ->
                if (material.isItem) {
                    list.add(material)
                }
            }
            materials = list
        }

        return true
    }

    override fun stop() {
        mobs = null
        materials = null
        livings = null
    }

    override fun register() {
        onSneak.register()
    }

    override fun unregister() {
        onSneak.unregister()
    }

    private val onSneak = listen<PlayerToggleSneakEvent>(register = false) {
        if (it.isSneaking) return@listen
        val player = it.player
        val pos = player.location
        if (mobs == true) {
            val type = livings?.random() ?: return@listen
            pos.world.spawnEntity(pos, type)
        } else {
            val type = materials?.random() ?: return@listen
            val item = pos.world.dropItem(pos, ItemStack(type))
            item.pickupDelay = 30
            item.isGlowing = true
        }
    }
}
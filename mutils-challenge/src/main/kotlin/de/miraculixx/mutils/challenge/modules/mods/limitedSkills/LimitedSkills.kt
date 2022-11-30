package de.miraculixx.mutils.modules.challenge.mods.limitedSkills

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.items.customModel
import net.axay.kspigot.runnables.task
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerMoveEvent

class LimitedSkills : Challenge {
    override val challenge = Challenge.LIMITED_SKILLS
    private var p1: Player? = null
    private var p2: Player? = null

    override fun start(): Boolean {
        val config = ConfigManager.getConfig(Configs.MODULES)
        if (config.getBoolean("LIMITEDSKILLS.Random")) {
            p1 = null
            p2 = null
            val list = ArrayList<Player>()
            onlinePlayers.forEach {
                if (!Spectator.isSpectator(it.uniqueId)) list.add(it)
            }
            if (list.size < 2) {
                broadcast(msg("module.challenge.error"))
                return false
            } else if (list.size > 2)
                broadcast(msg("module.challenge.notRecommend"))
            list.shuffle()
            p1 = list[0]
            p2 = list[1]
        } else {
            onlinePlayers.forEach {
                if (it.scoreboardTags.contains("LS_1")) p1 = it
                if (it.scoreboardTags.contains("LS_2")) p2 = it
            }
            if (p1 == p2 || p1 == null || p2 == null) {
                broadcast(msg("module.challenge.error"))
                return false
            }
        }
        //Player 2 can see nothing
        //Player 1 can damage nothing
        p2?.setResourcePack("https://www.dropbox.com/s/83lqeu9x5y1wgdn/NoEntitys.zip?dl=1")
        p2?.sendTitle("§6LOADING RESOURCEPACK", msg("modules.texturepack.loading", pre = false), 5, 30, 5)

        p1?.sendMessage(msg("module.challenge.limitedskills.player1"))
        p2?.sendMessage(msg("module.challenge.limitedskills.player2"))
        p2?.hidePlayer(Main.INSTANCE, p1?:return false)
        return true
    }

    override fun stop() {
        p2?.setResourcePack("https://www.dropbox.com/s/me1buxg3vy7ddc9/NoTextures.zip?dl=1")
        p2?.showPlayer(Main.INSTANCE, p1?:return)
    }

    override fun register() {
        onDamage.register()
        onTrade.register()
        onMove.register()
        onMove.register()
        onCollect.register()
    }
    override fun unregister() {
        onDamage.unregister()
        onTrade.unregister()
        onMove.unregister()
        onMove.unregister()
        onCollect.unregister()
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        if (p1 == it.damager) it.isCancelled = true
    }

    private val onTrade = listen<PlayerInteractAtEntityEvent>(register = false) {
        if (p1 == it.player) {
            it.isCancelled = true
            it.player.closeInventory()
            it.player.leaveVehicle()
            task(true, 0, 1, 3) { _ ->
                it.player.closeInventory()
                it.player.leaveVehicle()
            }
        }
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (it.from.block != it.to?.block) {
            it.player.getNearbyEntities(25.0, 25.0, 25.0).forEach { entity ->
                if (entity is LivingEntity && entity.type != EntityType.PLAYER) {
                    if (entity.equipment != null && entity.equipment?.itemInMainHand != null) {
                        entity.equipment?.itemInMainHand?.itemMeta?.customModel = 787
                    }
                    if (entity.equipment != null && entity.equipment?.itemInOffHand != null) {
                        entity.equipment?.itemInOffHand?.itemMeta?.customModel = 787
                    }
                    if (entity.equipment != null && entity.equipment?.boots != null) {
                        entity.equipment!!.boots = null
                    }
                    if (entity.equipment != null && entity.equipment?.leggings != null) {
                        entity.equipment!!.leggings = null
                    }
                    if (entity.equipment != null && entity.equipment?.chestplate != null) {
                        entity.equipment!!.chestplate = null
                    }
                    if (entity.equipment != null && entity.equipment?.helmet != null) {
                        entity.equipment!!.helmet = null
                    }
                }
            }
        }
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.entity !is Player) return@listen
        val item = it.item.itemStack
        if (item.hasItemMeta() && item.itemMeta != null) {
            if (item.itemMeta!!.hasCustomModelData() && item.itemMeta!!.customModelData != 0) {
                it.isCancelled = true
                it.item.remove()
                item.itemMeta?.customModel = 0
                (it.entity as Player).inventory.addItem(item)
            }
        }
    }
}
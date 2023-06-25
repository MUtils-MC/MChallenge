package de.miraculixx.mchallenge.modules.mods.multiplayer.limitedSkills

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.mchallenge.utils.gui.GUITypes
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.utils.gui.buildInventory
import de.miraculixx.mvanilla.messages.cHighlight
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.emptyComponent
import de.miraculixx.mvanilla.messages.msgString
import io.papermc.paper.event.player.PlayerArmSwingEvent
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.util.*

class LimitedSkills : Challenge {
    val selection: MutableMap<UUID, Boolean> = mutableMapOf() //True -> See
    private val randomRoles: Boolean

    init {
        val settings = challenges.getSetting(Challenges.LIMITED_SKILLS).settings
        randomRoles = settings["random"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        if (randomRoles) {
            onlinePlayers.shuffled().forEachIndexed { index, player ->
                selection[player.uniqueId] = index % 2 == 1
            }
            startGame()
        } else {
            onlinePlayers.forEach {
                GUITypes.CH_LIMITED_SKILLS.buildInventory(it, "LIMITED_SKILLS", LimitSkillsItems(this), LimitGUI(this))
            }
        }
        return true
    }

    override fun stop() {
        val playerNoSee = selection.filter { !it.value }.mapNotNull { Bukkit.getPlayer(it.key) }
        playerNoSee.forEach { player ->
            player.world.entities.forEach { e -> if (e != player) player.showEntity(PluginManager, e) }
        }
    }

    override fun register() {
        onDamage.register()
        onInteract.register()
        onMove.register()
        onMove.register()
        onCollect.register()
        onSpawn.register()
        onHit.register()
    }

    override fun unregister() {
        onDamage.unregister()
        onInteract.unregister()
        onMove.unregister()
        onMove.unregister()
        onCollect.unregister()
        onSpawn.unregister()
        onHit.unregister()
    }

    fun startGame() {
        val playerSee = selection.filter { it.value }.mapNotNull { Bukkit.getPlayer(it.key) }
        val playerNoSee = selection.filter { !it.value }.mapNotNull { Bukkit.getPlayer(it.key) }
        playerNoSee.forEach { player ->
            player.closeInventory()
            val times = Duration.ofSeconds(3)
            player.showTitle(Title.title(cmp(msgString("items.event.LIMITED_DAMAGE.n"), cHighlight), emptyComponent(), Title.Times.times(Duration.ofMillis(500), times, times)))
            player.world.entities.forEach { e -> player.hideEntity(PluginManager, e) }
        }
        playerSee.forEach { player ->
            player.closeInventory()
            val times = Duration.ofSeconds(3)
            player.showTitle(Title.title(cmp(msgString("items.event.LIMITED_SEE.n"), cHighlight), emptyComponent(), Title.Times.times(Duration.ofMillis(500), times, times)))
        }
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        if (selection[it.damager.uniqueId] == true) it.isCancelled = true
    }

    private val onInteract = listen<PlayerInteractAtEntityEvent>(register = false) {
        val player = it.player
        if (selection[player.uniqueId] == true) {
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
        if (it.from.block != it.to.block) {
            it.player.getNearbyEntities(25.0, 25.0, 25.0).forEach { entity ->
                if (entity is LivingEntity && entity.type != EntityType.PLAYER) {
                    val equipment = entity.equipment ?: return@forEach
                    if (equipment.itemInMainHand.itemMeta?.customModel != 787) {
                        equipment.itemInMainHand.editMeta { meta -> meta.customModel = 787 }
                    }
                    if (equipment.itemInOffHand.itemMeta?.customModel != 787) {
                        equipment.itemInOffHand.editMeta { meta -> meta.customModel = 787 }
                    }
                    if (equipment.boots != null) equipment.boots = null
                    if (equipment.leggings != null) equipment.leggings = null
                    if (equipment.chestplate != null) equipment.chestplate = null
                    if (equipment.helmet != null) equipment.helmet = null
                }
            }
        }
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.entity !is Player) return@listen
        val item = it.item.itemStack
        if (item.hasItemMeta() && item.itemMeta != null) {
            if (item.itemMeta?.customModel != 0) {
                it.isCancelled = true
                it.item.remove()
                item.itemMeta?.customModel = 0
                (it.entity as Player).inventory.addItem(item)
            }
        }
    }

    private val onSpawn = listen<EntitySpawnEvent>(register = false) {
        selection.forEach { (uuid, state) ->
            if (!state) Bukkit.getPlayer(uuid)?.hideEntity(PluginManager, it.entity)
        }
    }

    private val onHit = listen<PlayerArmSwingEvent>(register = false) {
        val player = it.player
        if (it.animationType != PlayerAnimationType.ARM_SWING) return@listen
        val target = player.getTargetEntity(3, false) ?: return@listen
        if (target !is LivingEntity) {
            val drop = when (target) {
                is Boat -> target.boatMaterial
                is Minecart -> target.minecartMaterial
                else -> return@listen
            }
            target.world.dropItem(target.location, ItemStack(drop))
            return@listen
        }
        player.attack(target)
    }
}
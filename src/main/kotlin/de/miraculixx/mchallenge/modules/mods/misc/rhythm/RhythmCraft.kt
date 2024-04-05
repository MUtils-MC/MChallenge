package de.miraculixx.mchallenge.modules.mods.misc.rhythm

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity.*
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.util.Vector
import org.joml.Vector2f
import java.util.*
import kotlin.math.max

class RhythmCraft : Challenge {
    private val inputCooldown = mutableSetOf<UUID>()
    private val tickingEntities = mutableMapOf<World, MutableMap<UUID, RLivingEntity>>()
    private val players = mutableMapOf<UUID, RPlayer>()
    private val speed = 10
    private val bar = RhythmBar(speed)

    // Static values
    private val alwaysEdible = setOf(Material.CHORUS_FRUIT, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.HONEY_BOTTLE)
    private val isArrow = setOf(Material.ARROW, Material.SPECTRAL_ARROW, Material.TIPPED_ARROW)
    private val isShootable = isArrow + setOf(Material.FIREWORK_ROCKET)

    override fun start(): Boolean {
        worlds.forEach { world ->
            tickingEntities[world] = buildMap { world.livingEntities.forEach { e -> put(e.uniqueId, entityToRhythm(e)) } }.toMutableMap()
        }
        tickingEntities.forEach { (_, entities) -> entities.forEach { (_, e) -> e.onSpawn() } }
        onlinePlayers.forEach { p ->
            players[p.uniqueId] = RPlayer(p)
            bar.show(p)
        }
        return true
    }

    override fun stop() {
        tickingEntities.forEach { (_, entities) ->
            entities.forEach { (_, e) -> e.stop() }
        }
        tickingEntities.clear()
        players.clear()
        onlinePlayers.forEach { p -> bar.hide(p) }
    }

    override fun register() {
        players.forEach { (_, player) -> player.onSpawn() }
        onMove.register()
        onJoin.register()
        onSpawn.register()
        onBlockBreak.register()
        onBlockPlace.register()
        onShoot.register()
        onInteract.register()
        onHit.register()
    }

    override fun unregister() {
        players.forEach { (_, player) -> player.onSpawn() }
        onMove.unregister()
        onJoin.unregister()
        onSpawn.unregister()
        onBlockBreak.unregister()
        onBlockPlace.unregister()
        onShoot.unregister()
        onInteract.unregister()
        onHit.unregister()
    }

    private fun entityToRhythm(entity: LivingEntity): RLivingEntity {
        return when (entity) {
            is Animals -> RAnimal(entity)
            is Mob -> RMelee(entity)
            else -> RUnknown(entity)
        }
    }

    private fun tick() {
        bar.press()
        worlds.forEach { world ->
            // Don't tick empty worlds - waste
            if (world.playerCount == 0) return@forEach

            // Tick all living entities except players
            val map = tickingEntities.getOrPut(world) { mutableMapOf() }
            val removal = mutableSetOf<UUID>()
            map.forEach { (id, e) -> if (e.checkDead()) removal.add(id) else e.tick() }
            removal.forEach { id -> map.remove(id) }
        }
    }


    //
    // Player interaction events
    //

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (!it.hasChangedPosition()) return@listen
        val player = it.player
        if (player.gameMode == GameMode.SPECTATOR || player.gameMode == GameMode.CREATIVE) return@listen
        val uuid = player.uniqueId
        val from = it.from
        val to = it.to
        player.velocity = Vector()
        if (inputCooldown.contains(uuid)) return@listen

        // Check which direction was pressed
        val lNorth = from.z - to.z // -z
        val lSouth = to.z - from.z // +z
        val lWest = from.x - to.x  // -x
        val lEast = to.x - from.x  // +x
        val direction = if (max(lNorth, lSouth) > max(lWest, lEast)) {
            if (lNorth > lSouth) BlockFace.NORTH else BlockFace.SOUTH
        } else {
            if (lWest > lEast) BlockFace.WEST else BlockFace.EAST
        }

        inputCooldown.add(uuid)
        taskRunLater(5, false) { inputCooldown.remove(uuid) }

        val rhythmPlayer = players[uuid]
        if (rhythmPlayer == null) {
            player.sendMessage(prefix + cmp("You are not registered for this challenge! Please rejoin to participate."))
            it.isCancelled = true
            return@listen
        }
        val isSuccessful = rhythmPlayer.moveDirection(direction, Vector2f(to.yaw, to.pitch), true)
        if (!isSuccessful) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.5f, 0.5f)
        }

        // Tick other entities AFTER all player moved or the beat ends
        // TODO Multiplayer support
        tick()
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {

        if (it.action.isLeftClick) {
            // TODO: Check if whiff or block break
            return@listen
        }

        val item = it.item
        if (item == null) return@listen // Maybe other interactions later
        val player = it.player
        val material = item.type

        when {
            material.isEdible -> {
                if (player.foodLevel < 20 || alwaysEdible.contains(material)) {
                    // Eat current item
                    (item as CraftItemStack).handle.finishUsingItem((it.player.world as CraftWorld).handle, (player as CraftPlayer).handle)
                }
            }

            material == Material.CROSSBOW -> {
                val meta = item.itemMeta as CrossbowMeta
                if (meta.chargedProjectiles.isNotEmpty()) { /* Shoot crossbow */
                } else {
                    // Load crossbow
                    val arrow = player.inventory.itemInOffHand.takeIf { i -> isShootable.contains(i.type) } ?: player.inventory.find { i -> i != null && isShootable.contains(i.type) }
                    if (arrow != null) {
                        it.isCancelled = true
                        val arrowCopy = arrow.clone().apply { amount = 1 }
                        item.editMeta(CrossbowMeta::class.java) { m ->
                            m.addChargedProjectile(arrowCopy)
                        }
                        arrow.amount--
                    }
                }
            }

            else -> return@listen
        }

        tick()
    }

    private val onBlockPlace = listen<BlockPlaceEvent>(register = false) {
        tick()
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        tick() // TODO: Check if block is breakable
    }

    private val onShoot = listen<EntityShootBowEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) {
            // Manage skeleton shooting
        } else {
            val projectile = it.projectile
            val vector = projectile.velocity
            val force = it.force
            val proportionalityFactor = 1 / force
            projectile.velocity = Vector(vector.x * proportionalityFactor, vector.y * proportionalityFactor, vector.z * proportionalityFactor)
        }
    }

    private val onHit = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        val entity = it.entity as? LivingEntity ?: return@listen
        val rhythmEntity = tickingEntities[entity.world]?.get(entity.uniqueId) ?: return@listen
        if (rhythmEntity is RMelee) {
            rhythmEntity.isStunned = true
        }
        tick()
    }


    //
    // Entity management events
    //

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val player = it.player
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return@listen
        players[player.uniqueId] = RPlayer(player)
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        val entity = it.entity
        val rhythmEntity = entityToRhythm(entity)
        val world = entity.world
        tickingEntities.getOrPut(world) { mutableMapOf() }[entity.uniqueId] = rhythmEntity
        rhythmEntity.onSpawn()
    }
}
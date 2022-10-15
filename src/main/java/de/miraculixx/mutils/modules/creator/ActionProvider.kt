package de.miraculixx.mutils.modules.creator

import de.miraculixx.mutils.modules.challenge.utils.getLivingMobs
import de.miraculixx.mutils.modules.creator.data.ActionData
import de.miraculixx.mutils.modules.creator.data.CustomChallengeListener
import de.miraculixx.mutils.modules.creator.enums.*
import de.miraculixx.mutils.modules.creator.events.*
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.tools.enumOf
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

object ActionProvider {

    // General Builder
    fun buildListener(event: de.miraculixx.mutils.modules.creator.data.Event): CustomChallengeListener<*> {
        val actionsData = event.data.actions
        val actions = getActions(actionsData)
        return when (event.event) {
            CreatorEvent.MOVE_GENERAL -> MoveGeneral(actions)
            CreatorEvent.MOVE_BLOCK -> MoveBlock(actions)
            CreatorEvent.MOVE_CAMERA -> MoveCamera(actions)
            CreatorEvent.MOVE_JUMP -> MoveJump(actions)
            CreatorEvent.DIMENSION_SWAP -> DimensionSwap(actions)
            CreatorEvent.MOB_DEATH -> MobDeath(actions)
            CreatorEvent.MOB_KILL -> MobKill(actions)
            CreatorEvent.MOB_DAMAGE -> MobDamage(actions)
            CreatorEvent.BLOCK_BREAK -> BlockBreak(actions)
            CreatorEvent.BLOCK_PLACE -> BlockPlace(actions)
            CreatorEvent.CRAFT_ITEM -> ItemCraft(actions)
            CreatorEvent.MOB_DAMAGE_BY_PLAYER -> MobDamageByPlayer(actions)
            CreatorEvent.COLLECT_ITEM -> CollectItem(actions)
            CreatorEvent.DROP_ITEM -> DropItem(actions)
            CreatorEvent.CONSUME_ITEM -> ConsumeItem(actions)
            CreatorEvent.PLAYER_HUNGER -> PlayerHunger(actions)
            CreatorEvent.PLAYER_DAMAGE -> PlayerDamage(actions)
            CreatorEvent.OPEN_CONTAINER -> OpenContainer(actions)
        }
    }

    // Action Builder
    private fun getActions(actions: Map<UUID, ActionData>): List<(Event) -> Unit> {
        return buildList {
            actions.forEach { (_, actionData) ->
                when (actionData.action.type) {
                    EventType.PLAYER_EVENT -> add(getAction(actionData.action, actionData.settings))
                    EventType.ENTITY_EVENT -> add(getAction(actionData.action, actionData.settings))
                    EventType.CANCELABLE -> add(getAction(actionData.action, actionData.settings))
                    EventType.GENERAL -> add(getAction(actionData.action, actionData.settings))

                    EventType.NO_FILTER -> {}
                }
            }
        }
    }

    private fun getAction(action: CreatorAction, options: List<String>): (Event) -> Unit {
        val types = buildMap {
            action.inputType.forEachIndexed { index, actionValueData ->
                put(actionValueData.type, options[index])
            }
        }
        val selectorType = enumOf<CreatorActionSelector>(types[CreatorActionInput.SELECTOR] ?: "")
        return when (action) {
            // General Events
            CreatorAction.GIVE_ITEM_PLAYER -> { it: Event ->
                val targets = getTargets(it, selectorType, true)
                val item = ItemStack(getMaterial(options[1]), options[2].toIntOrNull() ?: 1)
                targets.forEach { player ->
                    (player as? Player)?.inventory?.addItem(item)
                }
            }

            CreatorAction.DAMAGE_ENTITY -> { it: Event ->
                val targets = getTargets(it, selectorType, false)
                val dmg = options.firstOrNull()?.toDoubleOrNull() ?: 1.0
                targets.forEach { target -> target.damage(dmg) }
            }

            CreatorAction.CHANGE_TIME_WORLD -> { _: Event ->
                val ticks = options.firstOrNull()?.toLongOrNull() ?: 0
                worlds.forEach { world ->
                    world.time += ticks
                }
            }

            CreatorAction.CHANGE_TIME_PLAYER -> { it: Event ->
                val targets = getTargets(it, selectorType, true)
                val ticks = options[1].toLongOrNull() ?: 0
                targets.forEach { player ->
                    (player as? Player)?.setPlayerTime(ticks, false)
                }
            }

            CreatorAction.PLAY_SOUND -> { it: Event ->
                val targets = getTargets(it, selectorType, true)
                val sound = getSound(options[1])
                targets.forEach { player ->
                    (player as? Player)?.playSound(player, sound, 1f, 1f)
                }
            }

            CreatorAction.SEND_MESSAGE -> { it: Event ->
                val targets = getTargets(it, selectorType, true)
                val message = mm.deserialize(options[1])
                targets.forEach { player ->
                    (player as? Player)?.sendMessage(message)
                }
            }

            CreatorAction.TELEPORT_RADIUS -> { it: Event ->
                val targets = getTargets(it, selectorType, false)
                val radius = options[1].toIntOrNull() ?: 0
                val negativeRadius = radius - radius * 2
                targets.forEach { target ->
                    val currentLoc = target.location
                    val newLoc = currentLoc.add(Vector((negativeRadius..radius).random(), (negativeRadius..radius).random(), (negativeRadius..radius).random()))
                    target.teleportAsync(target.world.getHighestBlockAt(newLoc).location.add(.0,1.0,.0))
                }
            }

            CreatorAction.SPAWN_MOB -> listener@{ it: Event ->
                val position = getLocation(it) ?: return@listener
                val mob = getEntityType(options[0])
                val amount = options[1].toIntOrNull() ?: 1
                repeat(amount) {
                    position.world.spawnEntity(position, mob, true)
                }
            }

            CreatorAction.INVENTORY_CLEAR -> { it: Event ->
                val targets = getTargets(it, selectorType, true)
                targets.forEach { target ->
                    (target as? Player)?.inventory?.clear()
                }
            }

            // Cancelable
            CreatorAction.CANCEL -> listener@{ it: Event ->
                if (it !is Cancellable) {
                    warn(WarnTypes.NOT_CANCELABLE)
                    return@listener
                }
                it.isCancelled = true
            }
        }
    }

    private fun getMaterial(material: String): Material {
        return when {
            material == "#random-everytime" -> Material.values().filter { it.isItem && it.creativeCategory != null }.random()
            material.startsWith("#random-everytime-filter") -> {
                val filter = material.split(':', limit = 2)[1]
                Material.values().filter { it.isItem && it.creativeCategory != null && it.name.contains(filter.replace(' ', '_'), true) }.randomOrNull() ?: Material.AIR
            }

            else -> enumOf<Material>(material) ?: Material.AIR
        }
    }

    private fun getSound(sound: String): Sound {
        return when {
            sound == "#random-everytime" -> Sound.values().random()
            sound.startsWith("#random-everytime-filter") -> {
                val filter = sound.split(':', limit = 2)[1]
                Sound.values().filter { it.name.contains(filter.replace(' ', '_'), true) }.randomOrNull() ?: Sound.BLOCK_STONE_HIT
            }

            else -> enumOf<Sound>(sound) ?: Sound.BLOCK_STONE_HIT
        }
    }

    private fun getEntityType(mob: String): EntityType {
        return when {
            mob == "#random-everytime" -> getLivingMobs(false).random()
            mob.startsWith("#random-everytime-filter") -> {
                val filter = mob.split(':', limit = 2)[1]
                getLivingMobs(false).filter { it.name.contains(filter.replace(' ', '_'), true) }.randomOrNull() ?: EntityType.MARKER
            }

            else -> enumOf<EntityType>(mob) ?: EntityType.MARKER
        }
    }

    private fun getTargets(it: Event, selector: CreatorActionSelector?, onlyPlayer: Boolean): List<LivingEntity> {
        return when (selector) {
            CreatorActionSelector.SOURCE_PLAYER -> {
                when (it) {
                    is EntityDamageByEntityEvent -> if (it.damager is Player) return listOf(it.damager as Player) else warn(WarnTypes.NO_SOURCE_PLAYER)
                    is InventoryInteractEvent -> return listOf(it.whoClicked as Player)
                    is BlockPlaceEvent -> return listOf(it.player)
                    is BlockBreakEvent -> return listOf(it.player)

                    is PlayerEvent -> return listOf(it.player)
                    is EntityEvent -> if (it.entity is Player) return listOf(it.entity as Player) else warn(WarnTypes.NO_SOURCE_PLAYER)
                    else -> warn(WarnTypes.NO_SOURCE_PLAYER)
                }
                return emptyList()
            }

            CreatorActionSelector.EVERY_PLAYER -> onlinePlayers.toList()
            CreatorActionSelector.RANDOM_PLAYER -> listOf(onlinePlayers.random())

            CreatorActionSelector.ONLY_MOBS -> {
                if (onlyPlayer) {
                    warn(WarnTypes.NO_ENTITIES_ALLOWED)
                    emptyList()
                } else buildList { worlds.forEach { world -> addAll(world.livingEntities.filter { it !is Player }) } }
            }

            CreatorActionSelector.MOBS_AND_PLAYERS -> {
                buildList { worlds.forEach { world -> addAll(world.livingEntities.filter { !onlyPlayer || it is Player }) } }
            }

            else -> {
                warn(WarnTypes.NO_VALUE)
                emptyList()
            }
        }
    }

    private fun getLocation(it: Event): Location? {
        return when (it) {
            is BlockEvent -> it.block.location
            is EntityEvent -> it.entity.location
            is PlayerEvent -> it.player.location

            else -> null
        }
    }

    private fun warn(warnTypes: WarnTypes) {
        val message = "§cCustom Challenge tries to " + when (warnTypes) {
            WarnTypes.NO_SOURCE_PLAYER -> "receive Source-Player without being an Player-Event!"
            WarnTypes.NO_ENTITIES_ALLOWED -> "receive only Mobs without being allowed in this action!"
            WarnTypes.NOT_CANCELABLE -> "cancel a not cancelable event!"
            WarnTypes.NO_VALUE -> "receive a Target without any Selector!"
        }
        broadcast("$prefix $message")
    }

    enum class WarnTypes {
        NO_SOURCE_PLAYER, NO_ENTITIES_ALLOWED, NOT_CANCELABLE, NO_VALUE
    }
}
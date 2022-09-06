package de.miraculixx.mutils.modules.creator

import de.miraculixx.mutils.modules.creator.data.ActionData
import de.miraculixx.mutils.modules.creator.enums.*
import de.miraculixx.mutils.modules.creator.events.*
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleWarn
import de.miraculixx.mutils.utils.tools.enumOf
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack
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
                val item = ItemStack(enumValueOf(options[1]), options[2].toIntOrNull() ?: 1)
                targets.forEach { player ->
                    (player as? Player)?.inventory?.addItem(item)
                }
            }
            CreatorAction.DAMAGE_ENTITY -> { it: Event ->
                val targets = getTargets(it, selectorType, false)
                val dmg = options.firstOrNull()?.toDoubleOrNull() ?: 1.0
                targets.forEach { target -> target.damage(dmg) }
                broadcast("Damage $dmg")
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
                val sound = enumValueOf<Sound>(options[1])
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

    private fun getTargets(it: Event, selector: CreatorActionSelector?, onlyPlayer: Boolean): List<LivingEntity> {
        return when (selector) {
            CreatorActionSelector.SOURCE_PLAYER -> {
                if (it !is PlayerEvent) {
                    warn(WarnTypes.NO_SOURCE_PLAYER)
                    emptyList()
                } else listOf(it.player)
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

    private fun warn(warnTypes: WarnTypes) {
        val message = "Custom Challenge tries to " + when (warnTypes) {
            WarnTypes.NO_SOURCE_PLAYER -> "receive Source-Player without being an Player-Event!"
            WarnTypes.NO_ENTITIES_ALLOWED -> "receive only Mobs without being allowed in this action!"
            WarnTypes.NOT_CANCELABLE -> "cancel a not cancelable event!"
            WarnTypes.NO_VALUE -> "receive a Target without any Selector!"
        }
        consoleWarn("$prefix $message")
    }

    enum class WarnTypes {
        NO_SOURCE_PLAYER, NO_ENTITIES_ALLOWED, NOT_CANCELABLE, NO_VALUE
    }
}
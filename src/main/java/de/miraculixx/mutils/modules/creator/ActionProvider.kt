package de.miraculixx.mutils.modules.creator

import de.miraculixx.mutils.modules.creator.data.Event
import de.miraculixx.mutils.modules.creator.enums.CreatorAction
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.creator.enums.EventType
import de.miraculixx.mutils.modules.creator.events.*
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

object ActionProvider {

    // General Builder
    fun buildListener(event: Event): CustomChallengeListener<*> {
        val actions = event.data.actions
        return when (event.event) {
            CreatorEvent.MOVE_GENERAL -> MoveGeneral(getActions(actions))
            CreatorEvent.MOVE_BLOCK -> MoveBlock(getActions(actions))
            CreatorEvent.MOVE_CAMERA -> MoveCamera(getActions(actions))
            CreatorEvent.MOVE_JUMP -> MoveJump(getActions(actions))
            CreatorEvent.DIMENSION_SWAP -> DimensionSwap(getActions(actions))
        }
    }


    // Action Builder
    private fun <T> getActions(actions: Map<CreatorAction, List<String>>): List<T> {
        return buildList {
            actions.forEach { (action, options) ->
                when (action.type) {
                    EventType.PLAYER_EVENT -> getAction<PlayerEvent>(action, options)
                    EventType.ENTITY_EVENT -> getAction<EntityEvent>(action, options)
                    EventType.CANCELABLE -> getAction<Cancellable>(action, options)
                }
            }
        }
    }

    private fun <T> getAction(action: CreatorAction, options: List<String>): (T) -> Unit {
        return when (action) {
            // Player Events
            CreatorAction.GIVE_ITEM_TARGET_PLAYER -> listener@{ it: T ->
                if (it !is PlayerEvent) return@listener
                val player = it.player
                val inv = player.inventory
                options.forEach { strg ->
                    val split = strg.split(':')
                    val mat = Material.valueOf(split[0])
                    inv.addItem(ItemStack(mat, split[1].toIntOrNull() ?: 1))
                }
            }

            CreatorAction.DAMAGE_TARGET_PLAYER -> listener@{ it: T ->
                if (it !is PlayerEvent) return@listener
                val player = it.player
                val dmg = options.firstOrNull()?.toDoubleOrNull() ?: 1.0
                player.damage(dmg)
            }

            CreatorAction.DAMAGE_ALL_PLAYERS -> listener@{ it: T ->
                if (it !is PlayerEvent) return@listener
                val dmg = options.firstOrNull()?.toDoubleOrNull() ?: 1.0
                onlinePlayers.forEach { player ->
                    player.damage(dmg)
                }
            }

            // Cancelable
            CreatorAction.CANCEL -> listener@{ it: T ->
                if (it !is Cancellable) return@listener
                it.isCancelled = true
            }
        }
    }
}
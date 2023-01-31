package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.globalRules
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.InventoryUtils.get
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsMenu
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUIGameRules(world: World?) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta

        if ((meta.customModel ?: 0) == 0) {
            GUITypes.WORLD_MENU.buildInventory(player, "WORLD_MENU", ItemsMenu(), GUIMenu())
            player.click()
            return@event
        } else if (meta.customModel != 1) return@event
        val ruleKey = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.gamerules.key")) ?: return@event
        val gameRule = GameRule.getByName(ruleKey) ?: return@event
        val isGlobal = world == null
        val click = it.click
        val rule = if (isGlobal) globalRules[gameRule.name] else world?.getGameRuleValue(gameRule)

        if (click == ClickType.SWAP_OFFHAND) {
            if (rule == null) {
                player.soundEnable()
                globalRules[gameRule.name] = worlds[0].getGameRuleDefault(gameRule) ?: 0
            } else {
                player.soundDisable()
                globalRules.remove(gameRule.name)
            }
        } else {
            when (rule) {
                is Boolean -> {
                    if (rule) {
                        player.soundDisable()
                        gameRule.changeValue(false, isGlobal, player, world)
                    } else {
                        player.soundEnable()
                        gameRule.changeValue(true, isGlobal, player, world)
                    }
                }

                is Int -> {
                    val isShift = click.isShiftClick
                    when (click) {
                        ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                            if (rule <= 0) {
                                player.soundError()
                                return@event
                            }
                            val newValue = (rule - if (isShift) 50 else 1).coerceAtLeast(0)
                            gameRule.changeValue(newValue, isGlobal, player, world)
                            player.soundDown()
                        }

                        ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                            val newValue = rule + if (isShift) 50 else 1
                            gameRule.changeValue(newValue, isGlobal, player, world)
                            player.soundUp()
                        }

                        else -> Unit
                    }
                }

                null -> {
                    player.soundError()
                    player.sendMessage(prefix + msg("event.noOverride"))
                }

                else -> return@event
            }
        }
        inv.update()
    }

    private fun <T : Any> GameRule<*>.changeValue(value: T, isGlobal: Boolean, player: Player, world: World?): Boolean {
        return if (isGlobal) {
            globalRules[name] = value
            true
        } else {
            /**
             * Must be cast to [Boolean] or [Int] for valid gamerules
             */
            val success = (this as? GameRule<T>)?.let { v -> world?.setGameRule(v, value) } != null
            if (!success) player.soundError()
            success
        }
    }
}
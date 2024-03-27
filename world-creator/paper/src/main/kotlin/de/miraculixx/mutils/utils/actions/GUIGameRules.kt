package de.miraculixx.mutils.utils.actions

import de.miraculixx.challenge.api.data.CustomGameRule
import de.miraculixx.challenge.api.data.MergedGameRule
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mutils.globalRules
import de.miraculixx.mutils.module.CustomGameRuleListener
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.namespace
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsMenu
import de.miraculixx.mvanilla.extensions.*
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

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
        val ruleKey = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.gamerules.key"), PersistentDataType.STRING) ?: return@event
        val gameRule = GameRule.getByName(ruleKey)
        val customGameRule = enumOf<CustomGameRule>(ruleKey)
        val mergedGameRule = MergedGameRule(
            gameRule?.name ?: customGameRule?.name ?: return@event,
            gameRule ?: customGameRule ?: return@event,
            gameRule?.translationKey() ?: customGameRule?.key ?: return@event
        )

        val isGlobal = world == null
        val click = it.click
        val rule = if (world == null) {
            globalRules[mergedGameRule.name]
        } else {
            if (gameRule != null) world.getGameRuleValue(gameRule)
            else WorldManager.getWorldData(world.uid)?.customGameRules?.get(customGameRule)
        }

        if (click == ClickType.SWAP_OFFHAND) {
//            if (rule == null) {
//                player.soundEnable()
//                globalRules[gameRule.name] = worlds[0].getGameRuleDefault(gameRule) ?: 0
//            } else {
//                player.soundDisable()
//                globalRules.remove(gameRule.name)
//            }
        } else {
            when (rule) {
                is Boolean -> {
                    if (rule) {
                        player.soundDisable()
                        mergedGameRule.changeValue(false, player, world)
                    } else {
                        player.soundEnable()
                        mergedGameRule.changeValue(true, player, world)
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
                            mergedGameRule.changeValue(newValue, player, world)
                            player.soundDown()
                        }

                        ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                            val newValue = rule + if (isShift) 50 else 1
                            mergedGameRule.changeValue(newValue, player, world)
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

    private fun <T : Any> MergedGameRule.changeValue(value: T, player: Player, world: World?): Boolean {
        return if (world == null) {
            globalRules[name] = value
            true
        } else {
            /**
             * Must be cast to [Boolean] or [Int] for valid gamerules
             */
            val success = if (sourceEnum is CustomGameRule) {
                when (sourceEnum) {
                    CustomGameRule.BLOCK_UPDATES -> {
                        val bool = value as? Boolean ?: false
                        if (bool) CustomGameRuleListener.blockedPhysics.remove(world.uid)
                        else CustomGameRuleListener.blockedPhysics.add(world.uid)
                    }
                }
                true
            } else {
                val rule = GameRule.getByName(name)
                (rule as? GameRule<T>)?.let { world.setGameRule(it, value) } != null
            }
            if (!success) player.soundError()
            success
        }
    }
}
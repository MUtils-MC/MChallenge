package de.miraculixx.mutils.utils.gui.actions

import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.*
import de.miraculixx.api.utils.gui.GUITypes
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.utils.gui.buildInventory
import de.miraculixx.mutils.utils.gui.items.ItemsChallengeSettings
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class GUIChallengeSettings(previousInv: CustomInventory, section: ChallengeSectionSetting<*>?) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta

        if ((meta?.customModel ?: 0) == 0) {
            previousInv.open(player)
            previousInv.update()
            player.click()
            return@event
        }

        val challengeKey = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.challenge.ch"), PersistentDataType.STRING)
        val settingKey = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.challenge.setting"), PersistentDataType.STRING)
        val settingsData = if (section != null) section.getValue()
        else {
            val challenge = enumOf<Challenges>(challengeKey) ?: return@event
            challenges.getSetting(challenge).settings
        }

        val click = it.click
        when (val clickedData = settingsData[settingKey]) {
            is ChallengeIntSetting -> {
                val step = clickedData.step
                val current = clickedData.getValue()
                if (click.isLeftClick) {
                    val max = clickedData.max
                    val new = current + step
                    if (new > max) {
                        player.soundError()
                        return@event
                    } else {
                        player.soundUp()
                        clickedData.setValue(new)
                    }
                } else if (click.isRightClick) {
                    val min = clickedData.min
                    val new = current - step
                    if (new < min) {
                        player.soundError()
                        return@event
                    } else {
                        player.soundDown()
                        clickedData.setValue(new)
                    }
                }
            }

            is ChallengeDoubleSetting -> {
                val step = clickedData.step
                val current = clickedData.getValue()
                if (click.isLeftClick) {
                    val max = clickedData.max
                    val new = current + step
                    if (new > max) {
                        player.soundError()
                        return@event
                    } else {
                        player.soundUp()
                        clickedData.setValue(new)
                    }
                } else if (click.isRightClick) {
                    val min = clickedData.min
                    val new = current - step
                    if (new < min) {
                        player.soundError()
                        return@event
                    } else {
                        player.soundDown()
                        clickedData.setValue(new)
                    }
                }
            }

            is ChallengeBoolSetting -> {
                if (clickedData.getValue()) {
                    player.soundDisable()
                    clickedData.setValue(false)
                } else {
                    player.soundEnable()
                    clickedData.setValue(true)
                }
            }

            is ChallengeEnumSetting -> {
                val options = clickedData.options
                val currentValue = options.lastIndexOf(clickedData.getValue())
                val lastValue = options.size - 1
                val new = if (currentValue < lastValue) options[currentValue + 1]
                else options[0]
                clickedData.setValue(new)
                player.click()
            }

            is ChallengeSectionSetting<*> -> {
                val challenge = enumOf<Challenges>(challengeKey) ?: return@event
                GUITypes.CHALLENGE_SETTINGS.buildInventory(
                    player,
                    "CH-$challengeKey-$settingKey",
                    ItemsChallengeSettings(clickedData.getValue(), challenge),
                    GUIChallengeSettings(inv, clickedData)
                )
                player.click()
                return@event
            }

            else -> return@event
        }
        inv.update()
    }
}
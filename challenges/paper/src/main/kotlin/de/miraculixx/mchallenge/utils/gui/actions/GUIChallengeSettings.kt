package de.miraculixx.mchallenge.utils.gui.actions

import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.*
import de.miraculixx.api.utils.gui.GUITypes
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.utils.gui.buildInventory
import de.miraculixx.mchallenge.utils.gui.items.ItemsChallengeSettings
import de.miraculixx.mcore.await.AwaitConfirm
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mvanilla.messages.debug
import de.miraculixx.mvanilla.messages.namespace
import de.miraculixx.mvanilla.extensions.*
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
        val id = meta?.customModel ?: 0

        if (id == 0) {
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

        if (id == 3001) {
            //Reset
            player.click()
            AwaitConfirm(player, {
                resetSettings(settingsData)
                player.soundDelete()
                inv.update()
                inv.open(player)
            }) {
                inv.open(player)
                player.click()
            }
            return@event
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

    private fun resetSettings(settings: Map<String, ChallengeSetting<out Any?>>) {
        settings.forEach { (_, data) ->
            when (data) {
                is ChallengeIntSetting -> data.setValue(data.getDefault())
                is ChallengeDoubleSetting -> data.setValue(data.getDefault())
                is ChallengeBoolSetting -> data.setValue(data.getDefault())
                is ChallengeEnumSetting -> data.setValue(data.getDefault())
                is ChallengeSectionSetting<*> -> resetSettings(data.getValue())

                else -> {
                    if (debug) println("No Settings -> ${data.materialKey} - ${data.getValue()}")
                }
            }
        }
    }
}
package de.miraculixx.mchallenge.utils.gui.actions

import de.miraculixx.challenge.api.settings.*
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.gui.GUITypes
import de.miraculixx.mchallenge.utils.gui.buildInventory
import de.miraculixx.mchallenge.utils.gui.items.ItemsChallengeSettings
import de.miraculixx.mcore.await.AwaitConfirm
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.InventoryUtils.get
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.debug
import de.miraculixx.mvanilla.messages.namespace
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class GUIChallengeSettings(previousInv: CustomInventory, section: ChallengeSectionSetting<*>?) : GUIEvent {
    private val challengeNamespace = NamespacedKey(namespace, "gui.challenge")
    private val customChallengeNamespace = NamespacedKey(namespace, "gui.customchallenge")

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

        val dataContainer = meta.persistentDataContainer
        val challengeID = dataContainer.get(challengeNamespace)
        val customChallengeID = dataContainer.get(customChallengeNamespace)?.toUUID()
        val settingKey = dataContainer.get(NamespacedKey(namespace, "gui.challenge.setting"), PersistentDataType.STRING)
        val settingsData = section?.getValue()
            ?: (challengeID?.let { key -> enumOf<Challenges>(key)?.let { ch -> challenges.getSetting(ch).settings } }
                ?: customChallengeID?.let { key -> ChallengeManager.getChallenge(key)?.data?.settings } ?: return@event)

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
                GUITypes.CHALLENGE_SETTINGS.buildInventory(
                    player,
                    "CH-${challengeID ?: customChallengeID}-$settingKey",
                    ItemsChallengeSettings(clickedData.getValue(), challengeID, customChallengeID),
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
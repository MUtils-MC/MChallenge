package de.miraculixx.mchallenge.utils.gui.actions

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mchallenge.utils.gui.GUITypes
import de.miraculixx.mchallenge.utils.gui.buildInventory
import de.miraculixx.mchallenge.utils.gui.items.ItemsChallengeSettings
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.InventoryUtils.get
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class GUIChallenge : GUIEvent {
    private val validFilters = ChallengeTags.values()
    private val challengeKey = NamespacedKey(namespace, "gui.challenge")
    private val customChallengeKey = NamespacedKey(namespace, "gui.customchallenge")

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta
        val click = it.click

        when (meta?.customModel ?: 0) {
            0 -> {
                if (it.inventory.size == 9 * 6) {
                    GUITypes.CHALLENGE_MENU.buildInventory(player, "${player.uniqueId}-CHALLENGES", inv.itemProvider, this)
                    player.click()
                }
            }

            9005 -> {
                val current = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.storage.filter")) ?: return@event
                val currentFilter = enumOf<ChallengeTags>(current) ?: return@event
                val newFilter = validFilters.enumRotate(currentFilter)
                (inv.itemProvider as? ItemFilterProvider)?.filter = newFilter.name
                player.click()
                inv.update()
            }

            else -> {
                val dataContainer = meta?.persistentDataContainer ?: return@event
                val challenge = enumOf<Challenges>(dataContainer.get(challengeKey, PersistentDataType.STRING))
                val customUUID = dataContainer.get(customChallengeKey, PersistentDataType.STRING)?.toUUID()
                val customChallenge = customUUID?.let { id -> ChallengeManager.getChallenge(id) }

                if (customChallenge == null && challenge == null) return@event

                val tags = challenge?.filter ?: customChallenge?.tags ?: return@event
                if (!getAccountStatus() && !tags.contains(ChallengeTags.FREE)) {
                    player.soundError()
                    player.closeInventory()
                    player.sendMessage(prefix + cmp("No MUtils account connected!", cError))
                    player.sendMessage(prefix + cmp("Use /ch login to connect your account", cSuccess))
                    return@event
                }

                val data = challenge?.let { ch -> challenges.getSetting(ch) } ?: customChallenge?.data ?: return@event
                if (click.isLeftClick) {
                    data.active = data.active.toggle(player)
                    inv.update()
                } else if (click.isRightClick) {
                    val settings = data.settings
                    if (settings.isEmpty()) {
                        player.soundStone()
                        return@event
                    }
                    GUITypes.CHALLENGE_SETTINGS.buildInventory(player, "CH-${challenge?.name ?: customUUID}", ItemsChallengeSettings(settings, challenge?.name, customUUID), GUIChallengeSettings(inv, null))
                    player.click()
                }
            }
        }
    }
}

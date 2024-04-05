package de.miraculixx.mchallenge.gui.actions

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.gui.GUITypes
import de.miraculixx.mchallenge.gui.buildInventory
import de.miraculixx.mchallenge.gui.items.ItemsChallenge
import de.miraculixx.mchallenge.gui.items.ItemsSettings
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.utils.UniversalChallenge
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mcommons.extensions.click
import de.miraculixx.mcommons.extensions.soundError
import de.miraculixx.mcommons.extensions.soundStone
import de.miraculixx.mcommons.text.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ActionMenu : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta ?: return@event
        val click = it.click

        val applicable = when (meta.customModel) {
            1 -> {
                if (!getAccountStatus()) {
                    if (click.isRightClick) {
                        player.closeInventory()
                        val locale = player.language()
                        val dot = cmp("• ", NamedTextColor.DARK_GRAY)
                        val clickMsg = cmp(" (" + locale.msgString("common.click") + ")\n")
                        player.sendMessage(
                            cmp(" \n⇒ ", NamedTextColor.DARK_RED) + cmp(locale.msgString("command.notLoggedIn") + "\n", cError) +
                                    cmp("                                                      \n", NamedTextColor.DARK_RED, strikethrough = true) +
                                    dot + cmp("Account Panel", cMark).addUrl("https://mutils.net/oauth/login").addHover(cmp("Click to open your account panel")) + clickMsg +
                                    dot + cmp("Support", cMark).addUrl("https://dc.mutils.net").addHover(cmp("Click to join our support Discord")) + clickMsg +
                                    dot + cmp("/challenge login <key>", cMark).addCommand("/challenge login ").addHover(cmp("Click to login with your key"))
                        )
                        player.soundError()
                        return@event
                    }
                }
                Challenges.entries.filter { ch -> !ch.status }.map { ch -> UniversalChallenge(ch) }
            }

            2 -> Challenges.entries.filter { ch -> ch.status }.map { ch -> UniversalChallenge(ch) }

            3 -> {
                if (it.click.isRightClick) {
                    player.sendMessage(cmp("Addon Browser"))
                    return@event
                }

                if (ChallengeManager.getCustomChallenges().isEmpty()) {
                    player.closeInventory()
                    player.sendMessage(prefix + player.msg("command.noAddons"))
                    player.soundError()
                    return@event
                }
                ChallengeManager.getCustomChallenges().map { ch -> UniversalChallenge(addon = ch.key) }
            }

            4 -> ChallengeManager.favoriteChallenges

            5 -> ChallengeManager.historyChallenges

            6 -> {
                player.click()
                GUITypes.SETTINGS_CUSTOM_3.buildInventory(player, "GLOBAL_SETTINGS", ItemsSettings(player.language()), ActionSettings(inv))
                return@event
            }

            else -> {
                player.soundStone()
                return@event
            }
        }.toSet()

        player.click()
        GUITypes.CHALLENGE_MENU.buildInventory(player, player.uniqueId.toString(), ItemsChallenge(applicable, player.language()), ActionChallenge(inv))
    }
}
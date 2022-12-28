package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.gui.items.ItemFilterProvider
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.utils.cotm
import de.miraculixx.mutils.utils.getAccountStatus
import de.miraculixx.mutils.utils.settings
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

class ItemsChallenge : ItemFilterProvider {
    override var filter = StorageFilter.NO_FILTER

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            val challenges = Challenges.values()
            val amount = challenges.size - 1
            val range = challenges.copyOfRange(from.coerceAtMost(amount), to.coerceAtMost(amount))
            val status = getAccountStatus()

            // Adding Challenge of the month at first
            if (isMatchingFilter(cotm, filter)) {
                val monthly = getChallengeItem(cotm)
                val item = monthly.first
                item.editMeta {
                    it.name = it.name?.color(cSuccess)
                    it.lore(it.lore()?.plus(cmp("Challenge of the Month", cSuccess)))
                }
                put(item, monthly.second)
            }

            // Adding all other Challenges in reversed order (newest first)
            range.reversed().forEach { challenge ->
                if (challenge == cotm) return@forEach
                if (isMatchingFilter(challenge, filter)) {
                    val data = getChallengeItem(challenge)
                    if (!status) {
                        data.first.editMeta {
                            it.name = it.name?.color(cError)
                            it.lore(it.lore()?.plus(cmp("Premium only", cError)))
                        }
                    } else data.first.editMeta { it.name = it.name?.color(cHighlight) }
                    put(data.first, data.second)
                }
            }
        }
    }

    private fun isMatchingFilter(module: Challenges, filter: StorageFilter?): Boolean {
        return filter == null || filter == StorageFilter.NO_FILTER || module.matchingFilter(filter)
    }

    //Utilities
    private fun getChallengeItem(challenge: Challenges): Pair<ItemStack, Boolean> {
        return challenge.getIcon().apply {
            editMeta {
                it.displayName(getName(challenge))
                it.lore(getLore(challenge))
            }
        } to settings.getBoolean(challenge.name + ".active")
    }

    private fun getFilter(challenge: Challenges): List<Component> {
        return buildList {
            emptyComponent()
            cmp("∙ ") + cmp("Filters", cHighlight, underlined = true)
            addAll(challenge.filter.map { cmp("   - ") + cmp(it.name) })
        }
    }

    private fun getName(challenge: Challenges): Component {
        return cmp("", cHighlight, underlined = true) + msg("items.ch.${challenge.name}.n")
    }

    private fun getLore(challenge: Challenges): List<Component> {
        return buildList {
            val hasSettings = challenge.hasSettings()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            addAll(msgList("items.ch.${challenge.name}.l"))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (hasSettings) addAll(challenge.getSettings(settings).map {
                cmp("   ") + msg("items.chS.${challenge.name}.${it.id}.n") + cmp(": ") + cmp(it.value, cHighlight) + cmp(" (Default ${it.default})")
            }) else add(cmp("   None", italic = true))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Filter", cHighlight, underlined = true))
            addAll(getFilter(challenge))

            add(emptyComponent())
            if (hasSettings) {
                add(cmp("Left-Click ", cHighlight) + cmp("≫ Toggle Active"))
                add(cmp("Right-Click ", cHighlight) + cmp("≫ Open Settings"))
            } else add(cmp("Click ", cHighlight) + cmp("≫ Toggle Active"))
        }
    }
}
package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.extensions.msg
import de.miraculixx.mutils.gui.items.ItemFilterProvider
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.utils.cotm
import de.miraculixx.mutils.utils.getAccountStatus
import de.miraculixx.mutils.utils.settings.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemsChallenge : ItemFilterProvider {
    override var filter = StorageFilter.NO_FILTER

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            val challenges = Challenges.values()
            val amount = challenges.size
            val range = if (from >= amount) emptyList() else challenges.copyOfRange(from, to.coerceAtMost(amount)).filter {
                it != cotm
            }
            val status = getAccountStatus()

            // Adding Challenge of the month at first - only if first index
            if (isMatchingFilter(cotm, filter) && from == 0) {
                val monthly = getChallengeItem(cotm)
                val item = monthly.first
                item.editMeta {
                    it.name = it.name?.color(cSuccess)
                    it.lore(it.lore()?.apply { add(0, cmp("Challenge of the Month", cSuccess)) })
                }
                put(item, monthly.second)
            }

            // Adding all other Challenges in reversed order (newest first)
            range.forEach { challenge ->
                if (isMatchingFilter(challenge, filter)) {
                    val data = getChallengeItem(challenge)
                    if (!status && !challenge.status) {
                        data.first.editMeta {
                            it.name = it.name?.color(cError)
                            it.lore(it.lore()?.apply { add(0, cmp("Premium only", cError)) })
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
                it.customModel = 1
                it.displayName(getName(challenge))
                it.lore(getLore(challenge))
                it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                it.persistentDataContainer.set(NamespacedKey(namespace, "gui.challenge"), PersistentDataType.STRING, challenge.name)
            }
        } to challenges.getSetting(challenge).active
    }

    private fun getFilter(challenge: Challenges): List<Component> {
        return buildList {
            emptyComponent()
            cmp("∙ ") + cmp("Filters", cHighlight, underlined = true)
            addAll(challenge.filter.map { cmp("   - ") + cmp(it.name) })
        }
    }

    private fun getName(challenge: Challenges): Component {
        return cmp("", cHighlight, bold = true) + msg("items.ch.${challenge.name}.n")
    }

    private fun getLore(challenge: Challenges): List<Component> {
        return buildList {
            val settings = challenges.getSetting(challenge).settings
            val hasSettings = settings.isNotEmpty()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            addAll(msgList("items.ch.${challenge.name}.l"))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (hasSettings) {
                settings.forEach { (key, data) ->
                    addAll(challenge.getSettingLore(data, key, false))
                }
            } else add(cmp("   None", italic = true))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Filter", cHighlight, underlined = true))
            addAll(getFilter(challenge))

            add(emptyComponent())
            add(msgClickLeft + cmp("Toggle Active"))
            if (hasSettings) add(msgClickRight + cmp("Open Settings"))
        }
    }

    private fun Challenges.getSettingLore(data: ChallengeSetting<*>, key: String, isSection: Boolean): List<Component> {
        return if (data is ChallengeSectionSetting<*>) {
            buildList {
                add(cmp("   " + msgString("items.chS.$name.$key.n"), cMark))
                addAll(data.getValue().flatMap { getSettingLore(it.value, it.key, true) })
            }
        } else {
            val prefix = if (isSection) cmp("    → ", NamedTextColor.DARK_GRAY) else cmp("   ")
            val info = if (data is ChallengeBoolSetting) data.getValue().msg() to data.getDefault().msg()
            else data.getValue().toString() to data.getValue().toString()
            listOf(prefix + cmp(msgString("items.chS.$name.$key.n")) + cmp(": ") + cmp("${info.first}${data.getUnit()}" , cHighlight) + cmp(" (Default ${info.second})"))
        }
    }
}
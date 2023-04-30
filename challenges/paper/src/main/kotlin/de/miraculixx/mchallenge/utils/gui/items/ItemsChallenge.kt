package de.miraculixx.mchallenge.utils.gui.items

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.*
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mchallenge.utils.cotm
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsChallenge : ItemFilterProvider {
    override var filter = ChallengeTags.NO_FILTER.name

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            val finalFilter = enumOf<ChallengeTags>(filter)
            val officialChallenges = Challenges.values().filter { isMatchingFilter(it, finalFilter) }.map { ChallengeItemData(it.icon, challenges.getSetting(it), it, tags = it.filter) }
            val addonChallenges = ChallengeManager.getCustomChallenges().filter { isMatchingFilter(it.value.tags, finalFilter) }.map { ChallengeItemData(it.value.icon, it.value.data, customUUID = it.key, tags = it.value.tags) }
            val allChallenges = buildList {
                addAll(officialChallenges)
                addAll(addonChallenges)
            }
            val amount = allChallenges.size
            val range = if (from >= amount) mutableListOf() else allChallenges
                .subList(from, to.coerceAtMost(amount))
            val status = getAccountStatus()
            val hideAllPremiumStuff = MChallenge.settings.iReallyDontWantAnyPremiumFeatures

            // Adding all other Challenges - globals first
            range.forEach { challenge ->
                val data = getChallengeItem(challenge)
                when {
                    challenge.key == cotm -> data.first.editMeta {
                        it.name = it.name?.color(cSuccess)
                        it.lore(it.lore()?.apply { add(0, cmp("Challenge of the Month", cSuccess)) })
                    }
                    !status && challenge.key?.status == false -> {
                        data.first.editMeta {
                            it.name = it.name?.color(cError)
                            it.lore(it.lore()?.apply { add(0, cmp("Premium only", cError)) })
                        }
                        if (hideAllPremiumStuff) return@forEach
                    }
                    else -> data.first.editMeta { it.name = it.name?.color(cHighlight) }
                }
                put(data.first, data.second)
            }
        }
    }

    private fun isMatchingFilter(module: Challenges, filter: ChallengeTags?): Boolean {
        return filter == null || filter == ChallengeTags.NO_FILTER || module.matchingFilter(filter)
    }

    private fun isMatchingFilter(tags: Set<ChallengeTags>, tag: ChallengeTags?): Boolean {
        return tag == null || tag == ChallengeTags.NO_FILTER || tags.contains(tag)
    }

    //Utilities
    private fun getChallengeItem(itemData: ChallengeItemData): Pair<ItemStack, Boolean> {
        val item = itemStack(enumOf<Material>(itemData.icon.material) ?: Material.BARRIER) {
            meta {
                customModel = 1
                displayName(getName(itemData.icon, itemData.key))
                lore(getLore(itemData.icon, itemData.key, itemData.settings, itemData.tags))
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                persistentDataContainer.set(NamespacedKey(namespace, "gui.challenge"), PersistentDataType.STRING, (itemData.key ?: itemData.customUUID ?: "unknown").toString())
            }
        }
        return item to itemData.settings.active
    }

    private fun getFilter(tags: Set<ChallengeTags>): List<Component> {
        return buildList {
            emptyComponent()
            cmp("∙ ") + cmp("Filters", cHighlight, underlined = true)
            addAll(tags.map { cmp("   - ") + cmp(it.name) })
        }
    }

    private fun getName(icon: Icon, key: Challenges?): Component {
        return if (icon.naming?.name != null) (icon.naming?.name ?: cmp("Unknown", cError))
        else cmp("", cHighlight, bold = true) + msg("items.ch.${key?.name}.n")
    }

    private fun getLore(icon: Icon, key: Challenges?, data: ChallengeData, tags: Set<ChallengeTags>): List<Component> {
        return buildList {
            val settings = data.settings
            val hasSettings = settings.isNotEmpty()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            if (icon.naming?.lore != null) addAll(icon.naming?.lore ?: emptyList())
            else addAll(msgList("items.ch.${key?.name}.l"))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (hasSettings) {
                settings.forEach { (settingKey, settingData) ->
                    addAll(getSettingLore(settingData, settingKey, false, key?.name, data.settingNames))
                }
            } else add(cmp("   None", italic = true))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Filter", cHighlight, underlined = true))
            addAll(getFilter(tags))

            add(emptyComponent())
            add(msgClickLeft + cmp("Toggle Active"))
            if (hasSettings) add(msgClickRight + cmp("Open Settings"))
        }
    }

    private fun getSettingLore(data: ChallengeSetting<*>, settingsKey: String, isSection: Boolean, challengeKey: String?, settingNaming: Map<String, IconNaming>): List<Component> {
        return if (data is ChallengeSectionSetting<*>) {
            buildList {
                val naming = settingNaming[settingsKey]
                if (naming != null) add(cmp("   ") + naming.name)
                else add(cmp("   " + msgString("items.chS.$challengeKey.$settingsKey.n"), cMark))
                addAll(data.getValue().flatMap { getSettingLore(it.value, it.key, true, challengeKey, settingNaming) })
            }
        } else {
            val prefix = if (isSection) cmp("    → ", NamedTextColor.DARK_GRAY) else cmp("   ")
            val info = if (data is ChallengeBoolSetting) data.getValue().msg() to data.getDefault().msg()
            else data.getValue().toString() to data.getValue().toString()
            val naming = settingNaming[settingsKey]
            val settingName = naming?.name ?: cmp(msgString("items.chS.$challengeKey.$settingsKey.n"))
            listOf(prefix + settingName + cmp(": ") + cmp("${info.first}${data.getUnit()}", cHighlight))
        }
    }

    private data class ChallengeItemData(
        val icon: Icon,
        val settings: ChallengeData,
        val key: Challenges? = null,
        val customUUID: UUID? = null,
        val tags: Set<ChallengeTags>
    )
}
package de.miraculixx.mchallenge.gui.items

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.settings.ChallengeBoolSetting
import de.miraculixx.challenge.api.settings.ChallengeData
import de.miraculixx.challenge.api.settings.ChallengeSectionSetting
import de.miraculixx.challenge.api.settings.ChallengeSetting
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.UniversalChallenge
import de.miraculixx.mchallenge.utils.getAccountStatus
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsChallenge(private val applicable: Set<UniversalChallenge>) : ItemFilterProvider {
    override var filter = ChallengeTags.NO_FILTER.name
    private val challengeKey = NamespacedKey(namespace, "gui.challenge")
    private val customChallengeKey = NamespacedKey(namespace, "gui.customchallenge")

    private fun Challenges.getChallengeData(): ChallengeItemData {
        return ChallengeItemData(icon, challenges.getSetting(this), this, tags = filter, owner = "MUtils")
    }

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            // Map all applicable challenges to an icon
            val challenges = applicable.mapNotNull {
                it.internal?.getChallengeData() ?: it.addon?.let { uuid ->
                    ChallengeManager.getCustomChallenge(uuid)?.let { custom ->
                        ChallengeItemData(custom.icon, custom.data, customUUID = uuid, tags = custom.tags, owner = custom.owner)
                    }
                }
            }

            // Filter for variable user setting
            val filterEnum = enumOf<ChallengeTags>(filter) ?: ChallengeTags.NO_FILTER
            val filteredChallenges = if (filterEnum == ChallengeTags.NO_FILTER) challenges else challenges.filter {
                it.tags.contains(filterEnum)
            }

            // Check render range
            val renderedAmount = filteredChallenges.size
            val visibleChallenges = if (from >= renderedAmount) listOf()
            else filteredChallenges.subList(from, to.coerceAtMost(renderedAmount))

            // Render all icons
            val status = getAccountStatus()
            visibleChallenges.forEach { icon ->
                val universal = icon.toUniversal()
                val data = getChallengeItem(icon, universal)
                val isAddon = icon.customUUID != null

                if (isAddon) {
                    data.first.editMeta {
                        it.lore(it.lore()?.apply {
                            add(0, cmp("Mod by ${icon.owner}", NamedTextColor.GOLD))
                        })
                    }
                }

                if (!status && !(icon.key?.status ?: icon.tags.contains(ChallengeTags.FREE))) {
                    data.first.editMeta {
                        it.name = it.name?.color(cError)
                        it.lore(it.lore()?.apply {
                            add(0, cmp("No MUtils account connected!", cError))
                        })
                    }
                } else if (icon.tags.contains(ChallengeTags.BETA)) {
                    data.first.editMeta {
                        it.name = it.name?.color(cError)
                        it.lore(it.lore()?.apply {
                            add(0, cmp("BETA - This challenge is unfinished", cError))
                        })
                    }
                }

                put(data.first, data.second)
            }
        }
    }

    //Utilities
    private fun getChallengeItem(itemData: ChallengeItemData, universal: UniversalChallenge): Pair<ItemStack, Boolean> {
        val icon = itemData.icon
        val isFavorite = ChallengeManager.favoriteChallenges.contains(universal)
        val item = itemStack(enumOf<Material>(icon.material) ?: Material.BARRIER) {
            meta {
                customModel = 1
                displayName(getName(icon, itemData.key, isFavorite))
                lore(getLore(itemData, isFavorite))
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                itemData.key?.let { persistentDataContainer.set(challengeKey, PersistentDataType.STRING, it.name) }
                itemData.customUUID?.let {
                    persistentDataContainer.set(
                        customChallengeKey,
                        PersistentDataType.STRING,
                        it.toString()
                    )
                }
            }
            icon.texture?.let { itemMeta = (itemMeta as SkullMeta).skullTexture(it) }
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

    private fun getName(icon: Icon, key: Challenges?, isFavorite: Boolean): Component {
        val suffix = if (isFavorite) cmp(" ★", NamedTextColor.GOLD) else cmp(" ☆", NamedTextColor.DARK_GRAY)
        return if (icon.naming?.name != null) (icon.naming?.name?.decorate(bold = true, italic = false)?.append(suffix)
            ?: cmp("Unknown", cError, bold = true))
        else cmp("", cHighlight, bold = true) + msg("items.ch.${key?.name}.n") + suffix
    }

    private val loreSettings = cmp("∙ ") + cmp(msgString("common.settings"), cHighlight, underlined = true)
    private fun getLore(iconData: ChallengeItemData, isFavorite: Boolean): List<Component> {
        return buildList {
            val data = iconData.settings
            val settings = data.settings
            val hasSettings = settings.isNotEmpty()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            val icon = iconData.icon
            if (icon.naming?.lore != null) addAll(icon.naming?.lore?.map { cmp("   ") + it } ?: emptyList())
            else addAll(msgList("items.ch.${iconData.key?.name}.l"))

            add(emptyComponent())
            add(loreSettings)
            if (hasSettings) {
                settings.forEach { (settingKey, settingData) ->
                    addAll(getSettingLore(settingData, settingKey, false, iconData.key?.name, data.settingNames))
                }
            } else add(cmp("   None", italic = true))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Filter", cHighlight, underlined = true))
            addAll(getFilter(iconData.tags))

            add(emptyComponent())
            add(msgClickLeft + cmp("Toggle Active"))
            if (hasSettings) add(msgClickRight + cmp("Open Settings"))
            add(msgShiftClick + if (isFavorite) cmp("Remove Favorite") else cmp("Add Favorite"))
        }
    }

    private fun getSettingLore(
        data: ChallengeSetting<*>,
        settingsKey: String,
        isSection: Boolean,
        challengeKey: String?,
        settingNaming: Map<String, IconNaming>
    ): List<Component> {
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
        val tags: Set<ChallengeTags>,
        val owner: String
    ) {
        fun toUniversal() = UniversalChallenge(key, customUUID)
    }
}
package de.miraculixx.mchallenge.gui.items

import de.miraculixx.challenge.api.settings.*
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.getMaterial
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.extensions.msg
import de.miraculixx.mcommons.namespace
import de.miraculixx.mcommons.serializer.plainSerializer
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsChallengeSettings(
    private val challengeSettings: Map<String, ChallengeSetting<*>>,
    private val challengeKey: String?,
    private val customUUID: UUID?,
    private val locale: Locale
) : ItemProvider {
    private val msgSetting = listOf(emptyComponent(), cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
    private val challengeNamespace = NamespacedKey(namespace, "gui.challenge")
    private val customChallengeNamespace = NamespacedKey(namespace, "gui.customchallenge")

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        val customChallengeData = customUUID?.let { ChallengeManager.getChallenge(it)?.data }

        return challengeSettings.map {
            val key = it.key
            val data = it.value
            val material = data.getMaterial()
            val infoLore = msgSetting + data.getSettingLore(key, challengeKey, customChallengeData) + emptyComponent() + data.getClickLore()
            itemStack(material) {
                meta {
                    customModel = 1
                    val icon = challengeKey?.let { id -> enumOf<Challenges>(id)?.icon }
                        ?: customChallengeData?.settingNames?.get(key)?.let { naming -> Icon("BARRIER", naming = naming) }
                        ?: Icon("BARRIER", naming = IconNaming(cmp("Unknown"), emptyList())) // Fallback
                    name = getSettingName(icon, challengeKey, key)
                    lore(infoLore)
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    challengeKey?.let { key -> persistentDataContainer.set(challengeNamespace, PersistentDataType.STRING, key) }
                    customUUID?.let { id -> persistentDataContainer.set(customChallengeNamespace, PersistentDataType.STRING, id.toString()) }
                    persistentDataContainer.set(NamespacedKey(namespace, "gui.challenge.setting"), PersistentDataType.STRING, key)
                }
            }
        }
    }

    override fun getExtra(): List<ItemStack> {
        return listOf(
            itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 3001
                    name = cmp(locale.msgString("items.general.reset.n"), cError)
                    lore(locale.msgList("items.general.reset.l", inline = "<grey>"))
                    challengeKey?.let { key -> persistentDataContainer.set(challengeNamespace, PersistentDataType.STRING, key) }
                    customUUID?.let { id -> persistentDataContainer.set(customChallengeNamespace, PersistentDataType.STRING, id.toString()) }
                    skullTexture(KHeads.ARROW_RESET_WHITE)
                }
            }
        )
    }

    private fun getSettingName(icon: Icon, challengeKey: String?, key: String): Component {
        return if (icon.naming != null) (icon.naming?.name?.let { cmp(plainSerializer.serialize(it), cHighlight, true) } ?: cmp("Unknown", cError, bold = true))
        else cmp(locale.msgString("items.chS.${challengeKey}.$key.n"), cHighlight, bold = true)
    }

    private fun ChallengeSetting<*>.getSettingLore(key: String, challengeKey: String?, customChallengeData: ChallengeData?): List<Component> {
        return if (this is ChallengeSectionSetting<*>) {
            buildList {
                addAll(getValue().flatMap { it.value.getSettingLore(it.key, challengeKey, customChallengeData) })
            }
        } else {
            val settingName = challengeKey?.let { cmp(locale.msgString("items.chS.$it.$key.n")) } ?: customChallengeData?.settingNames?.get(key)?.name ?: cmp("Unknown")
            if (this is ChallengeBoolSetting) {
                val info = getValue() to getDefault()
                listOf(cmp("   ") + settingName + cmp(": ") + cmp(info.first.msg(locale), if (info.first) cSuccess else cError) + if (info.first == info.second) emptyComponent() else cmp(" (Default ${info.second})", cBaseDark))
            } else {
                val info = getValue() to getDefault()
                listOf(cmp("   ") + settingName + cmp(": ") + cmp("${info.first}${getUnit()}", cHighlight) + if (info.first == info.second) emptyComponent() else cmp(" (Default ${info.second}${getUnit()})", cBaseDark))
            }
        }
    }

    private fun ChallengeSetting<*>.getClickLore(): List<Component> {
        val unit = getUnit()
        return when (this) {
            is ChallengeIntSetting -> {
                val info = "$step$unit"
                listOf(locale.msgClickLeft() + cmp("+$info"), locale.msgClickRight() + cmp("-$info"))
            }

            is ChallengeDoubleSetting -> {
                val info = "$step$unit"
                listOf(locale.msgClickLeft() + cmp("+$info"), locale.msgClickRight() + cmp("-$info"))
            }

            is ChallengeBoolSetting -> listOf(locale.msgClick() + cmp("Toggle"))

            is ChallengeEnumSetting -> listOf(locale.msgClick() + cmp("Rotate"))

            is ChallengeSectionSetting<*> -> listOf(locale.msgClick() + cmp("Open Menu"))

            else -> emptyList()
        }
    }
}
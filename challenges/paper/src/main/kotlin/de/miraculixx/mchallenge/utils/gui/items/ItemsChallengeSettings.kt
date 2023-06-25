package de.miraculixx.mchallenge.utils.gui.items

import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.challenge.api.settings.*
import de.miraculixx.challenge.api.utils.CustomHeads
import de.miraculixx.challenge.api.utils.Icon
import de.miraculixx.challenge.api.utils.IconNaming
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.challenges.getMaterial
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsChallengeSettings(private val challengeSettings: Map<String, ChallengeSetting<*>>, private val challengeKey: String?, private val customUUID: UUID?) : ItemProvider {
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
                meta {
                    customModel = 3001
                    name = cmp(msgString("items.general.reset.n"), cError)
                    lore(msgList("items.general.reset.l", inline = "<grey>"))
                    challengeKey?.let { key -> persistentDataContainer.set(challengeNamespace, PersistentDataType.STRING, key) }
                    customUUID?.let { id -> persistentDataContainer.set(customChallengeNamespace, PersistentDataType.STRING, id.toString()) }
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(CustomHeads.ARROW_RESET.value)
            }
        )
    }

    private fun getSettingName(icon: Icon, challengeKey: String?, key: String): Component {
        return if (icon.naming != null) (icon.naming?.name?.let { cmp(plainSerializer.serialize(it), cHighlight, true) } ?: cmp("Unknown", cError, bold = true))
        else cmp(msgString("items.chS.${challengeKey}.$key.n"), cHighlight, bold = true)
    }

    private fun ChallengeSetting<*>.getSettingLore(key: String, challengeKey: String?, customChallengeData: ChallengeData?): List<Component> {
        return if (this is ChallengeSectionSetting<*>) {
            buildList {
                addAll(getValue().flatMap { it.value.getSettingLore(it.key, challengeKey, customChallengeData) })
            }
        } else {
            val info = if (this is ChallengeBoolSetting) getValue().msg() to getDefault().msg()
            else getValue().toString() to getDefault().toString()
            val settingName = challengeKey?.let { cmp(msgString("items.chS.$it.$key.n")) } ?: customChallengeData?.settingNames?.get(key)?.name ?: cmp("Unknown")
            listOf(cmp("   ") + settingName + cmp(": ") + cmp("${info.first}${getUnit()}", cHighlight) + cmp(" (Default ${info.second}${getUnit()})"))
        }
    }

    private fun ChallengeSetting<*>.getClickLore(): List<Component> {
        val unit = getUnit()
        return when (this) {
            is ChallengeIntSetting -> {
                val info = "$step$unit"
                listOf(msgClickLeft + cmp("+$info"), msgClickRight + cmp("-$info"))
            }

            is ChallengeDoubleSetting -> {
                val info = "$step$unit"
                listOf(msgClickLeft + cmp("+$info"), msgClickRight + cmp("-$info"))
            }

            is ChallengeBoolSetting -> listOf(msgClick + cmp("Toggle"))

            is ChallengeEnumSetting -> listOf(msgClick + cmp("Rotate"))

            is ChallengeSectionSetting<*> -> listOf(msgClick + cmp("Open Menu"))

            else -> emptyList()
        }
    }
}
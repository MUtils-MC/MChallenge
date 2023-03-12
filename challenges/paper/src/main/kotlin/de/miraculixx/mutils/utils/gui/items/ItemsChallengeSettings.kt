package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.*
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.extensions.msg
import de.miraculixx.mutils.gui.Head64
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.challenges.getMaterial
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class ItemsChallengeSettings(private val challengeSettings: Map<String, ChallengeSetting<*>>, private val challenge: Challenges) : ItemProvider {
    private val msgSetting = listOf(emptyComponent(), cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        val chName = challenge.name

        return challengeSettings.map {
            val key = it.key
            val data = it.value
            val material = data.getMaterial()
            val infoLore = msgSetting + data.getSettingLore(key, chName) + emptyComponent() + data.getClickLore()
            itemStack(material) {
                meta {
                    customModel = 1
                    name = cmp(msgString("items.chS.$chName.$key.n"), cHighlight, true)
                    lore(infoLore)
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    persistentDataContainer.set(NamespacedKey(namespace, "gui.challenge.ch"), PersistentDataType.STRING, chName)
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
                    persistentDataContainer.set(NamespacedKey(namespace, "gui.challenge.ch"), PersistentDataType.STRING, challenge.name)
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RESET.value)
            }
        )
    }

    private fun ChallengeSetting<*>.getSettingLore(key: String, challenge: String): List<Component> {
        return if (this is ChallengeSectionSetting<*>) {
            buildList {
                addAll(getValue().flatMap { it.value.getSettingLore(it.key, challenge) })
            }
        } else {
            val info = if (this is ChallengeBoolSetting) getValue().msg() to getDefault().msg()
            else getValue().toString() to getDefault().toString()
            listOf(cmp("   ") + cmp(msgString("items.chS.${challenge}.$key.n")) + cmp(": ") + cmp("${info.first}${getUnit()}", cHighlight) + cmp(" (Default ${info.second})"))
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
package de.miraculixx.mchallenge.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ItemsSettings(private val locale: Locale) : ItemProvider {

    override fun getSlotMap(): Map<Int, ItemStack> {
        val settings = ConfigManager.settings
        return mapOf(
            11 to itemStack(Material.LOOM) {
                meta {
                    name = cmp(locale.msgString("items.settings.theme.n"), cHighlight)
                    lore(
                        locale.msgList("items.settings.theme.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp(locale.msgString("items.settings.language.s")) + cmp(settings.language.toLanguageTag(), cHighlight),
                            emptyComponent(),
                            locale.msgClick() + cmp(locale.msgString("common.switch"))
                        )
                    )
                    customModel = 1
                }
            },

            12 to itemStack(Material.BARREL) {
                meta {
                    name = cmp(locale.msgString("items.settings.compact.n"), cHighlight)
                    lore(
                        locale.msgList("items.settings.compact.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp("- Compact", if (settings.gui.compact) cMark else cBaseTag),
                            cmp("   ") + cmp("- Detailed", if (!settings.gui.compact) cMark else cBaseTag),
                            emptyComponent(),
                            locale.msgClick() + cmp(locale.msgString("common.switch"))
                        )
                    )
                    customModel = 2
                }
            },

            15 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    skullTexture(KHeads.GLOBE)
                    name = cmp(locale.msgString("items.settings.language.n"), cHighlight)
                    lore(
                        locale.msgList("items.settings.language.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp(locale.msgString("items.settings.language.s")) + cmp(settings.language.toLanguageTag(), cHighlight),
                            emptyComponent(),
                            locale.msgClick() + cmp(locale.msgString("common.switch"))
                        )
                    )
                    customModel = 3
                }
            }
        )
    }

    private fun getLoreSettings() = cmp("∙ ") + cmp(locale.msgString("common.settings"), cHighlight, underlined = true)
}
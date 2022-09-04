package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.text.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class CreatorItems {
    /**
     * Item Library for Challenge Creator
     * - 1 -> Modify - main menu
     * - 2 -> Modify - Event Group Menu
     * - 3 -> Modify - Action Group Menu
     */
    fun getItems(id: Int, chData: CustomChallengeData, eventData: CreatorEvent?): Map<ItemStack, Int> {
        return when (id) {
            1 -> g1(chData)
            2 -> g2(chData)
            3 -> g3(chData, eventData!!)

            else -> emptyMap()
        }
    }

    private fun g3(chData: CustomChallengeData, eventData: CreatorEvent): Map<ItemStack, Int> {
        val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
        return mapOf(
            buildItem(Material.CHEST, 501, cmp("Saved Actions", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.savedActions"))
                }
            ) to 12,
            buildItem(Material.ENDER_CHEST, 502, cmp("Action Library", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.actionLib"))
                }
            ) to 14,
            InvUtils.secondaryPlaceholder.editMeta(dataContainer = PDCValues(NamespacedKey.fromString("gui.creator.uuid", Manager)!!, chData.uuid.toString()))
                .editMeta(dataContainer = PDCValues(NamespacedKey.fromString("gui.creator.event", Manager)!!, eventData.ordinal.toString())) to 0,
        )
    }

    // <active-events> <-> <add-new-event>
    // 101-199
    private fun g2(chData: CustomChallengeData): Map<ItemStack, Int> {
        val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
        return mapOf(
            buildItem(Material.CHEST, 101, cmp("Saved Events", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.savedEvents"))
                }
            ) to 12,
            buildItem(Material.ENDER_CHEST, 102, cmp("Event Library", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.eventLib"))
                }
            ) to 14,
            InvUtils.secondaryPlaceholder.editMeta(dataContainer = PDCValues(NamespacedKey.fromString("gui.creator.uuid", Manager)!!, chData.uuid.toString())) to 0
        )
    }

    // 1-99
    private fun g1(chData: CustomChallengeData): Map<ItemStack, Int> {
        val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
        return mapOf(
            buildItem(Material.NAME_TAG, 1, cmp("Challenge Name", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.name"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Name"))
                }
            ) to 10,
            buildItem(Material.WRITABLE_BOOK, 2, cmp("Challenge Description", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.description"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Description"))
                }
            ) to 11,
            buildItem(Material.ITEM_FRAME, 3, cmp("Display Icon", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.icon"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Icon"))
                }
            ) to 15,
            buildItem(Material.TARGET, 4, cmp("Events", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.events"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Open Menu"))
                }
            ) to 16,

            chData.item.editMeta(modelData = 0) to 13,
            InvUtils.secondaryPlaceholder.editMeta(dataContainer = PDCValues(NamespacedKey.fromString("gui.creator.uuid", Manager)!!, chData.uuid.toString())) to 0
        )
    }
}
package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.config.Config
import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.enums.gui.StorageFilter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsChallenge(private val config: Config) {

    fun getItems(id: Int, filter: StorageFilter?): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(filter)
            else -> {
                linkedMapOf(Pair(ItemStack(Material.BARRIER), false))
            }
        }
        /* ID Glossary
        1 → Main Challenges
        2 -> TODO
         */
        return list
    }

    private fun g1(filter: StorageFilter?): LinkedHashMap<ItemStack, Boolean> {
        val map = LinkedHashMap<ItemStack, Boolean>()

        if (isMatchingFilter(challengeOfTheMonth, filter)) {
            val monthly = getChallengeItem(challengeOfTheMonth)
            val item = monthly.first
            val meta = item.itemMeta
            val green = TextColor.fromHexString("#55FF55")
            meta.displayName(Component.text(ChatColor.stripColor(meta.name) ?: "error").color(green).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD))
            val lore = meta.lore()
            lore?.add(0, Component.text("Challenge of the Month").color(green).decoration(TextDecoration.ITALIC, false))
            meta.lore(lore)
            item.itemMeta = meta
            map[item] = monthly.second
        }

        Challenge.values().reversed().forEach { module ->
            if (module.isChallenge()) {
                if (module == challengeOfTheMonth) return@forEach
                if (isMatchingFilter(module, filter)) {
                    val pair = getChallengeItem(module)
                    if (!premium) {
                        val item = pair.first
                        val meta = item.itemMeta
                        val lore = meta.lore()
                        lore?.add(0, Component.text("Premium only").color(TextColor.fromHexString("#FF5555")).decoration(TextDecoration.ITALIC, false))
                        meta.lore(lore)
                        item.itemMeta = meta
                    }
                    map[pair.first] = pair.second
                }
            }
        }
        return map
    }

    private fun isMatchingFilter(module: Challenge, filter: StorageFilter?): Boolean {
        return filter == null || filter == StorageFilter.NO_FILTER || module.matchingFilter(filter)
    }

    //Utilities
    private fun getChallengeItem(challenge: Challenge): Pair<ItemStack, Boolean> {
        return Pair(challenge.getIcon().apply { editMeta {
            it.displayName(getName(challenge))
            it.lore(getLore(challenge))
        } } to )
    }

    private fun getFilter(challenge: Challenge): List<Component> {
        return buildList {
            emptyComponent()
            cmp("∙ ") + cmp("Filters", cHighlight, underlined = true)
            addAll(challenge.filter.map { cmp("   - ") + cmp(it.name) })
        }
    }

    private fun getName(challenge: Challenge): Component {
        return cmp("", cHighlight, underlined = true) + msg("items.ch.${challenge.name}.n")
    }

    private fun getLore(challenge: Challenge): List<Component> {
        return buildList {
            val hasSettings = challenge.hasSettings()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            addAll(msgList("items.ch.${challenge.name}.l"))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (hasSettings) addAll(challenge.getSettings(config).map {
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
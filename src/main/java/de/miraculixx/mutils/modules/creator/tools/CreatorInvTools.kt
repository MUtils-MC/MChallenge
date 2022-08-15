package de.miraculixx.mutils.modules.creator.tools

import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.utils.text.emptyComponent
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

class CreatorInvTools {
    fun getAllItems(vararg infoLore: Component): Map<ItemStack, Boolean> {
        return buildMap {
            var counter = 1
            val lore = buildList {
                add(emptyComponent())
                addAll(infoLore)
            }
            CreatorManager.getAllChallenges().forEach { challenge ->
                put(
                    CreatorManager.modifyItem(challenge.item, counter, lore),
                    CreatorManager.isActive(challenge.uuid)
                )
                counter++
            }
        }
    }
}
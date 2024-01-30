package de.miraculixx.mchallenge.utils.gui.items

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mvanilla.extensions.round
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

@Suppress("unused")
class ItemsSpecPlayer(private val owner: Player) : ItemProvider {
    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        return buildList {
            onlinePlayers.forEach { player ->
                if (player == owner) return@forEach
                add(itemStack(Material.PLAYER_HEAD) {
                    meta<SkullMeta> {
                        owningPlayer = player
                        name = cmp(player.name, cHighlight)
                        val world = player.world
                        lore(
                            listOf(
                                emptyComponent(),
                                cmp("∙ ") + cmp("Player Info", cHighlight, underlined = true),
                                cmp("  ∘ HP: ") + cmp(player.health.round(2).toString(), cMark),
                                cmp("  ∘ FOOD: ") + cmp(player.foodLevel.toString(), cMark),
                                cmp("  ∘ LEVEL: ") + cmp(player.level.toString(), cMark),
                                cmp("  ∘ WORLD: ") + cmp(world.name, cMark) + cmp(" (${world.environment.name.replace('_', ' ')})"),
                                emptyComponent(),
                                msgClick + cmp("Teleport")
                            )
                        )
                    }
                })
            }
        }
    }
}
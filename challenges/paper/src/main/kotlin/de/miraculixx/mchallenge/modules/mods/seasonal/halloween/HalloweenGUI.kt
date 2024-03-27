package de.miraculixx.mchallenge.modules.mods.seasonal.halloween

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.extensions.soundStone
import de.miraculixx.mvanilla.messages.cError
import de.miraculixx.mvanilla.messages.cmp
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class HalloweenGUIItem: ItemProvider {

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        return listOf(
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp("Vampire Class", cError, true)
                    lore(listOf(
                        cmp("Choose this class to live like a vampire!"),
                        cmp("You can transform into your bat form"),
                        cmp("and back. But be careful! Your"),
                        cmp("blood thirst is unstoppable...")
                    ))
                    customModel = 1
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI2YTk4ZDQwMzhlYWJhNDdlMDJlZWUxNTUxZGE5OTJhYTVhZDQ2NzA1YTc4MWY0NjE0NzA0MmQyOWNhZjEwNCJ9fX0=")
            },

            itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp("Werewolf Class", cError, true)
                    lore(listOf(
                        cmp("Choose this class to transform into a werewolf!"),
                        cmp("Your strength rises with the moon!"),
                        cmp("But keep a distance to water... It may"),
                        cmp("harm you more then you like")
                    ))
                    customModel = 2
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY4ZDQzMTI5MzliYjMxMTFmYWUyOGQ2NWQ5YTMxZTc3N2Y4ZjJjOWZjNDI3NTAxY2RhOGZmZTNiMzY3NjU4In19fQ==")
            }
        )
    }
}


class HalloweenGUILogic(private val challengeClass: HalloweenChallenge): GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        val item = it.currentItem
        val meta = item?.itemMeta ?: return@event
        val player = it.whoClicked as? Player ?: return@event
        it.isCancelled = true

        when (meta.customModel) {
            1 -> {
                challengeClass.playerClasses[player] = HalloweenClass.VAMPIRE
            }

            2 -> {
                challengeClass.playerClasses[player] = HalloweenClass.WEREWOLF
            }

            else -> {
                player.soundStone()
                return@event
            }
        }

        player.playSound(player, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)
        player.closeInventory()
    }
}
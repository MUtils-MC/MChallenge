package de.miraculixx.mutils.gui

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.msgList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object InventoryUtils {
    fun getCustomItem(key: String, id: Int, texture: Head64): ItemStack {
        return itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                skullTexture(texture.value)
                displayName(msg("gui.general.$key.n"))
                lore(msgList("gui.general.$key.l"))
                customModel = id
            }
        }
    }
}
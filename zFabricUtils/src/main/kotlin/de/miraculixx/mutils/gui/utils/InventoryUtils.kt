package de.miraculixx.mutils.gui.utils

import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.msgList
import de.miraculixx.mvanilla.messages.namespace
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setSkullTexture
import net.silkmc.silk.nbt.dsl.nbtCompound

object InventoryUtils {
    fun getCustomItem(key: String, id: Int, texture: Head64): ItemStack {
        return itemStack(Items.PLAYER_HEAD) {
            setSkullTexture(texture.value)
            setName(msg("items.general.$key.n"))
            setLore(msgList("items.general.$key.l", inline = "<grey>"))
            setID(id)
        }
    }

    fun ItemStack.setID(id: Int) {
        addTagElement(namespace, nbtCompound { put("ID", id) })
    }

    fun ItemStack.getID(): Int {
        return getTagElement(namespace)?.getInt("ID") ?: 0
    }

    fun ItemStack.clone(type: Item): ItemStack {
        return itemStack(type) {
            tag = this@clone.tag
        }
    }
}
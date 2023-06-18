package de.miraculixx.mutils.gui.utils

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.item.setLore

fun Player.adv() = this as Audience

fun Component.native() = adventure.toNative(this)

fun Collection<Component>.native() = map { adventure.toNative(it) }

fun ItemStack.setName(cmp: Component) {
    hoverName = cmp.native()
}

fun ItemStack.setLore(collection: Collection<Component>) {
    setLore(collection.native())
}

fun ItemStack.getLore(): List<net.minecraft.network.chat.Component> {
    return getTagElement("display")?.getList("Lore", 9)?.map { it as net.minecraft.network.chat.Component } ?: emptyList()
}
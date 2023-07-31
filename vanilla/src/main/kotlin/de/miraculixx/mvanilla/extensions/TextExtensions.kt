package de.miraculixx.mvanilla.extensions

import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.legacySerializer
import net.kyori.adventure.text.Component
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

fun Component.native() = legacySerializer.serialize(this)
fun List<Component>.native() = map { legacySerializer.serialize(it) }
fun ConsoleCommandSender.sendMessage(cmp: Component) {
    sendMessage(cmp.native())
}
fun Player.sendMessage(cmp: Component) {
    sendMessage(cmp.native())
}
fun ItemMeta.lore(list: List<Component>) {
    lore = list.native()
}
var ItemMeta.name: Component
    get() = cmp(this.displayName)
    set(cmp) = this.setDisplayName(cmp.native())
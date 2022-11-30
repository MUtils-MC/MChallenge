package de.miraculixx.mutils.utils.gui.item


import de.miraculixx.mutils.utils.messages.jsonSerializer
import net.kyori.adventure.text.Component
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.ItemLike
import java.util.*

/**
 * ItemBuilder by SilkMC -> https://github.com/SilkMC/silk
 */

/**
 * A utility function for building complex [item] stacks with the given [amount].
 *
 * ```kotlin
 * itemStack(Items.FEATHER, amount = 64) {
 *     setCustomName("Cool Feather")
 *     enchant(Enchantments.FIRE_ASPECT, 1)
 * }
 * ```
 */
inline fun itemStack(
    item: ItemLike,
    amount: Int = 1,
    builder: ItemStack.() -> Unit,
) = ItemStack(item, amount).apply(builder)

/**
 * Sets the item lore, which is displayed below the display
 * name of the item stack. Each element of the [text] collection represents
 * one line.
 */
fun ItemStack.setLore(text: Collection<Component>) {
    getOrCreateTagElement("display").put(
        "Lore",
        text.mapTo(ListTag()) { StringTag.valueOf(jsonSerializer.serialize(it)) }
    )
}

/**
 * Opens a [LiteralTextBuilder] to change the custom name of
 * the item stack. See [literalText].
 */
fun ItemStack.setName(name: Component?) {
    val compoundTag = getOrCreateTagElement("display")
    if (name != null) {
        compoundTag.putString("Name", jsonSerializer.serialize(name))
    } else {
        compoundTag.remove("Name")
    }
}

/**
 * Sets the given potion for this [ItemStack].
 *
 * If you want to pass a custom potion,
 * make sure to register it in the potion registry first.
 *
 * Example usage:
 * ```kotlin
 * itemStack(Items.POTION) {
 *     setPotion(Potions.HEALING)
 * }
 * ```
 */
fun ItemStack.setPotion(potion: Potion) {
    PotionUtils.setPotion(this, potion)
}

/**
 * Configures the `SkullOwner` nbt tag to have the given [texture].
 * The [texture] has to be base64 encoded.
 *
 * You can find a lot of heads with the associated base64 values on
 * [minecraft-heads.com](https://minecraft-heads.com/).
 *
 * Optional, you can specify a [uuid]. This is *not* necessary if this head
 * is just used because of its texture, but you should specify one if the head
 * is associated with an actual player.
 *
 * ```kotlin
 * skullStack.setSkullTexture(
 *     texture = "eyJ0ZXh0dXJlcyI6ey...", // base64 encoded texture json (this example is truncated)
 *     uuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5") // this is optional
 * )
 * ```
 */
fun ItemStack.setSkullTexture(
    texture: String,
    uuid: UUID = UUID(0, 0),
) {
    orCreateTag.put("SkullOwner", CompoundTag().apply {
        putUUID("Id", uuid)

        val propsCompound = if (this.contains("Properties"))
            this.getCompound("Properties")
        else
            CompoundTag().also { put("Properties", it) }

        propsCompound.put("textures", ListTag().apply {
            add(CompoundTag().apply {
                putString("Value", texture)
            })
        })
    })
}

/**
 * Configures the `SkullOwner` nbt tag to represent the given player
 * (specified via [uuid]). The [name] can be anything, but it *should* match
 * the actual player name.
 *
 * ```kotlin
 * skullStack.setSkullPlayer(server.playerList.getPlayerByName("Notch"))
 * ```
 */
fun ItemStack.setSkullPlayer(player: ServerPlayer) {
    player.gameProfile.properties.get("textures")
        .map { it.value }
        .forEach(::setSkullTexture)
}

fun ItemStack.setCustomModel(int: Int) {
    orCreateTag.putInt("CustomModelData", int)
}

fun ItemStack.getCustomModel(): Int {
    return orCreateTag.getInt("CustomModelData")
}

fun ItemStack.printJSONData() {
    println(item.toString() + "\n" + (tag?.asString ?: "No Tags"))
}

fun ItemStack.setPDCValue(key: String, value: String) {
    getOrCreateTagElement("PublicBukkitValues").putString("de.miraculixx.api:$key", value)
}

fun ItemStack.setPDCValue(key: String, value: Int) {
    getOrCreateTagElement("PublicBukkitValues").putInt("de.miraculixx.api:$key", value)
}

fun ItemStack.getPDCString(key: String): String? {
    return getTagElement("PublicBukkitValues")?.getString("de.miraculixx.api:$key")
}

fun ItemStack.getPDCInt(key: String): Int? {
    return getTagElement("PublicBukkitValues")?.getInt("de.miraculixx.api:$key")
}

fun ItemStack.getPDC(): CompoundTag {
    return getOrCreateTagElement("PublicBukkitValues")
}

fun ItemStack.copyAsMaterial(material: Item): ItemStack {
    val i = ItemStack(material, count)
    i.tag = tag
    return i
}

fun ItemStack.addHideFlags(flag: HideFlag) {
    orCreateTag.putInt("HideFlags", orCreateTag.getInt("HideFlags") + flag.value)
}

fun ItemStack.removeEnchantment(enchantment: Enchantment) {
    val listTag = orCreateTag.getList("Enchantments", 0)
    listTag.remove(EnchantmentHelper.storeEnchantment(
        EnchantmentHelper.getEnchantmentId(enchantment),
        EnchantmentHelper.getItemEnchantmentLevel(enchantment, this)
    ))
    tag?.put("Enchantments", listTag)
}
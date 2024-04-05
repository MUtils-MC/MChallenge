package de.miraculixx.mchallenge.modules.mods.seasonal.halloween

import de.miraculixx.kpaper.items.*
import de.miraculixx.mcommons.text.cmp
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class HalloweenItems {
    val nightVisionGogglesID = 10_001
    fun nightVisionGoggles() = itemStack(Material.LEATHER_HELMET) {
        meta {
            isUnbreakable = true
            customModel = nightVisionGogglesID
            name = cmp("Night Vision Goggles")
            addEnchant(Enchantment.MENDING, 1, true)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    val silverArrowID = 10_002
    fun silverArrow() = itemStack(Material.SPECTRAL_ARROW) {
        meta {
            customModel = silverArrowID
            name = cmp("Silver Tip Arrow")
        }
    }

    val silverSwordID = 10_003
    fun silverSword() = itemStack(Material.IRON_SWORD) {
        meta {
            customModel = silverSwordID
            name = cmp("Silver Sword")
        }
    }

    val pflockID = 10_004
    fun pflock() = itemStack(Material.STICK) {
        meta {
            customModel = pflockID
            name = cmp("Wooden Peg")
        }
    }

    val candyID = 10_005
    fun candy() = itemStack(Material.COOKIE) {
        meta {
            customModel = candyID
            name = cmp("Candy")
        }
    }

    val rocketID = 10_006
    fun rocket() = fireworkItemStack {
        customModel = rocketID
        power = 1
        name = cmp("Piñata")
        addEffect(
            FireworkEffect.builder()
                .withColor(Color.PURPLE).withFade(Color.LIME)
                .flicker(true)
                .with(FireworkEffect.Type.CREEPER)
                .trail(true).build()
        )
    }


    fun <T> addItemsToCommand(argument: Argument<T>) {
        argument.literalArgument("nightvisiongoggles") {
            addItem(nightVisionGoggles())
        }

        argument.literalArgument("silverarrow") {
            addItem(silverArrow())
        }

        argument.literalArgument("silversword") {
            addItem(silverSword())
        }

        argument.literalArgument("woodenpeg") {
            addItem(pflock())
        }

        argument.literalArgument("candy") {
            addItem(candy())
        }

        argument.literalArgument("pinata") {
            addItem(rocket())
        }
    }

    private fun <T> Argument<T>.addItem(itemStack: ItemStack) {
        playerExecutor { player, _ ->
            player.inventory.addItem(itemStack)
        }
    }
}
package de.miraculixx.mutils.utils.gui

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.challengeOfTheMonth
import de.miraculixx.mutils.utils.premium
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundError
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemMeta
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

fun Inventory.fillPlaceholder(): Inventory {
    for (i in 0 until size) {
        setItem(i, InvUtils.primaryPlaceholder)
    }
    if (size != 9) {
        setItem(17, InvUtils.secondaryPlaceholder)
        setItem(size - 18, InvUtils.secondaryPlaceholder)
        repeat(2) { setItem(it, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(it + 7, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(size - it - 8, InvUtils.secondaryPlaceholder) }
        repeat(2) { setItem(size - it - 1, InvUtils.secondaryPlaceholder) }
    } else {
        setItem(0, InvUtils.secondaryPlaceholder)
        setItem(8, InvUtils.secondaryPlaceholder)
    }
    return this
}

fun PersistentDataContainer.getOrNull(key: NamespacedKey): String? {
    return if (has(key)) get(key, PersistentDataType.STRING)
    else null
}

object InvUtils {
    val primaryPlaceholder: ItemStack
    val secondaryPlaceholder: ItemStack

    fun getIndicator(key: String, inventory: Inventory, slot: Int = 0): String? {
        return getIndicator(key, inventory.getItem(slot) ?: return null)
    }

    fun getIndicator(key: String, item: ItemStack): String? {
        return item.itemMeta?.persistentDataContainer?.get(NamespacedKey(Manager, key), PersistentDataType.STRING)
    }

    fun <T> enumRotate(enum: Array<T>, current: T): T {
        val currentValue = enum.lastIndexOf(current)
        val lastValue = enum.size - 1
        return if (currentValue < lastValue) enum[currentValue + 1]
        else enum[0]
    }

    fun navigate(p: Player, i: Int, g: GUI, s: GUIState, items: Map<ItemStack, Boolean>? = null) {
        val b = GUIBuilder(p, g)
        if (s == GUIState.SCROLL) b.scroll(i, items) else b.storage(null, items)
        b.open()
        p.click()
    }

    fun getCurrentFilter(item: ItemStack): StorageFilter {
        val filterString = item.itemMeta.persistentDataContainer.get(NamespacedKey.fromString("gui.storage.filter", Manager)!!, PersistentDataType.STRING)
        return try {
            StorageFilter.valueOf(filterString ?: "NO_FILTER")
        } catch (_: IllegalArgumentException) {
            StorageFilter.NO_FILTER
        }
    }

    fun colorRotate(code: Char?): Char {
        return when (code) {
            '0' -> '1'
            '1' -> '2'
            '2' -> '3'
            '3' -> '4'
            '4' -> '5'
            '5' -> '6'
            '6' -> '7'
            '7' -> '8'
            '8' -> '9'
            '9' -> 'a'
            'a' -> 'b'
            'b' -> 'c'
            'c' -> 'd'
            'd' -> 'e'
            'e' -> 'f'
            'f' -> '0'
            else -> '6'
        }
    }

    fun styleRotate(code: Char?): Char {
        return when (code) {
            'k' -> 'l'
            'l' -> 'm'
            'm' -> 'n'
            'n' -> 'o'
            'o' -> 'k'
            else -> 'l'
        }
    }

    fun toggleSetting(c: FileConfiguration, p: Player, s: String) {
        if (c.getBoolean(s)) {
            c[s] = false
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
        } else {
            c[s] = true
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }
    }

    fun numberChanger(c: FileConfiguration, p: Player, t: ClickType, s: String, step: Int = 1, min: Int = 0, max: Int = 100) {
        val b = when (t) {
            ClickType.LEFT -> true
            ClickType.RIGHT -> false
            else -> return
        }
        settings(c, b, p, s, step.toDouble(), max.toDouble(), min.toDouble())
    }

    fun numberChangerShift(c: FileConfiguration, p: Player, t: ClickType, s: String, step: Int = 1, min: Int = 0, max: Int = 100) {
        val b = when (t) {
            ClickType.RIGHT -> true
            ClickType.SHIFT_RIGHT -> false
            else -> return
        }
        settings(c, b, p, s, step.toDouble(), max.toDouble(), min.toDouble())
    }

    fun numberChangerShift(c: FileConfiguration, p: Player, t: ClickType, s: String, step: Double = 1.0, min: Double = 0.0, max: Double = 100.0) {
        val b = when (t) {
            ClickType.RIGHT -> true
            ClickType.SHIFT_RIGHT -> false
            else -> return
        }
        settings(c, b, p, s, step, max, min)
    }

    private fun settings(c: FileConfiguration, up: Boolean, p: Player, s: String, step: Double, max: Double, min: Double) {
        if (up) {
            if (c.getDouble(s) >= max) {
                p.playSound(p.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                return
            }
            c[s] = c.getDouble(s) + step
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.2f)
        } else {
            if (c.getDouble(s) <= min) {
                c[s] = min
                p.playSound(p.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                return
            }
            c[s] = c.getDouble(s) - step
            p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 0.5f)
        }
    }

    fun verify(module: Modules, player: Player?): Boolean {
        return if (!premium && module != challengeOfTheMonth) {
            player?.closeInventory()
            player?.soundError()
            player?.sendMessage(msg("command.verify.noPremium"))
            false
        } else true
    }

    init {
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val mat1 = Material.getMaterial(config.getString("Placeholder 1") ?: "") ?: Material.GRAY_STAINED_GLASS_PANE
        val mat2 = Material.getMaterial(config.getString("Placeholder 2") ?: "") ?: Material.BLACK_STAINED_GLASS_PANE
        val meta = itemMeta(mat1) {
            name = " "
            customModel = 200
        }
        primaryPlaceholder = itemStack(mat1) { itemMeta = meta }
        secondaryPlaceholder = itemStack(mat2) { itemMeta = meta }
    }
}
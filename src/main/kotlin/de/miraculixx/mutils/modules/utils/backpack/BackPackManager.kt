package de.miraculixx.mutils.modules.utils.backpack

import de.miraculixx.mutils.utils.gui.items.skullTexture
import de.miraculixx.mutils.utils.text.cropColor
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class BackPackManager(private val config: FileConfiguration) {

    fun getBackPack(player: OfflinePlayer? = null): Inventory {
        val name = player?.name ?: "Global"
        val size = config.getInt("Backpack Size") * 9
        val inv = Bukkit.createInventory(null, size + 9, "§9$name Backpack")
        var ph = itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta {
            customModel = 111
            this.name = " "
        }}
        repeat(9) { inv.setItem(it, ph) }

        ph = if (player != null) itemStack(Material.PLAYER_HEAD) { meta<SkullMeta> {
            customModel = 111
            this.name = "§9§l$name"
            owningPlayer = player
        }}
        else itemStack(Material.PLAYER_HEAD) { meta<SkullMeta> {
            customModel = 111
            this.name = "§9§l$name"
        }
            itemMeta = skullTexture(itemMeta as SkullMeta,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0MDk0MmYzNjRmNmNiY2VmZmNmMTE1MTc5NjQxMDI4NmE0OGIxYWViYTc3MjQzZTIxODAyNmMwOWNkMSJ9fX0=")
        }
        inv.setItem(4, ph)
        repeat(size) { inv.setItem(it + 9, config.getItemStack("$name.${it + 9}")) }
        return inv
    }

    fun saveInv(inventory: Inventory): String {
        val name = inventory.getItem(4)!!.itemMeta?.displayName!!.cropColor()

        repeat(config.getInt("Backpack Size")) { i ->
            val item = inventory.getItem(i + 9)
            config["$name.${i + 9}"] = item ?: ItemStack(Material.AIR)
        }
        val list = config.getStringList("Backpacks")
        if (!list.contains(name)) list.add(name)
        config["Backpacks"] = list
        return name
    }
}
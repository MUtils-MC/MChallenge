package de.miraculixx.mutils.utils.tools.gui.items

import de.miraculixx.mutils.enums.settings.gui.StorageFilters
import de.miraculixx.mutils.modules.spectator.SpecCollection
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemLib {

    fun getChallenge(id: Int, filter: StorageFilters?): LinkedHashMap<ItemStack, Boolean> {
        return ItemsChallenge(ConfigManager.getConfig(Configs.MODULES)).getItems(id, filter)
    }

    fun getSetting(id: Int, p: Player? = null): List<ItemStack> {
        return ItemsSettings().getItems(id, ConfigManager.getConfig(Configs.MODULES), p)
    }

    fun getTimer(id: Int, zeit: String? = null, title: String? = null): Map<ItemStack, Int> {
        return ItemsTimer().getItems(id, zeit, title)
    }

    fun getTimerRules(id: Int, c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        return ItemsTimerSettings().getItems(id, c)
    }

    fun getMain(id: Int): Map<ItemStack,Int> {
        return ItemsMainMenu().getItems(id)
    }

    fun getSpec(id: Int, s: SpecCollection? = null, b: Boolean? = null): Map<ItemStack?, Int> {
        return ItemsSpectator().getItems(id, s, b)
    }

    fun getSpeedrun(id: Int, c: FileConfiguration): LinkedHashMap<ItemStack, Boolean> {
        return ItemsSpeedrun().getItems(id, c)
    }

    fun getWorld(id: Int, player: Player? = null, c: FileConfiguration? = null): LinkedHashMap<ItemStack, Boolean> {
        return ItemsWorld().getItems(id, player, c)
    }

    fun getServerSettings(id: Int, c: FileConfiguration?): Map<ItemStack, Int> {
        return ItemsServerSettings().getItems(id, c)
    }

    //Utility
    fun getTopItem(id: Int): ItemStack {
        return ItemsTopItem().getItems(id)
    }
}
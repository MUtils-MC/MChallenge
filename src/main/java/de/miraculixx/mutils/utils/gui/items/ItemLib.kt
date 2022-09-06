package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.spectator.SpecCollection
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemLib {

    fun getChallenge(id: Int, filter: StorageFilter?): LinkedHashMap<ItemStack, Boolean> {
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

    fun getCreator(id: Int, data: CustomChallengeData, eventData: CreatorEvent? = null): Map<ItemStack, Int> {
        return CreatorItems().getItems(id, data, eventData)
    }

    fun getKeyed(id: Int, key: String): List<ItemStack> {
        return KeyedItems().getItems(id, key)
    }

    //Utility
    fun getTopItem(id: Int): ItemStack {
        return ItemsTopItem().getItems(id)
    }
}
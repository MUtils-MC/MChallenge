package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.data.GeneratorData
import de.miraculixx.mutils.data.WorldData
import de.miraculixx.mutils.data.enums.GeneratorAlgorithm
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.extensions.soundEnable
import de.miraculixx.mutils.extensions.soundError
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.namespace
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class GUINoiseAlgo(previousInv: CustomInventory, worldData: WorldData) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, _: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta ?: return@event

        if (meta.customModel == 1) {
            val noise = item.itemMeta?.persistentDataContainer?.get(NamespacedKey(namespace, "gui.wc.noise"), PersistentDataType.STRING)
            val noiseAlgo = enumOf<GeneratorAlgorithm>(noise)
            if (noiseAlgo == null) player.soundError()
            else {
                worldData.chunkProviders.add(GeneratorData(noiseAlgo))
                player.soundEnable()
            }

        } else player.click()

        previousInv.update()
        previousInv.open(player)
    }
}
package de.miraculixx.mutils.utils.gui.event

import de.miraculixx.mutils.utils.gui.data.CustomInventory
import net.minecraft.world.entity.player.Player

class GUICloseEvent(
    val gui: CustomInventory,
    val player: Player,
)
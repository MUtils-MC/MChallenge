package de.miraculixx.mutils.gui.event

import de.miraculixx.mutils.gui.data.CustomInventory
import net.minecraft.world.entity.player.Player

class GUICloseEvent(
    val gui: CustomInventory,
    val player: Player,
)
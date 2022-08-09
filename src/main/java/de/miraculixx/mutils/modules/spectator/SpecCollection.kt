package de.miraculixx.mutils.modules.spectator

import de.miraculixx.mutils.enums.settings.spectator.BlockBreak
import de.miraculixx.mutils.enums.settings.spectator.Hide
import de.miraculixx.mutils.enums.settings.spectator.ItemPickup
import de.miraculixx.mutils.enums.settings.spectator.SelfHide

class SpecCollection {
    var hide = Hide.HIDDEN
    var selfHide = SelfHide.SHOWN
    var itemPickup = ItemPickup.DISABLED
    var blockBreak = BlockBreak.DISABLED
    var flySpeed = 1
}
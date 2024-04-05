package de.miraculixx.mutils.gui.data

import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.event.GUICloseEvent

interface GUIEvent {
    val run: (GUIClickEvent, CustomInventory) -> Unit
    val close: ((GUICloseEvent, CustomInventory) -> Unit)?
        get() = null
}
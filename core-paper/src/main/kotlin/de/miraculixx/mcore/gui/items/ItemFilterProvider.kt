package de.miraculixx.mcore.gui.items

import de.miraculixx.mvanilla.gui.StorageFilter

interface ItemFilterProvider: ItemProvider {
    var filter: StorageFilter
}
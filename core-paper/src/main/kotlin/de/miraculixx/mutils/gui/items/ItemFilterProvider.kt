package de.miraculixx.mutils.gui.items

import de.miraculixx.mutils.gui.StorageFilter

interface ItemFilterProvider: ItemProvider {
    var filter: StorageFilter
}
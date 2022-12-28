package de.miraculixx.mutils.gui.items

import de.miraculixx.mutils.enums.gui.StorageFilter

interface ItemFilterProvider: ItemProvider {
    var filter: StorageFilter
}
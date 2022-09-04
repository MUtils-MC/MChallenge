package de.miraculixx.mutils.utils.gui

import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
class GUITransitions(private val player: Player, private val title: String) {

    fun moveLeft(oldInventory: Inventory, newInventory: Inventory) {
        val inventory = Bukkit.createInventory(null, newInventory.size, title)
        val rows = newInventory.size / 9
        player.openInventory(inventory)
        val segments = buildList {
            addAll(getSegments(SegmentType.COLUMN, 0, oldInventory, newInventory.size - oldInventory.size))
            addAll(getSegments(SegmentType.COLUMN, 9, newInventory, 0))
        }

        task(false, 0, 1, 9) {
            segments.forEach { segment ->
                segment.index -= 1
                if (segment.index in 0..8) {
                    segment.items.forEachIndexed { row, itemStack ->
                        if (row > (rows - 1)) return@forEachIndexed
                        inventory.setItem(segment.index + (row * 9), itemStack)
                    }
                }
            }
        }
    }

    fun moveRight(oldInventory: Inventory, newInventory: Inventory) {
        val inventory = Bukkit.createInventory(null, newInventory.size, title)
        val rows = newInventory.size / 9
        player.openInventory(inventory)
        val segments = buildList {
            addAll(getSegments(SegmentType.COLUMN, 0, oldInventory, newInventory.size - oldInventory.size))
            addAll(getSegments(SegmentType.COLUMN, -9, newInventory, 0))
        }

        task(false, 0, 1, 9) {
            segments.forEach { segment ->
                segment.index += 1
                if (segment.index in 0..8) {
                    segment.items.forEachIndexed { row, itemStack ->
                        if (row > (rows - 1)) return@forEachIndexed
                        inventory.setItem(segment.index + (row * 9), itemStack)
                    }
                }
            }
        }
    }

    fun moveUp(oldInventory: Inventory, newInventory: Inventory) {
        val inventory = Bukkit.createInventory(null, newInventory.size, title)
        player.openInventory(inventory)
        val rows = newInventory.size / 9
        val segments = buildList {
            addAll(getSegments(SegmentType.ROW, 0, oldInventory, 0))
            addAll(getSegments(SegmentType.ROW, rows, newInventory, 0))
        }

        task(false, 0, 1, rows.toLong()) {
            segments.forEach { segment ->
                segment.index -= 1
                if (segment.index in 0 until rows) {
                    segment.items.forEachIndexed { column, itemStack ->
                        inventory.setItem(column + (segment.index * 9), itemStack)
                    }
                }
            }
        }
    }

    fun moveDown(oldInventory: Inventory, newInventory: Inventory) {
        val inventory = Bukkit.createInventory(null, newInventory.size, title)
        player.openInventory(inventory)
        val rows = newInventory.size / 9

        val segments = buildList {
            addAll(getSegments(SegmentType.ROW, 0, oldInventory, 0))
            addAll(getSegments(SegmentType.ROW, rows - (rows * 2), newInventory, 0))
        }

        task(false, 0, 1, rows.toLong()) {
            segments.forEach { segment ->
                segment.index += 1
                if (segment.index in 0 until rows) {
                    segment.items.forEachIndexed { column, itemStack ->
                        inventory.setItem(column + (segment.index * 9), itemStack)
                    }
                }
            }
        }
    }

    /**
     * Get the Inventory splittet in Segments
     * @param type Column Inventorys are spliced into '9' Segments. Row Inventorys are spliced into 'row-count' Segments.
     * @param startIndex Old Inventory starts by 0 - New Inventory starts before or after old inv index
     * @param inventory Spliced Inventory
     * @return Segments for this Inventory
     */
    private fun getSegments(type: SegmentType, startIndex: Int, inventory: Inventory, sizeOffset: Int): List<Segment> {
        val rows = inventory.size / 9
        val ph = InvUtils.secondaryPlaceholder
        val addons = (sizeOffset / 9).coerceIn(0..6)
        return buildList {
            if (type == SegmentType.COLUMN) {
                repeat(9) { column ->
                    add(Segment(column + startIndex, buildList {
                        repeat(rows) { row ->
                            add(inventory.getItem(column + (row * 9)) ?: ph)
                        }
                        repeat(addons) { add(ph) }
                    }))
                }
            } else {
                repeat(rows) { row ->
                    add(Segment(row + startIndex, buildList {
                        repeat(9) { column ->
                            add(inventory.getItem(column + (row * 9)) ?: ph)
                        }
                        repeat(addons) { add(ph) }
                    }))
                }
            }
        }
    }

    enum class SegmentType {
        ROW, COLUMN
    }

    data class Segment(var index: Int, val items: List<ItemStack>)
}
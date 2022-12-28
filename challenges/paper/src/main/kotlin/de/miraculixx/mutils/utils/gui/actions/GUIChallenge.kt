package de.miraculixx.mutils.utils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.enums.Challenges.*
import de.miraculixx.mutils.enums.gui.StorageFilter
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.extensions.enumRotate
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.InventoryUtils.get
import de.miraculixx.mutils.gui.InventoryUtils.numberChangerShift
import de.miraculixx.mutils.gui.InventoryUtils.toggle
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemFilterProvider
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.utils.settings
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUIChallenge : GUIEvent {
    private val validFilters = arrayOf(
        StorageFilter.NO_FILTER,
        StorageFilter.FUN,
        StorageFilter.MEDIUM,
        StorageFilter.HARD,
        StorageFilter.COMPLEX,
        StorageFilter.FORCE,
        StorageFilter.RANDOMIZER,
        StorageFilter.MULTIPLAYER
        )

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta
        val click = it.click

        when (meta?.customModel) {
            9005 -> {
                val current = meta.persistentDataContainer.get(NamespacedKey(namespace, "gui.storage.filter")) ?: return@event
                val currentFilter = enumOf<StorageFilter>(current) ?: return@event
                val newFilter = validFilters.enumRotate(currentFilter)
                (inv.itemProvider as? ItemFilterProvider)?.filter = newFilter
                player.click()
                inv.update()
            }
        }

        val challengeKey = meta?.persistentDataContainer.get(NamespacedKey(namespace, "gui.challenge")) ?: return@event
        val challenge = enumOf<Challenges>(challengeKey) ?: return@event
        when (challenge) {
            //Settings names are hardcoded to make the String static (saves 0.00001% performance)
            //and prevent config issues on enum renames
            FLY -> if (click == ClickType.LEFT) settings.toggle("FLY.active", inv, player)
            else settings.numberChangerShift(player, click, "FLY.power", inv, 0.1, 0.2, 1.0)

            IN_TIME -> if (click == ClickType.LEFT) settings.toggle("IN_TIME.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            MOB_BLOCKS -> if (click == ClickType.LEFT) settings.toggle("MOB_RANDOMIZER.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("MOB_RANDOMIZER.rnd", inv, player)

            CHECKPOINTS -> if (click == ClickType.LEFT) settings.toggle("CHECKPOINTS.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("CHECKPOINTS.onlyTP", inv, player)

            DIM_SWAP -> if (click == ClickType.LEFT) settings.toggle("DIM_SWAP.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("DIM_SWAP.starter", inv, player)

            SNAKE -> if (click == ClickType.LEFT) settings.toggle("SNAKE.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "SNAKE.speed", inv, 1, 1, 20)

            REALISTIC -> if (click == ClickType.LEFT) settings.toggle("REALISTIC.active", inv, player)

            CAPTIVE -> if (click == ClickType.LEFT) settings.toggle("CAPTIVE.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            GHOST -> if (click == ClickType.LEFT) settings.toggle("GHOST.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            BLOCK_ASYNC -> if (click == ClickType.LEFT) settings.toggle("BLOCK_ASYNC.active", inv, player)

            NO_SAME_ITEM -> if (click == ClickType.LEFT) settings.toggle("NO_SAME_ITEM.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            LIMITED_SKILLS -> if (click == ClickType.LEFT) settings.toggle("LIMITED_SKILLS.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("LIMITED_SKILLS.random", inv, player)

            RUN_RANDOMIZER -> if (click == ClickType.LEFT) settings.toggle("RUN_RANDOMIZER.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "RUN_RANDOMIZER.goal", inv, 25, 25, 5000)

            SPLIT_HP -> if (click == ClickType.LEFT) settings.toggle("SPLIT_HP.active", inv, player)

            DAMAGE_DUELL -> if (click == ClickType.LEFT) settings.toggle("DAMAGE_DUELL.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "DAMAGE_DUELL.percent", inv, 5, 5, 100)

            ONE_BIOME -> if (click == ClickType.LEFT) settings.toggle("ONE_BIOME.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "ONE_BIOME.delay", inv, 15, 30, 6000)

            BOOST_UP -> if (click == ClickType.LEFT) settings.toggle("BOOST_UP.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            RIGHT_TOOL -> if (click == ClickType.LEFT) settings.toggle("RIGHT_TOOL.active", inv, player)

            CHUNK_BLOCK_BREAK -> if (click == ClickType.LEFT) settings.toggle("CHUNK_BLOCK_BREAK.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("CHUNK_BLOCK_BREAK.bundle", inv, player)

            SNEAK_SPAWN -> if (click == ClickType.LEFT) settings.toggle("SNEAK_SPAWN.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("SNEAK_SPAWN.onlyMob", inv, player)

            WORLD_PEACE -> if (click == ClickType.LEFT) settings.toggle("WORLD_PEACE.active", inv, player)

            GRAVITY -> if (click == ClickType.LEFT) settings.toggle("ONE_BIOME.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "ONE_BIOME.delay", inv, 15, 30, 6000)

            STAY_AWAY -> if (click == ClickType.LEFT) settings.toggle("STAY_AWAY.active", inv, player)
            else if (click == ClickType.RIGHT) settings.numberChangerShift(player, click, "STAY_AWAY.distance", inv, 0.5, 0.5, 10.0)

            RANDOMIZER_BLOCK -> if (click == ClickType.LEFT) settings.toggle("RANDOMIZER_BLOCK.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("RANDOMIZER_BLOCK.random", inv, player)

            RANDOMIZER_ENTITY -> if (click == ClickType.LEFT) settings.toggle("RANDOMIZER_ENTITY.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("RANDOMIZER_ENTITY.random", inv, player)

            RANDOMIZER_BIOMES -> if (click == ClickType.LEFT) settings.toggle("RANDOMIZER_BIOMES.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("RANDOMIZER_BIOMES.random", inv, player)

            RANDOMIZER_MOBS -> if (click == ClickType.LEFT) settings.toggle("RANDOMIZER_MOBS.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("RANDOMIZER_MOBS.random", inv, player)

            FORCE_COLLECT -> if (click == ClickType.LEFT) settings.toggle("FORCE_COLLECT.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            RANDOMIZER_DAMAGE -> if (click == ClickType.LEFT) settings.toggle("RANDOMIZER_DAMAGE.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("RANDOMIZER_DAMAGE.random", inv, player)

            NO_DOUBLE_KILL -> if (click == ClickType.LEFT) settings.toggle("NO_DOUBLE_KILL.active", inv, player)
            else if (click == ClickType.RIGHT) settings.toggle("NO_DOUBLE_KILL.global", inv, player)

            DAMAGER -> if (click == ClickType.LEFT) settings.toggle("DAMAGER.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()

            RIVALS_COLLECT -> if (click == ClickType.LEFT) settings.toggle("RIVALS_COLLECT.active", inv, player)
            else if (click == ClickType.RIGHT) TODO()
        }
    }
}

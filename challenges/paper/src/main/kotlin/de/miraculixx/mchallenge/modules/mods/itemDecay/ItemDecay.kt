package de.miraculixx.mchallenge.modules.mods.itemDecay

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mvanilla.extensions.toUUID
import de.miraculixx.mchallenge.modules.challenges.InternalTimer
import de.miraculixx.mchallenge.modules.challenges.getFormatted
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ItemDecay : Challenge {
    private val startTime: Int
    private val itemTimers: MutableMap<UUID, InternalTimer> = mutableMapOf()
    private val timerNamespace = NamespacedKey(namespace, "challenge.item_decay.timer")
    private val msgTimeDisplay = cmp(msgString("event.itemDecay.time"), cHighlight) + cmp(" » ")

    private var running = true
    private var stopped = false

    init {
        val settings = challenges.getSetting(Challenges.ITEM_DECAY).settings
        startTime = settings["time"]?.toInt()?.getValue() ?: 300
    }

    override fun register() {
        onClick.register()
        onCollect.register()
        onSlotSwitch.register()
        itemTimers.forEach { (_, timer) -> timer.running = true }
        running = true
    }

    override fun unregister() {
        onClick.unregister()
        onCollect.unregister()
        onSlotSwitch.unregister()
        itemTimers.forEach { (_, timer) -> timer.running = false }
        running = false
    }

    override fun stop() {
        running = false
        stopped = true
        itemTimers.forEach { (_, timer) ->
            timer.running = false
            timer.stopped = true
        }
        itemTimers.clear()
    }

    private val onCollect = listen<EntityPickupItemEvent>(priority = EventPriority.HIGHEST, register = false) {
        if (it.isCancelled) return@listen
        val entity = it.entity
        if (entity !is Player) return@listen
        val item = it.item.itemStack
        if (item.type.isAir) return@listen
        val data = item.itemMeta.persistentDataContainer
        val id = data.get(timerNamespace, PersistentDataType.STRING)
        if (id != null) {
            val uuid = id.toUUID()
            val timer = itemTimers[uuid]?.time ?: return@listen
            item.lore(listOf(msgTimeDisplay + timer.calcLore(timer.inWholeSeconds)))
        } else item.registerTimer()
    }

    private val onClick = listen<InventoryClickEvent>(priority = EventPriority.HIGHEST, register = false) {
        if (it.isCancelled) return@listen
        if (it.whoClicked !is Player) return@listen
        val item = it.currentItem
        if (item == null || item.type.isAir) return@listen
        item.registerTimer()
    }

    private val onSlotSwitch = listen<PlayerItemHeldEvent>(priority = EventPriority.HIGHEST, register = false) {
        if (it.isCancelled) return@listen
        val player = it.player
        val item = player.inventory.getItem(it.newSlot) ?: return@listen
        if (item.type.isAir) return@listen
        item.lore(listOf(msgTimeDisplay + cmp(msgString("event.itemDecay.paused"), cError, italic = true)))
    }

    val scheduler = task(false, 0, 20) {
        val loadedWorlds = hashSetOf<World>()
        onlinePlayers.forEach { player ->
            loadedWorlds.add(player.world)
            player.inventory.proceedInv(player, true)
            player.openInventory.topInventory.proceedInv(player, false)
        }
        loadedWorlds.forEach { world ->
            sync {
                world.entities.forEach entities@{ e ->
                    async {
                        if (e !is Item) return@async
                        val id = e.itemStack.itemMeta.persistentDataContainer.get(timerNamespace, PersistentDataType.STRING) ?: return@async
                        val uuid = id.toUUID() ?: return@async
                        val timer = itemTimers[uuid] ?: return@async
                        val time = timer.time.inWholeSeconds
                        if (time == -1L) {
                            sync { e.remove() }
                            timer.running = false
                            timer.stopped = true
                            itemTimers.remove(uuid)
                        }
                    }
                }
            }
        }
    }

    private fun Inventory.proceedInv(player: Player, isOwn: Boolean) {
        forEachIndexed { index, slot ->
            if (slot == null || slot.type.isAir) return@forEachIndexed
            val id = slot.itemMeta.persistentDataContainer.get(timerNamespace, PersistentDataType.STRING)
            if (id == null) {
                slot.registerTimer()
                return@forEachIndexed
            }
            val uuid = id.toUUID() ?: return@forEachIndexed
            val timer = itemTimers[uuid] ?: return@forEachIndexed
            val time = timer.time
            val seconds = time.inWholeSeconds
            if (seconds == -1L) {
                player.inventory.setItem(index, null)
                return@forEachIndexed
            }
            if (isOwn && player.inventory.heldItemSlot == index) return@forEachIndexed // Prevent Item Update
            val message = time.calcLore(seconds)
            slot.lore(listOf(msgTimeDisplay + message))
        }
    }

    private fun Duration.calcLore(seconds: Long): Component {
        return when (seconds) {
            in 0..30 -> cmp(getFormatted(), NamedTextColor.RED)
            in 30..120 -> cmp(getFormatted(), NamedTextColor.YELLOW)
            else -> cmp(getFormatted(), NamedTextColor.GREEN)
        }
    }

    private fun ItemStack.registerTimer() {
        val meta = itemMeta
        val data = meta.persistentDataContainer
        if (!data.has(timerNamespace)) {
            val timer = InternalTimer(startTime.seconds, {}) { _, _ -> }
            val uuid = UUID.randomUUID()
            data.set(timerNamespace, PersistentDataType.STRING, uuid.toString())
            val time = timer.time
            lore(listOf(msgTimeDisplay + time.calcLore(startTime.toLong())))
            itemMeta = meta
            itemTimers[uuid] = timer
        }
    }
}
package de.miraculixx.mutils.modules.mods.itemDecay

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mutils.extensions.toUUID
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.challenges.InternalTimer
import de.miraculixx.mutils.modules.challenges.getTime
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class ItemDecay: Challenge {
    override val challenge: Challenges = Challenges.ITEM_DECAY
    private val startTime: Int
    private val itemTimers: MutableMap<UUID, ItemStack> = mutableMapOf()
    private val timerNamespace = NamespacedKey(namespace, "challenge.ITEM_DECAY.timer")
    private val msgTimeDisplay = cmp(msgString("event.itemDecay"), cHighlight) + cmp(" » ")

    init {
        val settings = challenges.getSetting(Challenges.ITEM_DECAY).settings
        startTime = settings["time"]?.toInt()?.getValue() ?: 300
    }

    override fun register() {
        onClick.register()
        onCollect.register()
    }

    override fun unregister() {
        onClick.unregister()
        onCollect.unregister()
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        val entity = it.entity
        if (entity !is Player) return@listen
        it.item.itemStack.registerTimer()
    }

    private val onClick = listen<InventoryClickEvent>(priority = EventPriority.HIGHEST, register = false) {
        if (it.isCancelled) return@listen
        if (it.whoClicked !is Player) return@listen
        it.currentItem?.registerTimer()
    }

    private fun ItemStack.registerTimer() {
        editMeta {
            val data = it.persistentDataContainer
            if (!data.has(timerNamespace)) {
                InternalTimer(startTime.seconds, { task ->
                    val uuid = data.get(timerNamespace, PersistentDataType.STRING)?.toUUID()
                    type = Material.AIR
                    task.cancel()
                    uuid?.let { id -> itemTimers.remove(id) }
                }) { _, time ->
                    val message = when (time.inWholeSeconds) {
                        in 0..30 -> cmp(time.getTime(), NamedTextColor.RED)
                        in 30..120 -> cmp(time.getTime(), NamedTextColor.YELLOW)
                        else -> cmp(time.getTime(), NamedTextColor.GREEN)
                    }
                    lore(listOf(msgTimeDisplay + message))
                }
                val uuid = UUID.randomUUID()
                data.set(timerNamespace, PersistentDataType.STRING, uuid.toString())
                itemTimers[uuid] = this
            }
        }
    }
}
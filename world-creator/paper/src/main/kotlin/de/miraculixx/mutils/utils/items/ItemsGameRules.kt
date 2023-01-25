package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.extensions.bukkit.render
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mutils.globalRules
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemsGameRules(private val world: World?) : ItemProvider {
    private val infoLore = listOf(emptyComponent(), cmp("• ") + cmp("Info", cHighlight, underlined = true))
    private val settingLore = listOf(emptyComponent(), cmp("• ") + cmp("Setting", cHighlight, underlined = true))
    private val overrideLore = cmp("Key '", cHighlight) + Component.keybind("key.swapOffhand") + cmp("'", cHighlight) + cmp(" ≫ Toggle Override")
    private val numberLore = listOf(msgClickLeft + cmp("+1"), msgShiftClickLeft + cmp("+50"), msgClickRight + cmp("-1"), msgShiftClickRight + cmp("-50"))
    private val boolLore = msgClick + cmp("Toggle Value")
    private val local = getLocal()

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            val isGlobal = world == null
            val gameRules = GameRule.values().toList()
            gameRules.subList(from, to.coerceAtMost(gameRules.size - 1)).forEach {
                val value = if (isGlobal) globalRules[it.name] else world?.getGameRuleValue(it)
                val isSet = value != null

                put(itemStack(getIcon(it)) {
                    meta {
                        name = cmp(fancyName(it.name), cHighlight)
                        customModel = 1
                        persistentDataContainer.set(NamespacedKey(namespace, "gui.gamerules.key"), PersistentDataType.STRING, it.name)
                        lore(listOf(cmp("Key: ${it.name}", NamedTextColor.DARK_GRAY))
                                + infoLore
                                + getTranslated(it.translationKey())
                                + settingLore
                                + buildList {
                                    if (isGlobal) add(cmp("   Override: ") + cmp(if (isSet) msgTrue else msgFalse, cHighlight))
                                    add(cmp("   Value: ") + cmp(value.stringify(), cHighlight))
                                }
                                + getClickLore(isGlobal, value)
                        )
                    }
                }, if (isGlobal) isSet else value as? Boolean ?: true)
            }
        }
    }

    /**
     * Turns doDaylightCircle into Do Daylight Circle
     */
    private fun fancyName(name: String): String {
        return buildString {
            name.forEachIndexed { index, char ->
                when {
                    index == 0 -> append(char.uppercase())
                    char.isUpperCase() -> append(" $char")
                    else -> append(char)
                }
            }
        }
    }

    private fun Any?.stringify(): String {
        return when (this) {
            is Boolean -> if (this) msgTrue else msgFalse
            is Int -> toString()
            else -> msgNone
        }
    }

    private fun getTranslated(key: String): Component {
        return cmp("   ") + Component.translatable(key).render(local).mergeStyle(cmp(""))
    }

    private fun getClickLore(isGlobal: Boolean, value: Any?): List<Component> {
        return buildList {
            add(emptyComponent())
            if (isGlobal) add(overrideLore)
            when (value) {
                is Boolean -> add(boolLore)
                is Int -> addAll(numberLore)
            }
        }
    }

    private fun getIcon(gameRule: GameRule<*>): Material {
        return when (gameRule) {
            GameRule.ANNOUNCE_ADVANCEMENTS -> Material.KNOWLEDGE_BOOK
            GameRule.COMMAND_BLOCK_OUTPUT -> Material.COMMAND_BLOCK
            GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK -> Material.ELYTRA
            GameRule.DO_DAYLIGHT_CYCLE -> Material.CLOCK
            GameRule.DO_ENTITY_DROPS -> Material.MUTTON
            GameRule.DO_FIRE_TICK -> Material.FLINT_AND_STEEL
            GameRule.DO_LIMITED_CRAFTING -> Material.CRAFTING_TABLE
            GameRule.DO_MOB_LOOT -> Material.ROTTEN_FLESH
            GameRule.DO_MOB_SPAWNING -> Material.ZOMBIE_HEAD
            GameRule.DO_TILE_DROPS -> Material.DIRT
            GameRule.DO_WEATHER_CYCLE -> Material.DAYLIGHT_DETECTOR
            GameRule.KEEP_INVENTORY -> Material.ENDER_CHEST
            GameRule.LOG_ADMIN_COMMANDS -> Material.WRITABLE_BOOK
            GameRule.MOB_GRIEFING -> Material.CREEPER_HEAD
            GameRule.NATURAL_REGENERATION -> Material.BEETROOT
            GameRule.REDUCED_DEBUG_INFO -> Material.STRUCTURE_BLOCK
            GameRule.SEND_COMMAND_FEEDBACK -> Material.REPEATING_COMMAND_BLOCK
            GameRule.SHOW_DEATH_MESSAGES -> Material.PLAYER_HEAD
            GameRule.SPECTATORS_GENERATE_CHUNKS -> Material.WHITE_STAINED_GLASS
            GameRule.DISABLE_RAIDS -> Material.BELL
            GameRule.DO_INSOMNIA -> Material.PHANTOM_MEMBRANE
            GameRule.DO_IMMEDIATE_RESPAWN -> Material.RESPAWN_ANCHOR
            GameRule.DROWNING_DAMAGE -> Material.POTION
            GameRule.FALL_DAMAGE -> Material.FEATHER
            GameRule.FIRE_DAMAGE -> Material.CAMPFIRE
            GameRule.FREEZE_DAMAGE -> Material.SNOW
            GameRule.DO_PATROL_SPAWNING -> Material.CROSSBOW
            GameRule.DO_TRADER_SPAWNING -> Material.WANDERING_TRADER_SPAWN_EGG
            GameRule.DO_WARDEN_SPAWNING -> Material.SCULK_SHRIEKER
            GameRule.FORGIVE_DEAD_PLAYERS -> Material.GUNPOWDER
            GameRule.UNIVERSAL_ANGER -> Material.GOLDEN_SWORD
            GameRule.SPAWN_RADIUS -> Material.ARROW
            GameRule.MAX_ENTITY_CRAMMING -> Material.SKELETON_SKULL
            GameRule.MAX_COMMAND_CHAIN_LENGTH -> Material.CHAIN_COMMAND_BLOCK
            GameRule.PLAYERS_SLEEPING_PERCENTAGE -> Material.LIGHT_BLUE_BED
            else -> Material.PAPER
        }
    }
}
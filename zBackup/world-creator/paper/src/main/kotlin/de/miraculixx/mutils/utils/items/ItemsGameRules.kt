package de.miraculixx.mutils.utils.items

import de.miraculixx.challenge.api.data.CustomGameRule
import de.miraculixx.challenge.api.data.MergedGameRule
import de.miraculixx.kpaper.extensions.bukkit.render
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mutils.globalRules
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mvanilla.messages.*
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
            val customRules = CustomGameRule.entries.map { MergedGameRule(it.name, it, it.key) }
            val gameRules = GameRule.values().map { MergedGameRule(it.name, it, it.translationKey()) }
            val allRules = customRules + gameRules
            allRules.subList(from, to.coerceAtMost(gameRules.size - 1)).forEach {

                val value = if (world == null) globalRules[it.name] else {
                    if (it.sourceEnum is CustomGameRule) {
                        WorldManager.getWorldData(world.uid)?.customGameRules?.get(it.sourceEnum)
                    } else world.getGameRuleValue(it.sourceEnum as GameRule<*>)
                }
                val isSet = value != null

                put(itemStack(getIcon(it.name)) {
                    meta {
                        val rawKey = it.key.removePrefix("gamerule.")
                        name = cmp(fancyName(rawKey), cHighlight)
                        customModel = 1
                        persistentDataContainer.set(NamespacedKey(namespace, "gui.gamerules.key"), PersistentDataType.STRING, it.name)
                        lore(listOf(cmp("Key: $rawKey", NamedTextColor.DARK_GRAY))
                                + infoLore
                                + getTranslated(it.key)
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

    private fun getIcon(gameRule: String): Material {
        return when (gameRule) {
            "announceAdvancements" -> Material.KNOWLEDGE_BOOK
            "commandBlockOutput" -> Material.COMMAND_BLOCK
            "disableElytraMovementCheck" -> Material.ELYTRA
            "doDaylightCycle" -> Material.CLOCK
            "doEntityDrops" -> Material.MUTTON
            "doFireTick" -> Material.FLINT_AND_STEEL
            "doLimitedCrafting" -> Material.CRAFTING_TABLE
            "doMobLoot" -> Material.ROTTEN_FLESH
            "doMobSpawning" -> Material.ZOMBIE_HEAD
            "doTileDrops" -> Material.DIRT
            "doWeatherCycle" -> Material.DAYLIGHT_DETECTOR
            "keepInventory" -> Material.ENDER_CHEST
            "logAdminCommands" -> Material.WRITABLE_BOOK
            "mobGriefing" -> Material.CREEPER_HEAD
            "naturalRegeneration" -> Material.BEETROOT
            "reducedDebugInfo" -> Material.STRUCTURE_BLOCK
            "sendCommandFeedback" -> Material.REPEATING_COMMAND_BLOCK
            "showDeathMessages" -> Material.PLAYER_HEAD
            "spectatorsGenerateChunks" -> Material.WHITE_STAINED_GLASS
            "disableRaids" -> Material.BELL
            "doInsomnia" -> Material.PHANTOM_MEMBRANE
            "doImmediateRespawn" -> Material.RESPAWN_ANCHOR
            "drowningDamage" -> Material.POTION
            "fallDamage" -> Material.FEATHER
            "fireDamage" -> Material.CAMPFIRE
            "freezeDamage" -> Material.SNOW_BLOCK
            "doPatrolSpawning" -> Material.CROSSBOW
            "doTraderSpawning" -> Material.WANDERING_TRADER_SPAWN_EGG
            "doWardenSpawning" -> Material.SCULK_SHRIEKER
            "forgiveDeadPlayers" -> Material.GUNPOWDER
            "universalAnger" -> Material.GOLDEN_SWORD
            "spawnRadius" -> Material.ARROW
            "maxEntityCramming" -> Material.SKELETON_SKULL
            "maxCommandChainLength" -> Material.CHAIN_COMMAND_BLOCK
            "playersSleepingPercentage" -> Material.LIGHT_BLUE_BED
            "tntExplosionDropDecay" -> Material.TNT
            "globalSoundEvents" -> Material.JUKEBOX
            "doVinesSpread" -> Material.VINE
            "waterSourceConversion" -> Material.WATER_BUCKET
            "lavaSourceConversion" -> Material.LAVA_BUCKET
            "mobExplosionDropDecay" -> Material.CREEPER_HEAD
            "blockExplosionDropDecay" -> Material.TNT
            "randomTickSpeed" -> Material.OBSERVER
            "commandModificationBlockLimit" -> Material.WOODEN_AXE
            "snowAccumulationHeight" -> Material.SNOW

            //Custom Game Rules
            "PVP" -> Material.DIAMOND_SWORD
            "BLOCK_UPDATES" -> Material.SAND
            else -> Material.PAPER
        }
    }
}
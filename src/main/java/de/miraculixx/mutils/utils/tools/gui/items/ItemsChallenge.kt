package de.miraculixx.mutils.utils.tools.gui.items

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.settings.gui.StorageFilters
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.utils.addLines
import de.miraculixx.mutils.utils.challengeOfTheMonth
import de.miraculixx.mutils.utils.getMessageList
import de.miraculixx.mutils.utils.premium
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsChallenge(private val c: FileConfiguration) {

    fun getItems(id: Int, filter: StorageFilters?): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(filter)
            else -> {
                linkedMapOf(Pair(ItemStack(Material.BARRIER), false))
            }
        }
        /* ID Glossary
        1 → Main Challenges
        2 -> TODO
         */
        return list
    }

    private fun g1(filter: StorageFilters?): LinkedHashMap<ItemStack, Boolean> {
        val map = LinkedHashMap<ItemStack, Boolean>()

        if (isMatchingFilter(challengeOfTheMonth, filter)) {
            val monthly = getChallengeItem(challengeOfTheMonth)
            val item = monthly.first
            val meta = item.itemMeta
            val green = TextColor.fromHexString("#55FF55")
            meta.displayName(Component.text(ChatColor.stripColor(meta.name) ?: "error").color(green).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD))
            val lore = meta.lore()
            lore?.add(0, Component.text("Challenge of the Month").color(green).decoration(TextDecoration.ITALIC, false))
            meta.lore(lore)
            item.itemMeta = meta
            map[item] = monthly.second
        }

        Modules.values().reversed().forEach { module ->
            if (module.isChallenge()) {
                if (module == challengeOfTheMonth) return@forEach
                if (isMatchingFilter(module, filter)) {
                    val pair = getChallengeItem(module)
                    if (!premium) {
                        val item = pair.first
                        val meta = item.itemMeta
                        val lore = meta.lore()
                        lore?.add(0, Component.text("Premium only").color(TextColor.fromHexString("#FF5555")).decoration(TextDecoration.ITALIC, false))
                        meta.lore(lore)
                        item.itemMeta = meta
                    }
                    map[pair.first] = pair.second
                }
            }
        }
        return map
    }

    private fun isMatchingFilter(module: Modules, filter: StorageFilters?): Boolean {
        return filter == null || filter == StorageFilters.NO_FILTER || module.matchingFilter(filter)
    }

    //Utilities
    private fun getChallengeItem(challenge: Modules): Pair<ItemStack, Boolean> {
        val l = mutableListOf(" ", "§7∙ §9§nChallenge")
            .addLines(getMessageList("item.GUI.${challenge.name}.l", "   "))
            .addLines(getFilter(challenge))
            .addLines(" ", "§7∙ §9§nSettings")
        val item = when (challenge) {
            Modules.FLY -> itemStack(Material.ELYTRA) {
                meta {
                    customModel = 100
                    name = "§9§lF.L.Y. Challenge"
                    lore = l.addLines(
                        "   §7Boost Power: §9${c.getDouble("FLY.Boost")} §8(Default 2.0)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +0.5 Boost",
                        "§9Shift Right click§7 ≫ -0.5 Boost"
                    )
                }
            }
            Modules.IN_TIME -> itemStack(Material.CLOCK) {
                meta {
                    customModel = 101
                    name = "§9§lInTime Challenge"
                    lore = l.addLines(
                        "   §7Player Time: §9" + c.getInt("IN_TIME.PlayerTime") + "s §8(Default 120s)",
                        "   §7Entity Time: §9" + c.getInt("IN_TIME.MobTime") + "s §8(Default 120s)",
                        "   §7Time per HP: §9" + c.getInt("IN_TIME.DamageTime") + "s §8(Default 5s)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
            }
            Modules.MOB_RANDOMIZER -> itemStack(Material.ZOMBIE_HEAD) {
                meta {
                    customModel = 102
                    name = "§9§lMob Blocks Challenge"
                    lore = l.addLines(
                        "   §7Completely Random: §9" + c.getBoolean("MOB_RANDOMIZER.Random") + " §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting"
                    )
                }
            }
            Modules.CHECKPOINTS -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 103
                    name = "§9§lCheckpoints Challenge"
                    lore = l.addLines(
                        "   §7Only teleport: §9" + c.getBoolean("CHECKPOINTS.Teleport") + " §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting"
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0="
                )
            }
            Modules.DIM_SWAP -> itemStack(Material.END_PORTAL_FRAME) {
                meta {
                    customModel = 104
                    name = "§9§lDimension Swap Challenge"
                    lore = l.addLines(
                        "   §7Starter Pickaxe: §9" + c.getBoolean("DIM_SWAP.Pickaxe") + " §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Pickaxe"
                    )
                }
            }
            Modules.SNAKE -> itemStack(Material.RED_CONCRETE) {
                meta {
                    customModel = 105
                    name = "§9§lSnake Challenge"
                    lore = l.addLines(
                        "   §7Start Speed: §9" + c.getInt("SNAKE.Speed") + "b/s §8(Default 1)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +1b/s",
                        "§9Shift Right click§7 ≫ -1b/s"
                    )
                }
            }
            Modules.REALISTIC -> itemStack(Material.OAK_SAPLING) {
                meta {
                    customModel = 106
                    name = "§c§lRealistic Challenge"
                    lore = mutableListOf("§c§l§oIN DEVELOPMENT - WIP")
                        .addLines(l)
                        .addLines(
                            "   §7§oNone",
                            " ",
                            "§9Left click§7 ≫ Toggle Active"
                        )
                }
            }
            Modules.CAPTIVE -> itemStack(Material.IRON_BARS) {
                meta {
                    customModel = 107
                    name = "§9§lCaptive Challenge"
                    lore = l.addLines(
                        "   §7Base Size: §9" + c.getInt("CAPTIVE.Size") + "b §8(Default 1b)",
                        "   §7Amplifier: §9" + c.getInt("CAPTIVE.Amplifier") + "b §8(Default 1b)",
                        "   §7Level Mode: §9" + c.getBoolean("CAPTIVE.LevelMode") + " §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
            }
            Modules.GHOST -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 108
                    name = "§9§lGhost Challenge"
                    lore = l.addLines(
                        "   §7Radius: §9" + c.getInt("GHOST.Radius") + "b §8(Default 7b)",
                        "   §7Adventure: §9" + c.getBoolean("GHOST.Adventure") + " §8(Default false)",
                        "   §7Mode: §9" + c.getBoolean("GHOST.Mode") + " §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19"
                )
            }
            Modules.BLOCK_ASYNC -> itemStack(Material.RED_STAINED_GLASS) {
                meta {
                    customModel = 109
                    name = "§9§lBlock Async Challenge"
                    lore = l.addLines(
                        "   §7§oNone",
                        " ",
                        "§9Left click§7 ≫ Toggle Active"
                    )
                }
            }
            Modules.NO_SAME_ITEM -> itemStack(Material.WITHER_ROSE) {
                meta {
                    customModel = 110
                    name = "§9§lNo Same Item Challenge"
                    lore = l.addLines(
                        "   §7Lives: §9${c.getInt("NO_SAME_ITEM.Lives")} §8(Default 5)",
                        "   §7Sync Heart: §9${c.getBoolean("NO_SAME_ITEM.SyncHeart")} §8(Default false)",
                        "   §7Info Mode: §9${c.getString("NO_SAME_ITEM.Info")} §8(Default EVERYTHING)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
            }
            Modules.LIMITED_SKILLS -> itemStack(Material.TURTLE_HELMET) {
                meta {
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    customModel = 111
                    name = "§9§lLimited Skills"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("LIMITED_SKILLS.Random")} §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Random"
                    )
                }
            }
            Modules.RUN_RANDOMIZER -> itemStack(Material.GOLDEN_BOOTS) {
                meta {
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    customModel = 112
                    name = "§9§lRun Randomizer"
                    lore = l.addLines(
                        "   §7Block Goal: §9${c.getInt("RUN_RANDOMIZER.Goal")} §8(Default 500)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +50 Blocks",
                        "§9Shift Right click§7 ≫ -50 Blocks"
                    )
                }
            }
            Modules.SPLIT_HP -> itemStack(Material.BEETROOT) {
                meta {
                    customModel = 113
                    name = "§9§lShared Hearts"
                    lore = l.addLines(
                        "   §7§oNone",
                        " ",
                        "§9Left click§7 ≫ Toggle Active"
                    )
                }
            }
            Modules.DAMAGE_DUELL -> itemStack(Material.IRON_SWORD) {
                meta {
                    customModel = 114
                    name = "§9§lDamage Duell"
                    lore = l.addLines(
                        "   §7Percentage: §9${c.getInt("DAMAGE_DUELL.Percent")}% §8(Default 50%)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +10%",
                        "§9Shift Right click§7 ≫ -10%"
                    )
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                }
            }
            Modules.ONE_BIOME -> itemStack(Material.MAP) {
                meta {
                    customModel = 115
                    name = "§9§lOne Biome"
                    lore = mutableListOf("§cHuge Performance Impact").addLines(l)
                        .addLines(
                            "   §7Delay: §9${c.getInt("ONE_BIOME.Delay")}s §8(Default 300)",
                            " ",
                            "§9Left click§7 ≫ Toggle Active",
                            "§9Right click§7 ≫ +10s",
                            "§9Shift Right click§7 ≫ -10s"
                        )
                }
            }
            Modules.BOOST_UP -> itemStack(Material.SHULKER_SHELL) {
                meta {
                    customModel = 116
                    name = "§9§lBoost Up"
                    lore = l.addLines(
                        "   §7Radius: §9${c.getInt("BOOST_UP.Radius")}b §8(Default 5)",
                        "   §7Boost: §9${c.getInt("BOOST_UP.Boost")} §8(Default 5)",
                        "   §7Mode: §9${c.getBoolean("BOOST_UP.Mode")} §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
            }
            Modules.RIGHT_TOOL -> itemStack(Material.WOODEN_AXE) {
                meta {
                    customModel = 117
                    name = "§9§lOnly right Tool"
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    lore = l.addLines(
                        "   §7§oNone",
                        " ",
                        "§9Click§7 ≫ Toggle Active",
                    )
                }
            }
            Modules.CHUNK_BLOCK_BREAK -> itemStack(Material.TNT) {
                meta {
                    customModel = 118
                    name = "§9§lChunk Block Breaker"
                    lore = l.addLines(
                        "   §7Bundle: §9${c.getBoolean("CHUNK_BLOCK_BREAK.Bundle")} §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings"
                    )
                }
            }
            Modules.SNEAK_SPAWN -> itemStack(Material.HOPPER) {
                meta {
                    customModel = 119
                    name = "§9§lSneak Drop Randomizer"
                    lore = l.addLines(
                        "   §7Only Mobs: §9${c.getBoolean("SNEAK_SPAWN.Mobs")} §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting"
                    )
                }
            }
            Modules.WORLD_PEACE -> itemStack(Material.CORNFLOWER) {
                meta {
                    customModel = 120
                    name = "§9§lWorld Peace"
                    lore = l.addLines(
                        "   §7§onone",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +10s",
                        "§9Shift right click§7 ≫ -10s"
                    )
                }
            }
            Modules.GRAVITY -> itemStack(Material.SAND) {
                meta {
                    customModel = 121
                    name = "§9§lGravity Switch"
                    lore = l.addLines(
                        "   §7Delay: §9${c.getInt("GRAVITY.Delay")}s §8(Default 180s)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +10s",
                        "§9Shift right click§7 ≫ -10s"
                    )
                }
            }
            Modules.STAY_AWAY -> itemStack(Material.TNT) {
                meta {
                    customModel = 122
                    name = "§9§lStay Away"
                    lore = l.addLines(
                        "   §7Distance: §9${c.getDouble("STAY_AWAY.Distance")} §8(Default 3.0)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +0.5b",
                        "§9Shift right click§7 ≫ -0.5b"
                    )
                }
            }
            Modules.RANDOMIZER_BLOCK -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 123
                    name = "§9§lBlock Randomizer"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("RANDOMIZER_BLOCK.Random")} §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMzY2VjNjg3NjlmZTljOTcxMjkxZWRiN2VmOTZhNGUzYjYwNDYyY2ZkNWZiNWJhYTFjYmIzYTcxNTEzZTdiIn19fQ=="
                )
            }
            Modules.RANDOMIZER_ENTITY -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 124
                    name = "§9§lMob Drops Randomizer"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("RANDOMIZER_ENTITY.Random")} §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkNGEwMWRiNjEyNjYwMWRlZDE0MDZjZjYyMzhjZTJiNzAyNGVhY2U1ZWE2MDRmYmMyMDhhMmFmMjljOTdhZCJ9fX0="
                )
            }
            Modules.RANDOMIZER_BIOMES -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 125
                    name = "§9§lBiome Randomizer"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("RANDOMIZER_BIOMES.Random")} §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZiZGY1MjIyMmI0ZjA5MmUxMTIyODMxYjM4ODE2NGM0NjJmYTQxZGYxZDQ5NDI4ZDQ0OGE4Nzk0MzM5YjM0YiJ9fX0="
                )
            }
            Modules.RANDOMIZER_MOBS -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    customModel = 126
                    name = "§9§lMob Spawn Randomizer"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("RANDOMIZER_MOB.Random")} §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0MGU1ZGIyMWNlZGFjNGM5NzJiN2IyMmViYjY0Y2Y0YWRkNjFiM2I1NGIxMzE0MzVlZWRkMzA3NTk4YjcifX19"
                )
                }
            Modules.FORCE_COLLECT -> itemStack(Material.CHEST) {
                meta {
                    customModel = 127
                    name = "§9§lForce Item"
                    lore = l.addLines(
                        "   §7Minimum: §9${c.getInt("FORCE_COLLECT.MinSecs")}s §8(Default 180s)",
                        "   §7Maximum: §9${c.getInt("FORCE_COLLECT.MaxSecs")}s §8(Default 360s)",
                        "   §7Cooldown: §9${c.getInt("FORCE_COLLECT.Cooldown")}s §8(Default 300s)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings",
                    )
                }
            }
            Modules.RANDOMIZER_ENTITY_DAMAGE -> itemStack(Material.PLAYER_HEAD) {
                meta {
                    customModel = 128
                    name = "§9§lEntity Damage Randomizer"
                    lore = l.addLines(
                        "   §7Random: §9${c.getBoolean("RANDOMIZER_ENTITY_DAMAGE.Random")} §8(Default false)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
                itemMeta = skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBkMmEzY2U0OTk5ZmVkMzMwZDNhNWQwYTllMjE4ZTM3ZjRmNTc3MTk4MDg2NTczOTZkODMyMjM5ZTEyIn19fQ=="
                )
            }
            Modules.NO_DOUBLE_KILL -> itemStack(Material.REPEATER) {
                meta {
                    customModel = 129
                    name = "§9§lNo Double Kill"
                    lore = l.addLines(
                        "   §7Global: §9${c.getBoolean("NO_DOUBLE_KILL.Global")} §8(Default true)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Toggle Setting",
                    )
                }
            }
            Modules.DAMAGER -> itemStack(Material.DIAMOND_SWORD) {
                meta {
                    customModel = 130
                    name = "§9§lDamager"
                    lore = l.addLines(
                        "   §7Mode: §9${c.getString("DAMAGER.Mode")} §8(Default SLOT_CHANGE)",
                        "   §7Damage: §9${c.getDouble("DAMAGER.Damage")}hp §8(Default 1hp)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings",
                    )
                }
            }
            Modules.RIVALS_COLLECT -> itemStack(Material.CHEST_MINECART) {
                meta {
                    customModel = 131
                    name = "§9§lRivals Collect"
                    lore = l.addLines(
                        "   §7Mode: §9${c.getString("RIVALS_COLLECT.Mode")} §8(Default ITEMS)",
                        "   §7Joker: §9${c.getInt("RIVALS_COLLECT.Joker")} §8(Default 3)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ Settings",
                    )
                }
            }

            else -> ItemStack(Material.BARRIER)
        }
        return Pair(item, ModuleManager.isActive(challenge))
    }

    private fun getFilter(challenge: Modules): List<String> {
        return mutableListOf(" ", "§7∙ §9§nFilters").addLines(challenge.getFilterLore())
    }
}
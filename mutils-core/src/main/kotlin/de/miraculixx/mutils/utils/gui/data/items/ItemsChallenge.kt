package de.miraculixx.mutils.utils.gui.data.items

import de.miraculixx.mutils.utils.config.Config
import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.utils.enums.gui.StorageFilter
import de.miraculixx.mutils.utils.gui.item.*
import de.miraculixx.mutils.utils.messages.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ItemsChallenge(private val c: Config) {

    fun getItems(id: Int, filter: StorageFilter?): LinkedHashMap<ItemStack, Boolean> {
        val list = when (id) {
            1 -> g1(filter)
            else -> {
                linkedMapOf(Pair(ItemStack(Items.BARRIER), false))
            }
        }
        /* ID Glossary
        1 → Main Challenges
        2 -> TODO
         */
        return list
    }

    private fun g1(filter: StorageFilter?): LinkedHashMap<ItemStack, Boolean> {
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

        Challenge.values().reversed().forEach { module ->
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

    private fun isMatchingFilter(module: Challenge, filter: StorageFilter?): Boolean {
        return filter == null || filter == StorageFilter.NO_FILTER || module.matchingFilter(filter)
    }

    //Utilities
    private fun getChallengeItem(challenge: Challenge): Pair<ItemStack, Boolean> {
        val item = when (challenge) {
            Challenge.FLY -> itemStack(Items.ELYTRA) {
                setCustomModel(100)
                getName(challenge)
                getLore(challenge, listOf())
                lore = l.addLines(
                    "   §7Boost Power: §9${c.getDouble("FLY.Boost")} §8(Default 2.0)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +0.5 Boost",
                    "§9Shift Right click§7 ≫ -0.5 Boost"
                )
            }

            Challenge.IN_TIME -> itemStack(Items.CLOCK) {
                setCustomModel(101)
                getName(challenge)
                lore = l.addLines(
                    "   §7Player Time: §9" + c.getInt("IN_TIME.PlayerTime") + "s §8(Default 120s)",
                    "   §7Entity Time: §9" + c.getInt("IN_TIME.MobTime") + "s §8(Default 120s)",
                    "   §7Time per HP: §9" + c.getInt("IN_TIME.DamageTime") + "s §8(Default 5s)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
            }

            Challenge.MOB_RANDOMIZER -> itemStack(Items.ZOMBIE_HEAD) {
                setCustomModel(102)
                getName(challenge)
                lore = l.addLines(
                    "   §7Completely Random: §9" + c.getBoolean("MOB_RANDOMIZER.Random") + " §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting"
                )
            }

            Challenge.CHECKPOINTS -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(103)
                getName(challenge)
                lore = l.addLines(
                    "   §7Only teleport: §9" + c.getBoolean("CHECKPOINTS.Teleport") + " §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting"
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0=")
            }

            Challenge.DIM_SWAP -> itemStack(Items.END_PORTAL_FRAME) {
                setCustomModel(104)
                getName(challenge)
                lore = l.addLines(
                    "   §7Starter Pickaxe: §9" + c.getBoolean("DIM_SWAP.Pickaxe") + " §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Pickaxe"
                )
            }

            Challenge.SNAKE -> itemStack(Items.RED_CONCRETE) {
                setCustomModel(105)
                getName(challenge)
                lore = l.addLines(
                    "   §7Start Speed: §9" + c.getInt("SNAKE.Speed") + "b/s §8(Default 1)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +1b/s",
                    "§9Shift Right click§7 ≫ -1b/s"
                )
            }

            Challenge.REALISTIC -> itemStack(Items.OAK_SAPLING) {
                setCustomModel(106)
                getName(challenge)
                lore = mutableListOf("§c§l§oIN DEVELOPMENT - WIP")
                    .addLines(l)
                    .addLines(
                        "   §7§oNone",
                        " ",
                        "§9Left click§7 ≫ Toggle Active"
                    )
            }

            Challenge.CAPTIVE -> itemStack(Items.IRON_BARS) {
                setCustomModel(107)
                getName(challenge)
                lore = l.addLines(
                    "   §7Base Size: §9" + c.getInt("CAPTIVE.Size") + "b §8(Default 1b)",
                    "   §7Amplifier: §9" + c.getInt("CAPTIVE.Amplifier") + "b §8(Default 1b)",
                    "   §7Level Mode: §9" + c.getBoolean("CAPTIVE.LevelMode") + " §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
            }

            Challenge.GHOST -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(108)
                getName(challenge)
                lore = l.addLines(
                    "   §7Radius: §9" + c.getInt("GHOST.Radius") + "b §8(Default 7b)",
                    "   §7Adventure: §9" + c.getBoolean("GHOST.Adventure") + " §8(Default false)",
                    "   §7Mode: §9" + c.getBoolean("GHOST.Mode") + " §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19")
            }

            Challenge.BLOCK_ASYNC -> itemStack(Items.RED_STAINED_GLASS) {
                setCustomModel(109)
                getName(challenge)
                lore = l.addLines(
                    "   §7§oNone",
                    " ",
                    "§9Left click§7 ≫ Toggle Active"
                )
            }

            Challenge.NO_SAME_ITEM -> itemStack(Items.WITHER_ROSE) {
                setCustomModel(110)
                getName(challenge)
                lore = l.addLines(
                    "   §7Lives: §9${c.getInt("NO_SAME_ITEM.Lives")} §8(Default 5)",
                    "   §7Sync Heart: §9${c.getBoolean("NO_SAME_ITEM.SyncHeart")} §8(Default false)",
                    "   §7Info Mode: §9${c.getString("NO_SAME_ITEM.Info")} §8(Default EVERYTHING)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
            }

            Challenge.LIMITED_SKILLS -> itemStack(Items.TURTLE_HELMET) {
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                setCustomModel(111)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("LIMITED_SKILLS.Random")} §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Random"
                )
            }

            Challenge.RUN_RANDOMIZER -> itemStack(Items.GOLDEN_BOOTS) {
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                setCustomModel(112)
                getName(challenge)
                lore = l.addLines(
                    "   §7Block Goal: §9${c.getInt("RUN_RANDOMIZER.Goal")} §8(Default 500)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +50 Blocks",
                    "§9Shift Right click§7 ≫ -50 Blocks"
                )
            }

            Challenge.SPLIT_HP -> itemStack(Items.BEETROOT) {
                setCustomModel(113)
                getName(challenge)
                lore = l.addLines(
                    "   §7§oNone",
                    " ",
                    "§9Left click§7 ≫ Toggle Active"
                )
            }

            Challenge.DAMAGE_DUELL -> itemStack(Items.IRON_SWORD) {
                setCustomModel(114)
                getName(challenge)
                lore = l.addLines(
                    "   §7Percentage: §9${c.getInt("DAMAGE_DUELL.Percent")}% §8(Default 50%)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +10%",
                    "§9Shift Right click§7 ≫ -10%"
                )
                addHideFlags(HideFlag.HIDE_ATTRIBUTES)
            }

            Challenge.ONE_BIOME -> itemStack(Items.MAP) {
                setCustomModel(115)
                getName(challenge)
                lore = mutableListOf("§cHuge Performance Impact").addLines(l)
                    .addLines(
                        "   §7Delay: §9${c.getInt("ONE_BIOME.Delay")}s §8(Default 300)",
                        " ",
                        "§9Left click§7 ≫ Toggle Active",
                        "§9Right click§7 ≫ +10s",
                        "§9Shift Right click§7 ≫ -10s"
                    )
            }

            Challenge.BOOST_UP -> itemStack(Items.SHULKER_SHELL) {
                setCustomModel(116)
                getName(challenge)
                lore = l.addLines(
                    "   §7Radius: §9${c.getInt("BOOST_UP.Radius")}b §8(Default 5)",
                    "   §7Boost: §9${c.getInt("BOOST_UP.Boost")} §8(Default 5)",
                    "   §7Mode: §9${c.getBoolean("BOOST_UP.Mode")} §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
            }

            Challenge.RIGHT_TOOL -> itemStack(Items.WOODEN_AXE) {
                meta {
                    setCustomModel(117)
                    getName(challenge)
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    lore = l.addLines(
                        "   §7§oNone",
                        " ",
                        "§9Click§7 ≫ Toggle Active",
                    )
                }
            }

            Challenge.CHUNK_BLOCK_BREAK -> itemStack(Items.TNT) {
                setCustomModel(118)
                getName(challenge)
                lore = l.addLines(
                    "   §7Bundle: §9${c.getBoolean("CHUNK_BLOCK_BREAK.Bundle")} §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings"
                )
            }

            Challenge.SNEAK_SPAWN -> itemStack(Items.HOPPER) {
                setCustomModel(119)
                getName(challenge)
                lore = l.addLines(
                    "   §7Only Mobs: §9${c.getBoolean("SNEAK_SPAWN.Mobs")} §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting"
                )
            }

            Challenge.WORLD_PEACE -> itemStack(Items.CORNFLOWER) {
                setCustomModel(120)
                getName(challenge)
                lore = l.addLines(
                    "   §7§onone",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +10s",
                    "§9Shift right click§7 ≫ -10s"
                )
            }

            Challenge.GRAVITY -> itemStack(Items.SAND) {
                setCustomModel(121)
                getName(challenge)
                lore = l.addLines(
                    "   §7Delay: §9${c.getInt("GRAVITY.Delay")}s §8(Default 180s)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +10s",
                    "§9Shift right click§7 ≫ -10s"
                )
            }

            Challenge.STAY_AWAY -> itemStack(Items.TNT) {
                setCustomModel(122)
                getName(challenge)
                lore = l.addLines(
                    "   §7Distance: §9${c.getDouble("STAY_AWAY.Distance")} §8(Default 3.0)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ +0.5b",
                    "§9Shift right click§7 ≫ -0.5b"
                )
            }

            Challenge.RANDOMIZER_BLOCK -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(123)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("RANDOMIZER_BLOCK.Random")} §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMzY2VjNjg3NjlmZTljOTcxMjkxZWRiN2VmOTZhNGUzYjYwNDYyY2ZkNWZiNWJhYTFjYmIzYTcxNTEzZTdiIn19fQ==")
            }

            Challenge.RANDOMIZER_ENTITY -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(124)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("RANDOMIZER_ENTITY.Random")} §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkNGEwMWRiNjEyNjYwMWRlZDE0MDZjZjYyMzhjZTJiNzAyNGVhY2U1ZWE2MDRmYmMyMDhhMmFmMjljOTdhZCJ9fX0=")
            }

            Challenge.RANDOMIZER_BIOMES -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(125)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("RANDOMIZER_BIOMES.Random")} §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZiZGY1MjIyMmI0ZjA5MmUxMTIyODMxYjM4ODE2NGM0NjJmYTQxZGYxZDQ5NDI4ZDQ0OGE4Nzk0MzM5YjM0YiJ9fX0=")
            }

            Challenge.RANDOMIZER_MOBS -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(126)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("RANDOMIZER_MOB.Random")} §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0MGU1ZGIyMWNlZGFjNGM5NzJiN2IyMmViYjY0Y2Y0YWRkNjFiM2I1NGIxMzE0MzVlZWRkMzA3NTk4YjcifX19")
            }

            Challenge.FORCE_COLLECT -> itemStack(Items.CHEST) {
                setCustomModel(127)
                getName(challenge)
                lore = l.addLines(
                    "   §7Minimum: §9${c.getInt("FORCE_COLLECT.MinSecs")}s §8(Default 180s)",
                    "   §7Maximum: §9${c.getInt("FORCE_COLLECT.MaxSecs")}s §8(Default 360s)",
                    "   §7Cooldown: §9${c.getInt("FORCE_COLLECT.Cooldown")}s §8(Default 300s)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings",
                )
            }

            Challenge.RANDOMIZER_ENTITY_DAMAGE -> itemStack(Items.PLAYER_HEAD) {
                setCustomModel(128)
                getName(challenge)
                lore = l.addLines(
                    "   §7Random: §9${c.getBoolean("RANDOMIZER_ENTITY_DAMAGE.Random")} §8(Default false)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
                setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBkMmEzY2U0OTk5ZmVkMzMwZDNhNWQwYTllMjE4ZTM3ZjRmNTc3MTk4MDg2NTczOTZkODMyMjM5ZTEyIn19fQ==")
            }

            Challenge.NO_DOUBLE_KILL -> itemStack(Items.REPEATER) {
                setCustomModel(129)
                getName(challenge)
                getLore(challenge, challenge.getSettings(listOf(c.getBoolean("NO_DOUBLE_KILL.Global"))))
                lore = l.addLines(
                    "   §7Global: §9${c.getBoolean("NO_DOUBLE_KILL.Global")} §8(Default true)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Toggle Setting",
                )
            }

            Challenge.DAMAGER -> itemStack(Items.DIAMOND_SWORD) {
                setCustomModel(130)
                getName(challenge)
                getLore(challenge, challenge.getSettings(listOf(c.getString("DAMAGER.Mode"), c.getString("DAMAGER.Damage"))))
                lore = l.addLines(
                    "   §7Mode: §9${c.getString("DAMAGER.Mode")} §8(Default SLOT_CHANGE)",
                    "   §7Damage: §9${c.getDouble("DAMAGER.Damage")}hp §8(Default 1hp)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings",
                )
            }

            Challenge.RIVALS_COLLECT -> itemStack(Items.CHEST_MINECART) {
                setCustomModel(131)
                setName(getName(challenge))
                val lore = getLore(challenge, challenge)
                lore = l.addLines(
                    "   §7Mode: §9${c.getString("RIVALS_COLLECT.Mode")} §8(Default ITEMS)",
                    "   §7Joker: §9${c.getInt("RIVALS_COLLECT.Joker")} §8(Default 3)",
                    " ",
                    "§9Left click§7 ≫ Toggle Active",
                    "§9Right click§7 ≫ Settings",
                )
            }

            else -> ItemStack(Items.BARRIER)
        }
        return Pair(item, ModuleManager.isActive(challenge))
    }

    private fun getFilter(challenge: Challenge): List<Component> {
        return buildList {
            emptyComponent()
            cmp("∙ ") + cmp("Filters", cHighlight, underlined = true)
            addAll(challenge.filter.map { cmp("   - ") + cmp(it.name) })
        }
    }

    private fun getName(challenge: Challenge): Component {
        return cmp("", cHighlight, underlined = true) + msg("items.ch.${challenge.name}.n")
    }

    private fun getLore(challenge: Challenge, settings: List<SettingsData>): List<Component> {
        return buildList {
            val isEmpty = settings.isEmpty()
            add(emptyComponent())
            add(cmp("∙ ") + cmp("Challenge", cHighlight, underlined = true))
            addAll(msgList("items.ch.${challenge.name}.l"))

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            if (isEmpty) add(cmp("   None", italic = true))
            else addAll(settings.map { cmp("   ") + msg("items.chS.${it.id}") + cmp(": ") + cmp(it.value, cHighlight) + cmp(" (Default ${it.default})") })

            add(emptyComponent())
            add(cmp("∙ ") + cmp("Filter", cHighlight, underlined = true))
            addAll(getFilter(challenge))

            add(emptyComponent())
            if (isEmpty) add(cmp("Click ", cHighlight) + cmp("≫ Toggle Active"))
            else {
                add(cmp("Left-Click ", cHighlight) + cmp("≫ Toggle Active"))
                add(cmp("Right-Click ", cHighlight) + cmp("≫ Open Settings"))
            }
        }
    }
}
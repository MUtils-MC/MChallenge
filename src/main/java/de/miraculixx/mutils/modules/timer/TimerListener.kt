package de.miraculixx.mutils.modules.timer

import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.modules.timer.DeathPunish
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.ChallengeManager
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.broadcast
import de.miraculixx.mutils.utils.consoleMessage
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.runnables.task
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

object TimerListener {
    fun setModuleStatus(b: Boolean) {
        setRunning(!b, false)
        if (b) {
            onDie.register()
            onEntityDeath.register()
        } else {
            onDie.unregister()
            onEntityDeath.unregister()
            onQuit.unregister()
        }
    }

    fun setRunning(b: Boolean, ai: Boolean) {
        val chManager = ChallengeManager()
        if (!b) {
            onDamage.register()
            onInteract.register()
            onBreak.register()
            onPlace.register()
            onSpawn.register()
            onHunger.register()
            onQuit.unregister()
            chManager.unregisterChallenges(ModuleManager.getChallenges())
        } else {
            onDamage.unregister()
            onInteract.unregister()
            onBreak.unregister()
            onPlace.unregister()
            onSpawn.unregister()
            onHunger.unregister()
            onQuit.register()
        }
        if (ai)
            worlds.forEach { w ->
                w.entities.forEach { e ->
                    if (e is LivingEntity && e !is Player)
                        e.setAI(b)
                }
            }
    }

    //
    // Timer Goals
    //
    private val onDie = listen<PlayerDeathEvent>(register = false) {
        val player = it.entity
        val c = ConfigManager.getConfig(Configs.TIMER)
        val chManger = ChallengeManager()
        if (c.getBoolean("Goals.Player Death")) {
            val loc = it.entity.location
            ModuleManager.setTimerStatus(false)
            challenges = ChallengeStatus.PAUSED
            chManger.unregisterChallenges(ModuleManager.getChallenges())
            val fileBack = Config("utils/back")
            val cBack = fileBack.getConfig()
            val locString = "${loc.x} ${loc.y} ${loc.z}"
            cBack.set("World", loc.world?.name)
            onlinePlayers.forEach { p ->
                p.gameMode = GameMode.SPECTATOR
                cBack["${p.uniqueId}.Location"] = locString
                cBack["${p.uniqueId}.World"] = loc.world?.name
            }
            fileBack.save()

            broadcast(
                "\n§3§l§m======================\n" +
                        msg("modules.timer.gameOver", player, pre = false)
            )
            if (c.getBoolean("Settings.Send Location"))
                broadcast(literalText("§8>> §6Location: §e${loc.blockX} ${loc.blockY} ${loc.blockZ}") {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§9§lExact Location"), Text("§7$locString"), Text("§7${loc.world?.name}"))
                })
            if (c.getBoolean("Settings.Send Seed"))
                broadcast(literalText("§8>> §6Seed: §e${loc.world?.seed}") {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§9Click to Copy"))
                    clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, loc.world?.seed.toString())
                })
            broadcast(
                "§8>> §6Playtime: §e${ModuleManager.getTime()}\n" +
                        "§8>> ${msg("modules.timer.back", pre = false)}\n" +
                        "§3§l§m======================"
            )
        } else {
            if (!c.getBoolean("Goals.Player Death Vanilla")) {
                val loc = it.entity.location
                loc.world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                task(true, 2, 2, 2) { _ ->
                    it.entity.gameMode = GameMode.SPECTATOR
                    it.entity.teleport(loc)
                    if (c.getBoolean("World.SpecDeath")) {
                        Spectator.setSpectator(it.entity)
                    }
                }
            } else player.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false)
        }

        val punish = DeathPunish.valueOf(c.getString("Settings.Death Punishment") ?: "NOTHING")
        if (punish != DeathPunish.NOTHING) {
            val kickMsg = "§1>§m             §1[ §c§lGame Over§1 ]§1§m             §1<\n\n" +
                    "${msg("modules.timer.kick")}\n\n" +
                    "§1>§m             §1[ §c§lGame Over§1 ]§1§m             §1<\n\n"
            if (punish == DeathPunish.BAN) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(
                    it.entity.name, "\n§1>§m             §1[ §c§lGame Over§1 ]§1§m             §1<\n\n" +
                            "${msg("modules.timer.ban")}\n\n" +
                            "§1>§m             §1[ §c§lGame Over§1 ]§1§m             §1<\n\n", null, "MUtils"
                )
                player.kickPlayer(kickMsg)
            } else if (punish == DeathPunish.KICK)
                player.kickPlayer(kickMsg)
        }
    }

    private val onEntityDeath = listen<EntityDeathEvent>(register = false) {
        val c = ConfigManager.getConfig(Configs.TIMER)
        val entity = it.entity
        if (entity.type == EntityType.ENDER_DRAGON && c.getBoolean("Goals.Dragon")) finished(entity, c)
        if (entity.type == EntityType.WITHER && c.getBoolean("Goals.Wither")) finished(entity, c)
        if (entity.type == EntityType.ELDER_GUARDIAN && c.getBoolean("Goals.Elder Guardian")) finished(entity, c)
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val c = ConfigManager.getConfig(Configs.TIMER)
        if (onlinePlayers.size - 1 == 0 && c.getBoolean("Goals.Empty Server")) {
            ModuleManager.setTimerStatus(false)
            consoleMessage(msg("command.timer.stop"))
        }
    }

    private fun finished(entity: Entity, c: FileConfiguration) {
        ModuleManager.setTimerStatus(false)
        val tool = ChallengeManager()
        tool.stopChallenges(ModuleManager.getChallenges())
        challenges = ChallengeStatus.PAUSED
        broadcast("\n§3§l§m======================\n${msg("modules.timer.success", input = entity.name)}")
        if (c.getBoolean("Settings.Send Seed"))
            broadcast(literalText("§8>> §6Seed: §e${entity.world.seed}") {
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§9Click to Copy"))
                clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entity.world.seed.toString())
            })
        broadcast(msg("modules.timer.playtime", input = ModuleManager.getTime()) + "§3§l§m======================")
    }


    /*
    If timer is paused, the World should freeze...
    At least, you shouldn't be able to interact with it
     */
    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (it.cause != EntityDamageEvent.DamageCause.VOID)
            it.isCancelled = true
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE && gm != GameMode.SPECTATOR
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }
    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        it.isCancelled = true
    }

    private val onHunger = listen<FoodLevelChangeEvent>(register = false) {
        it.isCancelled = true
    }
}
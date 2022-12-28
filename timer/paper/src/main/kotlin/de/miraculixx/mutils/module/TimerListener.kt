package de.miraculixx.mutils.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.geometry.toSimpleString
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.settings
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class TimerListener {
    private var lastLocation: Location? = null

    fun disableAll() {
        onDamage.unregister()
        onDie.unregister()
        onQuit.unregister()
        onBreak.unregister()
        onHunger.unregister()
        onPlace.unregister()
        onEntityDeath.unregister()
        onInteract.unregister()
        onSpawn.unregister()
    }

    fun setRunning(active: Boolean, effectAI: Boolean) {
        //val chManager = ChallengeManager()
        if (!active) {
            onDamage.register()
            onInteract.register()
            onBreak.register()
            onPlace.register()
            onSpawn.register()
            onHunger.register()
            onQuit.unregister()

            onDie.unregister()
            onEntityDeath.unregister()
            onQuit.unregister()
            //chManager.unregisterChallenges(ModuleManager.getChallenges())
        } else {
            onDamage.unregister()
            onInteract.unregister()
            onBreak.unregister()
            onPlace.unregister()
            onSpawn.unregister()
            onHunger.unregister()

            onQuit.register()
            onDie.register()
            onEntityDeath.register()
        }
        if (effectAI)
            worlds.forEach { w ->
                w.entities.forEach { e ->
                    if (e is LivingEntity && e !is Player)
                        e.setAI(active)
                }
            }
    }

    //
    // Timer Goals
    //
    private val onDie = listen<PlayerDeathEvent>(register = false) {
        val player = it.entity
        val timer = TimerManager.getGlobalTimer()
        //val chManger = ChallengeManager() TODO Adding Challenge API
        if (settings.getBoolean("Goals.Player")) {
            val loc = it.entity.location
            timer.running = false
            //challenges = ChallengeStatus.PAUSED
            //chManger.unregisterChallenges(ModuleManager.getChallenges())
            lastLocation = loc
            val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
            var cmp = dash + msg("modules.timer.gameOver", listOf(player.name))

            if (settings.getBoolean("Settings.Send Location"))
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp(msgString("event.location"), NamedTextColor.GOLD) + cmp("${loc.blockX} ${loc.blockY} ${loc.blockZ}", NamedTextColor.YELLOW))
                    .addHover(
                        cmp(msgString("event.exactLocation"), cHighlight, bold = true) + cmp(" ${loc.toSimpleString()}") +
                                cmp(msgString("event.world"), cHighlight, bold = true) + cmp(loc.world.name)
                    )

            if (settings.getBoolean("Settings.Send Seed")) {
                val seed = loc.world.seed.toString()
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("Seed", NamedTextColor.GOLD) + cmp(seed, NamedTextColor.YELLOW))
                    .addHover(cmp(msgString("event.clickCopy"), cHighlight))
                    .clickEvent(ClickEvent.copyToClipboard(seed))
            }

            cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + cmp(msgString("event.playtime"), NamedTextColor.GOLD) + cmp(timer.buildSimple(), NamedTextColor.YELLOW)

            if (settings.getBoolean("Settings.Teleport Back"))
                cmp = cmp + cmp("\n>> ", NamedTextColor.DARK_GRAY) + msg("event.backPrompt")

            broadcast(cmp + dash)
        } else {
            if (!settings.getBoolean("Goals.Player Death Vanilla")) {
                val loc = it.entity.location
                val immediateRespawn = loc.world?.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN)
                loc.world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                task(true, 2, 2, 2, endCallback = {
                    loc.world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, immediateRespawn ?: false)
                }) { _ ->
                    it.entity.gameMode = GameMode.SPECTATOR
                    it.entity.teleport(loc)
                    if (settings.getBoolean("World.SpecDeath")) {
                        //Spectator.setSpectator(it.entity) TODO - Adding Spectator Tools API
                    }
                }
            }
        }

        /*val punish = DeathPunish.valueOf(c.getString("Settings.Death Punishment") ?: "NOTHING")
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


        }*/
    }

    private val onEntityDeath = listen<EntityDeathEvent>(register = false) {
        val entity = it.entity
        val type = entity.type
        when {
            type == EntityType.ENDER_DRAGON -> if (settings.getBoolean("Goals.Dragon")) finished(entity, TimerManager.getGlobalTimer())
            type == EntityType.WITHER -> if (settings.getBoolean("Goals.Wither")) finished(entity, TimerManager.getGlobalTimer())
            type == EntityType.ELDER_GUARDIAN -> if (settings.getBoolean("Goals.ElderGuardian")) finished(entity, TimerManager.getGlobalTimer())
            majorVersion >= 19 && type == EntityType.WARDEN -> if (settings.getBoolean("Goals.Warden")) finished(entity, TimerManager.getGlobalTimer())
            else -> Unit
        }
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val pTimer = TimerManager.getPersonalTimer(it.player.uniqueId)
        pTimer?.running = false

        if (onlinePlayers.size <= 1 && settings.getBoolean("Goals.LastPlayer")) {
            TimerManager.getGlobalTimer().running = false
            console.sendMessage(msg("command.stop", listOf("Console")))
        }
    }

    private fun finished(entity: Entity, timer: Timer) {
        timer.running = false
        //val tool = ChallengeManager()
        //tool.stopChallenges(ModuleManager.getChallenges())
        //challenges = ChallengeStatus.PAUSED
        val dash = cmp("\n======================\n", NamedTextColor.DARK_AQUA, bold = true, strikethrough = true)
        var final = dash + msg("event.endSuccess", listOf(entity.name))

        if (settings.getBoolean("Settings.Send Seed")) {
            val seed = entity.world.seed.toString()
            final += cmp("\n>> ", NamedTextColor.DARK_GRAY) + (cmp("Seed", NamedTextColor.GOLD) + cmp(seed, NamedTextColor.YELLOW))
                .addHover(cmp(msgString("event.clickCopy"), cHighlight))
                .clickEvent(ClickEvent.copyToClipboard(seed))
        }

        broadcast(final + cmp(msgString("event.playtime"), NamedTextColor.GOLD) + cmp(" ${timer.buildSimple()}") + dash)
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
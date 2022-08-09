package de.miraculixx.mutils.modules

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.modules.timer.TimerDesign
import de.miraculixx.mutils.enums.modules.timer.TimerSettings
import de.miraculixx.mutils.enums.modules.timer.TimerValue
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.challenge.ChallengeManager
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.speedrun.SpeedrunListener
import de.miraculixx.mutils.modules.timer.Timer
import de.miraculixx.mutils.modules.timer.TimerListener
import de.miraculixx.mutils.modules.utils.back.BackListener
import de.miraculixx.mutils.modules.utils.backpack.BackPackListener
import de.miraculixx.mutils.modules.worldManager.WorldTools
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.prefix
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration

/*
Global quick access
 */
var challenges = ChallengeStatus.STOPPED

object ModuleManager {
    private val moduleMap = HashMap<Modules, Boolean>()

    /*
     << Challenge managing >>
     Loaded Challenges get stored with all integrated Listeners.
     Listener should always be disabled before removing class from scope
     */
    private val chClasses = ArrayList<Challenge>()

    fun getChallenges(): List<Challenge> {
        return buildList {
            addAll(chClasses)
            addAll(CreatorManager.getAllChallenges())
        }
    }

    fun startChallenges(): Boolean {
        val manager = ChallengeManager()
        if (challenges != ChallengeStatus.STOPPED)
            manager.stopChallenges(chClasses)
        chClasses.clear()
        chClasses.addAll(manager.startChallenges() ?: return false)
        return true
    }

    private fun addChallenge(challenge: Challenge): Boolean {
        return if (chClasses.contains(challenge)) false
        else {
            chClasses.add(challenge)
            true
        }
    }

    private fun removeChallenge(challenge: Challenge): Boolean {
        return if (chClasses.contains(challenge)) {
            chClasses.remove(challenge)
            true
        } else false
    }

    /*
    << Timer managing >>
    Timer data get stored in Timer class
     */
    private val timer = Timer()
    fun getTime(formatted: Boolean = false): String {
        return if (formatted) timer.titleBuilder() else timer.getTime()
    }

    fun setTimerStatus(b: Boolean) {
        if (timer.running == b) return
        TimerListener.setRunning(b, true)
        timer.running = b
    }

    fun getTimerStatus(): Boolean {
        return timer.running
    }

    fun setTime(day: Int? = null, hour: Int? = null, min: Int? = null, sec: Int? = null) {
        timer.setTime(day, hour, min, sec)
    }

    fun addTime(day: Int = 0, hour: Int = 0, min: Int = 0, sec: Int = 0): Boolean {
        return timer.addTime(day, hour, min, sec)
    }

    fun timerSettings(c: FileConfiguration, s: TimerSettings, i1: Char? = null, i2: TimerDesign? = null, i3: Boolean? = null) {
        when (s) {
            TimerSettings.COUNT_UP -> {
                val f = i3 ?: true
                timer.up = f
                c["Settings.Count Up"] = f
            }
            TimerSettings.COLOR_PRIMARY -> {
                val f = i1 ?: '6'
                timer.color1 = i1 ?: '6'
                c["Style.Primary Color"] = f
            }
            TimerSettings.COLOR_SECONDARY -> {
                val f = i1 ?: '6'
                timer.color2 = i1 ?: '6'
                c["Style.Secondary Color"] = f
            }
            TimerSettings.STYLE_PRIMARY -> {
                val f = i1 ?: 'l'
                timer.style1 = i1 ?: 'l'
                c["Style.Primary Style"] = f
            }
            TimerSettings.STYLE_SECONDARY -> {
                val f = i1 ?: 'l'
                timer.style2 = i1 ?: 'l'
                c["Style.Secondary Style"] = f
            }
            TimerSettings.DESIGN -> {
                val f = i2 ?: TimerDesign.COMPACT
                timer.design = f
                c["Style.Design"] = f.name
            }
        }
    }

    fun timerSettings(s: TimerSettings): Any {
        return when (s) {
            TimerSettings.COUNT_UP -> timer.up
            TimerSettings.COLOR_PRIMARY -> timer.color1
            TimerSettings.COLOR_SECONDARY -> timer.color2
            TimerSettings.STYLE_PRIMARY -> timer.style1
            TimerSettings.STYLE_SECONDARY -> timer.style2
            TimerSettings.DESIGN -> timer.design
        }
    }

    /*
    << Module managing >>
    Global Methods that apply on every Module
     */
    fun isActive(module: Modules): Boolean {
        return moduleMap[module]!!
    }

    fun disableModule(module: Modules): Boolean {
        moduleMap[module] = false
        when {
            module == Modules.TIMER -> {
                timer.setActive(false)
                TimerListener.setModuleStatus(false)
            }
            module == Modules.SPEEDRUN -> SpeedrunListener.unregister()
            module.isChallenge() -> {
                val m = ChallengeManager()
                val cl = m.getClass(module) ?: return false
                removeChallenge(cl)
            }
        }
        return true
    }

    fun enableModule(module: Modules?): Boolean {
        val version = Bukkit.getVersion()
        when (module) {
            //Version bounded
            Modules.CAPTIVE -> {
                val v = "1.17.1"
                if (!version.contains(v)) {
                    broadcast(msg("modules.global.outdated", input = "Captive Challenge", input2 = v))
                    broadcast("$prefix ")
                    return false
                }
            }

            //Utilitys
            Modules.TIMER -> {
                TimerListener.setModuleStatus(true)
                timer.setActive(true)
            }
            Modules.BACK -> BackListener
            Modules.BACKPACK -> BackPackListener
            Modules.SPEEDRUN -> SpeedrunListener.register()
            null -> return false
            else -> {}
        }
        moduleMap[module] = true
        return true
    }

    /*
    Config managing
    Load data from Disk or save it back
     */
    private fun load() {
        val c = ConfigManager.getConfig(Configs.MODULES)
        Modules.values().forEach { s: Modules ->
            val active = c.getBoolean("${s.name}.Active")
            moduleMap[s] = active
            if (active) enableModule(s)
        }
        taskRunLater(20) {
            val wTools = WorldTools()
            wTools.loadWorlds()
        }
    }

    fun save() {
        val c = ConfigManager.getConfig(Configs.MODULES)
        moduleMap.forEach { (s, b) ->
            c["${s.name}.Active"] = b
        }
        val cT = ConfigManager.getConfig(Configs.TIMER)
        cT["Time.Seconds"] = timer.getTime(TimerValue.SECONDS)
        cT["Time.Minutes"] = timer.getTime(TimerValue.MINUTES)
        cT["Time.Hours"] = timer.getTime(TimerValue.HOURS)
        cT["Time.Days"] = timer.getTime(TimerValue.DAYS)
    }

    fun shutDown() {
        chClasses.forEach {
            if (challenges != ChallengeStatus.STOPPED) it.stop()
            it.unregister()
        }
        timer.setActive(false)
    }

    init {
        load()
    }
}
package de.miraculixx.mutils.modules.timer

import de.miraculixx.mutils.enums.modules.timer.TimerDesign
import de.miraculixx.mutils.enums.modules.timer.TimerValue
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.ChallengeManager
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.GameMode
import org.bukkit.Sound
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
class Timer {
    private var time = Duration.ZERO
    var running = false
    private var activated = false
    private val title: String
        get() = titleBuilder()
    private val mDay: String
    private val mDays: String

    // Settings
    var up: Boolean
    var design: TimerDesign
    var color1: Char
    var color2: Char
    var style1: Char
    var style2: Char

    init {
        val c = ConfigManager.getConfig(Configs.TIMER)
        up = c.getBoolean("Settings.Count Up")
        design = TimerDesign.valueOf(c.getString("Style.Design") ?: "COMPACT")
        // Why the hell config.getChar() does not exist?
        color1 = (c.getString("Style.Primary Color") ?: "6")[0]
        color2 = (c.getString("Style.Secondary Color") ?: "6")[0]
        style1 = (c.getString("Style.Primary Style") ?: "l")[0]
        style2 = (c.getString("Style.Secondary Style") ?: "l")[0]

        time = c.getInt("Time.Seconds").seconds.plus(
            c.getInt("Time.Minutes").minutes
        ).plus(
            c.getInt("Time.Hours").hours
        ).plus(
            c.getInt("Time.Days").days
        )
        val d = msg("modules.timer.days", pre = false)
        mDay = d.split('/')[0]
        mDays = d.replace("/", "")
    }

    fun setActive(b: Boolean) {
        if (b == activated) return
        activated = b
        if (b) run()
    }

    fun getTime(value: TimerValue): Int {
        time.toComponents { days, hours, minutes, seconds, _ ->
            if (value == TimerValue.DAYS) return days.toInt()
            if (value == TimerValue.HOURS) return hours
            if (value == TimerValue.MINUTES) return minutes
            if (value == TimerValue.SECONDS) return seconds
            return 0
        }
    }

    fun getTime(): String {
        var string = ""
        time.toComponents { days, hours, minutes, seconds, _ ->
            if (days != 0L && days > 1L) string += "$days $mDays "
            if (days != 0L && days == 1L) string += "$days $mDay "
            string += p1(hours, minutes, seconds)
        }
        return string
    }

    @Suppress("DuplicatedCode")
    fun setTime(pDay: Int? = null, pHour: Int? = null, pMin: Int? = null, pSec: Int? = null) {
        var dummy = Duration.ZERO
        time.toComponents { days, hours, minutes, seconds, _ ->
            if (pDay == null) dummy += days.days
            if (pHour == null) dummy += hours.hours
            if (pMin == null) dummy += minutes.minutes
            if (pSec == null) dummy += seconds.seconds

            if (pDay != null) dummy += pDay.days
            if (pHour != null) dummy += pHour.hours
            if (pMin != null) dummy += pMin.minutes
            if (pSec != null) dummy += pSec.seconds
        }
        time = dummy
    }

    fun addTime(day: Int = 0, hour: Int = 0, min: Int = 0, sec: Int = 0): Boolean {
        val adder = day.days.plus(hour.hours).plus(min.minutes).plus(sec.seconds)
        return if (!(adder + time).isNegative()) {
            time += adder
            true
        } else false
    }

    /*
    * Anti Duplication Area
    * Instead of going crazy with formatting,
    * I decide to create those anti-dupe methods.
     */
    private fun p1(h: Int, m: Int, s: Int): String {
        var t = ""
        if (h != 0 && h in 1..9) t += "0$h:"
        if (h != 0 && h >= 10) t += "$h:"
        t += p2(m, s)
        return t
    }

    private fun p2(m: Int, s: Int): String {
        var t = ""
        if (m in 0..9) t += "0$m:"
        if (m >= 10) t += "$m:"
        if (s in 0..9) t += "0$s"
        if (s >= 10) t += "$s"
        return t
    }

    fun titleBuilder(): String {
        var string = ""
        val c1 = "§$color1"
        val c2 = "§$color2"
        val s1 = "§$style1"
        val s2 = "§$style2"
        time.toComponents { days, hours, minutes, seconds, _ ->
            when (design) {
                TimerDesign.COMPACT -> {
                    if (days != 0L && days > 1L) string += "$c1$s1$days $mDays "
                    if (days != 0L && days == 1L) string += "$c1$s1$days $mDay "
                    if (hours != 0 && hours in 1..9) string += "$c1${s1}0$hours$c2${s2}:"
                    if (hours != 0 && hours >= 10) string += "$c1$s1$hours$c2${s2}:"
                    if (minutes in 0..9) string += "$c1${s1}0$minutes$c2${s2}:"
                    if (minutes >= 10) string += "$c1$s1$minutes$c2${s2}:"
                    if (seconds in 0..9) string += "$c1${s1}0$seconds"
                    if (seconds >= 10) string += "$c1$s1$seconds"
                }
                TimerDesign.BRACKETS -> {
                    string += "$c2$s2[$c1$s1"
                    if (days != 0L) string += "$days "
                    string += p1(hours, minutes, seconds)
                    string += "$c2$s2]"
                }
                TimerDesign.PREFIX -> {
                    string += "$c2${s2}Challenge: $c1$s1"
                    if (days != 0L) string += "$days "
                    if (hours in 1..9) string += "0$hours:"
                    if (hours >= 10) string += "$hours:"
                    string += p2(minutes, seconds)
                }
                TimerDesign.EXACT -> {
                    if (days != 0L) string += "$c1$s1$days$c2${s2}d "
                    if (hours != 0) string += "$c1$s1$hours$c2${s2}h "
                    if (minutes != 0) string += "$c1$s1$minutes$c2${s2}m "
                    if (seconds != 0) string += "$c1$s1$seconds$c2${s2}s"
                }
            }
        }
        return string
    }

    private fun timeOver() {
        running = false
        val chManager = ChallengeManager()
        chManager.stopChallenges(ModuleManager.getChallenges())
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) {
                player.damage(0.01)
                player.gameMode = GameMode.SPECTATOR
            }
            player.sendTitle("§cTime Over", "", 10, 30, 10)
            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f)
        }
        broadcast("\n\n§3§l§m======================\n" +
                msg("modules.timer.timeOver", pre = false) +
                msg("modules.timer.playtime", input = getTime(), pre = false) +
                msg("modules.timer.back", pre = false) +
                "§3§l§m======================"
        )
    }

    private fun run() {
        task(false, 20, 20) {
            if (!activated) {
                it.cancel()
                onlinePlayers.forEach { p ->
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, literalText(" "))
                }
                return@task
            }
            if (!running) {
                val textTitle = literalText("${msg("modules.timer.paused", pre = false)} (${getTime()})") {
                    color = ChatColor.getByChar(color1)
                    italic = true
                }
                onlinePlayers.forEach { p ->
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, textTitle)
                }

                /*
                 Spigot's Mapping Error prevention
                 After some time with deactivated Timer, something
                 get unloaded and lead to a Mapping Error...
                 Tested in 1.16.5 TODO(Test in newer Versions)
                 */
                time += 0.seconds
                return@task
            }

            if (up)
                time += 1.seconds
            else {
                val adder = 1.seconds
                if (!(time - adder).isNegative()) {
                    time -= adder
                    if (time.isNegative()) time = Duration.ZERO
                } else {
                    sync {
                        timeOver()
                    }
                }
            }

            val textTitle = literalText(title)
            onlinePlayers.forEach { p ->
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, textTitle)
            }
        }
    }
}
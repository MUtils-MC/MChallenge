package de.miraculixx.mutils.enums.modules.timer

@Suppress("GrazieInspection")
enum class TimerDesign {
    COMPACT, //min:sec -> h:min:sec -> d Tage h:min:sec
    BRACKETS, //[min:sec] -> [h:min:sec] -> [d h:min:sec]
    PREFIX, //Challenge: h:min:sec -> Challenge: d h:min:sec
    EXACT //sec s -> min m sec s -> h h min m sec s -> d d h h min m sec s
}
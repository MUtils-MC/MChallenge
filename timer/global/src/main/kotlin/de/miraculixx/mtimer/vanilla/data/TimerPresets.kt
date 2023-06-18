package de.miraculixx.mtimer.data

import java.util.*

@Suppress("BooleanLiteralArgument")
enum class TimerPresets(val uuid: UUID, val design: TimerDesign) {
    PRESET(
        UUID.fromString("41fb61c2-7c9c-11ed-a1eb-0242ac120002"), TimerDesign(
          TimerDesignPart(
              "<prefix><d><h><m><s><suffix>",
              TimerDesignValue(false, false, "", " "),
              TimerDesignValue(true, true, "", " "),
              TimerDesignValue(true, true, "", " "),
              TimerDesignValue(true, true, "", " "),
              TimerDesignValue(false, true, "", ""),
              "", "", 0.05f
          ),
            TimerDesignPart(
                "<prefix><d><h><m><s><suffix>",
                TimerDesignValue(false, false, "", " "),
                TimerDesignValue(true, true, "", " "),
                TimerDesignValue(true, true, "", " "),
                TimerDesignValue(true, true, "", " "),
                TimerDesignValue(false, true, "", ""),
                "", "", 0.05f
            ), "New Design", "MUtils"
        )
    ),

    CLASSIC(
        UUID.fromString("aaca0008-7492-11ed-a1eb-0242ac120002"), TimerDesign(
            TimerDesignPart(
                "<prefix><d><h><m><s><suffix>",
                TimerDesignValue(false, false, "", " "),
                TimerDesignValue(true, false, "", ":"),
                TimerDesignValue(true, true, "", ":"),
                TimerDesignValue(true, true, "", ""),
                TimerDesignValue(false, true, "", ""),
                "<b><gold>", "", 0f
            ),
            TimerDesignPart(
                "<prefix><d><h><m><s><suffix>",
                TimerDesignValue(false, false, "", " "),
                TimerDesignValue(true, false, "", ":"),
                TimerDesignValue(true, true, "", ":"),
                TimerDesignValue(true, true, "", ""),
                TimerDesignValue(false, true, "", ""),
                "<red><i>Timer Paused (", ")", 0f
            ), "Classic", "MUtils"
        )
    ),

    GALAXY(
        UUID.fromString("43d8e7e8-78a1-11ed-a1eb-0242ac120002"), TimerDesign(
            TimerDesignPart(
                "<prefix><d><h><m><s><suffix>",
                TimerDesignValue(false, false, "", "d "),
                TimerDesignValue(false, false, "", "h "),
                TimerDesignValue(false, false, "", "m "),
                TimerDesignValue(false, true, "", "s"),
                TimerDesignValue(false, true, "", ""),
                "<b><gradient:#707CF7:#F658CF:<x>>", "", 0.03f
            ),
            TimerDesignPart(
                "<prefix><d><h><m><s><suffix>",
                TimerDesignValue(false, false, "", "d "),
                TimerDesignValue(false, false, "", "h "),
                TimerDesignValue(false, false, "", "m "),
                TimerDesignValue(false, true, "", "s"),
                TimerDesignValue(false, true, "", ""),
                "<b><gradient:#707CF7:#F658CF:<x>>Paused (", ")", 0.03f
            ), "Galaxy (Animated)", "MUtils"
        )
    )
    ;

    companion object {
        private val errorValue = TimerDesignValue(false, false, "", "")
        private val errorPart = TimerDesignPart("<red>Unknown Exception", errorValue, errorValue, errorValue, errorValue, errorValue, "", "", 0f)
        val error = TimerDesign(errorPart, errorPart, "Error", "MUtils")
    }
}
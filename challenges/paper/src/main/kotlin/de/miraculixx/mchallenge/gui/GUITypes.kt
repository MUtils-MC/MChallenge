package de.miraculixx.mchallenge.gui

import de.miraculixx.mvanilla.messages.cHighlight
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

enum class GUITypes(val title: Component) {
    CH_LIMITED_SKILLS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Limited Skills", cHighlight)),
    CH_LOW_VISION(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Select Blocks", cHighlight)),

    COMPETITION(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Create Competition", cHighlight)),
    CHALLENGE_SETTINGS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Challenge Settings", cHighlight)),
    SETTINGS_CUSTOM_3(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Settings", cHighlight)),
    SPEC_PLAYER_OVERVIEW(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Online Player", cHighlight)),
    CHALLENGE_MENU(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Challenges", cHighlight)),
    MAIN_MENU(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Challenge Menu", cHighlight));
}
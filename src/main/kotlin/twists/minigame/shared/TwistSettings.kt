package twists.minigame.shared

import net.casual.arcade.minigame.settings.MinigameSettings
import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults

open class TwistSettings(
    minigame: TwistedMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): MinigameSettings(minigame, defaults) {

}
package twists.minigame

import net.casual.arcade.minigame.settings.MinigameSettings
import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.float64
import net.casual.arcade.utils.ItemUtils.named
import net.minecraft.world.item.Items

open class TwistSettings(
    minigame: TwistedMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): MinigameSettings(minigame, defaults) {



}
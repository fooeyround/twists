package twists.minigame.manhunt

import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import twists.minigame.shared.TwistSettings
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.bool
import net.casual.arcade.utils.ItemUtils.named
import net.minecraft.world.item.Items

class ManhuntSettings(
    minigame: ManhuntMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): TwistSettings(minigame, defaults) {

    var intentionalGameDesign by this.register(bool {
        name = "intentional_game_design"
        display = Items.WIND_CHARGE.named("Enable Intentional Game Design")
        value = true
    })

}
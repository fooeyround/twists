package twists.minigame.manhunt

import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import twists.minigame.shared.TwistSettings
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.bool
import net.casual.arcade.minigame.settings.display.*
import net.casual.arcade.utils.ItemUtils.named
import net.minecraft.world.item.Items

class ManhuntSettings(
    minigame: ManhuntMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): TwistSettings(minigame, defaults) {

    var intentionalGameDesign by this.register(bool {
        name = "intentional_game_design"
        display = Items.BED.red().named("Intentional Game Design")
        value = true
        defaults.options(this)
    })

    var communalPocketsRunnersOnly by this.register(bool {
        name = "communal_pockets_runners_only"
        display = Items.RABBIT_FOOT.named("Communal Pockets only applies to the runner")
        value = false
        defaults.options(this)
    })

    var hardcoreHunters by this.register(bool {
        name = "hardcore_hunters"
        display = Items.TOTEM_OF_UNDYING.named("Hardcore Hunters")
        value = false
        defaults.options(this)
    })
}
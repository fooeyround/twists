package twists.minigame.collective_survival

import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import twists.minigame.shared.TwistSettings
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.time
import net.casual.arcade.utils.ItemUtils
import net.casual.arcade.utils.ItemUtils.named
import net.casual.arcade.utils.TimeUtils.Minutes
import net.casual.arcade.utils.TimeUtils.Ticks
import net.minecraft.world.item.Items
import kotlin.math.min


class CollectiveSurvivalSettings(
    minigame: CollectiveSurvivalMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): TwistSettings(minigame, defaults) {

    var itemDropCooldown by this.register(time {
        name = "item_drop_cooldown"
        display = Items.ITEM_FRAME.named("Item Drop Cooldown")
        value = 0.Ticks

        option("disabled", ItemUtils.light(0).named("Disabled"), 0.Ticks)
        for (i in 1..20) {
            option("${i}_min", ItemUtils.light(min(i, 15)).named("$i min"), i.Minutes)
        }
    })

}
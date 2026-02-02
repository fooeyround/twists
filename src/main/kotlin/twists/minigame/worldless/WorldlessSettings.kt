package twists.minigame.worldless

import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import twists.minigame.TwistSettings
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.time
import net.casual.arcade.utils.ItemUtils
import net.casual.arcade.utils.ItemUtils.named
import net.casual.arcade.utils.TimeUtils.Minutes
import net.casual.arcade.utils.TimeUtils.Ticks
import net.casual.arcade.utils.time.MinecraftTimeDuration
import net.minecraft.world.item.Items

class WorldlessSettings(
    minigame: WorldlessMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): TwistSettings(minigame, defaults) {

    var worldResetTime by this.register(time {
        name = "world_reset_time"
        display = Items.GRINDSTONE.named("World Reset Time")
        value = 5.Minutes

        for (i in 1..10) {
            option("${i}_min", ItemUtils.light(i).named("$i min"), i.Minutes)
        }
    })

}
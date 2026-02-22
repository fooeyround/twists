package twists.minigame.deathswap

import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import twists.minigame.TwistSettings
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.time
import net.casual.arcade.utils.ItemUtils
import net.casual.arcade.utils.ItemUtils.named
import net.casual.arcade.utils.TimeUtils.Minutes
import net.minecraft.world.item.Items
import kotlin.math.max


class DeathSwapSettings(
    minigame: DeathSwapMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): TwistSettings(minigame, defaults) {

    var deathSwapCooldown by this.register(time {
        name = "death_swap_cooldown"
        display = Items.WIND_CHARGE.named("Death Swap Cooldown")
        value = 5.Minutes

        for (i in 1..20) {
            option("${i}_min", ItemUtils.light(max(i, 15)).named("$i min"), i.Minutes)
        }
    })

}
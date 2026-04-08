package twists.minigame.shared

import net.casual.arcade.minigame.settings.MinigameSettings
import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.bool
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.time
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.enumeration
import net.casual.arcade.utils.ItemUtils
import net.casual.arcade.utils.ItemUtils.named
import net.casual.arcade.utils.TimeUtils.Minutes
import net.casual.arcade.utils.TimeUtils.Ticks
import net.minecraft.world.item.Items
import twists.extension.SharedInventoryTeamExtension
import kotlin.math.min


open class TwistSettings(
    minigame: TwistedMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): MinigameSettings(minigame, defaults) {

    var communalPockets by this.register(enumeration<SharedInventoryTeamExtension.ShareLevel> {
        name = "communal_pockets"
        display = Items.BUNDLE.named("Communal Pockets")
        value = SharedInventoryTeamExtension.ShareLevel.None
        option("none", Items.BARRIER.named("None"), SharedInventoryTeamExtension.ShareLevel.None)
        option(
            "only_main_inventory",
            Items.DIAMOND_PICKAXE.named("Only Main Inventory"),
            SharedInventoryTeamExtension.ShareLevel.Inventory
        )
        option(
            "inventory_and_equipment",
            Items.DIAMOND_CHESTPLATE.named("Inventory And Equipment"),
            SharedInventoryTeamExtension.ShareLevel.InventoryAndEquipment
        )
    })

    var randomizeLootTables by this.register(bool {
        name = "randomize_loot_tables"
        display = Items.CHEST.named("Randomize Loot")
        value = false
        defaults.options(this)
    })

    var randomItemOnInterval by this.register(time {
        name = "random_items_on_interval"
        display = Items.BUNDLE.named("Communal Pockets")
        value = 0.Ticks
        option("disabled", Items.BARRIER.named("Disabled"), 0.Ticks)
        for (i in 1..20) {
            option("${i}_min", ItemUtils.light(min(i, 15)).named("$i min"), i.Minutes)
        }
    })

}
package twists.minigame.shared

import net.casual.arcade.minigame.settings.MinigameSettings
import net.casual.arcade.minigame.settings.display.DisplayableSettingsDefaults
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.bool
import net.casual.arcade.minigame.settings.display.MenuGameSettingBuilder.Companion.enumeration
import net.casual.arcade.utils.ItemUtils.named
import net.minecraft.world.item.Items
import twists.extension.SharedInventoryTeamExtension


open class TwistSettings(
    minigame: TwistedMinigame,
    defaults: DisplayableSettingsDefaults = DisplayableSettingsDefaults()
): MinigameSettings(minigame, defaults) {

    var communalPockets by this.register(enumeration<SharedInventoryTeamExtension.ShareLevel> {
        name = "communal_pockets"
        display = Items.BUNDLE.named("Communal Pockets")
        value = SharedInventoryTeamExtension.ShareLevel.None
        option("none", Items.BARRIER.named("None"), SharedInventoryTeamExtension.ShareLevel.None)
        option("only_main_inventory", Items.DIAMOND_PICKAXE.named("Only Main Inventory"), SharedInventoryTeamExtension.ShareLevel.Inventory)
        option("inventory_and_equipment", Items.DIAMOND_CHESTPLATE.named("Inventory And Equipment"), SharedInventoryTeamExtension.ShareLevel.InventoryAndEquipment)
    })

}
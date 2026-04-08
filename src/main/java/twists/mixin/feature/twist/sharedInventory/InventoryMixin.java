package twists.mixin.feature.twist.sharedInventory;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.casual.arcade.extensions.utils.ExtensionUtilsKt;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twists.extension.SharedInventoryTeamExtension;

/// While this isn't the best way to do this,
/// we will simply redirect to the team's extension inventory if enabled for everything except serialization.
@Mixin(Inventory.class)
public class InventoryMixin {

    /// Purposefully does not include save/load, that occurs in the extension itself
    @ModifyExpressionValue(
            method = {
                    "getSelectedItem", "setSelectedItem", "getNonEquipmentItems", "getFreeSlot", "addAndPickItem", "pickSlot",
                    "findSlotMatchingItem", "findSlotMatchingCraftingIngredient", "getSuitableHotbarSlot", "getSlotWithRemainingSpace",
                    "tick", "add(ILnet/minecraft/world/item/ItemStack;)Z", "removeItem*", "removeItemNoUpdate", "setItem", "getContainerSize",
                    "isEmpty", "getItem", "dropAll", "clearContent", "fillStackedContents",

            },
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Inventory;items:Lnet/minecraft/core/NonNullList;", opcode = Opcodes.GETFIELD)
    )
    private NonNullList<ItemStack> twists$sharedInventories$teamInventoryItemsRedirect(NonNullList<ItemStack> original) {
        if (((Inventory)(Object)(this)).player.getTeam() instanceof PlayerTeam team) {
            SharedInventoryTeamExtension extension = ExtensionUtilsKt.getExtension(team, SharedInventoryTeamExtension.class);
            if (extension.getShareLevel() != SharedInventoryTeamExtension.ShareLevel.None) {
                return extension.getInventory().getItems();
            }
        }
        return original;
    }



}

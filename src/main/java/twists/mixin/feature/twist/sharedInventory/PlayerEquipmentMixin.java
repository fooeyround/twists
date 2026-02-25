package twists.mixin.feature.twist.sharedInventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.casual.arcade.extensions.utils.ExtensionUtilsKt;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerEquipment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import twists.extension.SharedInventoryTeamExtension;

@Mixin(PlayerEquipment.class)
public class PlayerEquipmentMixin {

    @Shadow
    @Final
    private Player player;

    @WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityEquipment;set(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack twists$sharedInventories$teamInventoryItemsRedirect$set(PlayerEquipment instance, EquipmentSlot slot, ItemStack stack, Operation<ItemStack> original) {
        EntityEquipment shared = this.getSharedEquipment();
        if (shared != null) {
            return shared.set(slot, stack);
        }
        return original.call(instance, slot, stack);
    }

    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityEquipment;get(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack twists$sharedInventories$teamInventoryItemsRedirect$get(PlayerEquipment instance, EquipmentSlot slot, Operation<ItemStack> original) {
        EntityEquipment shared = this.getSharedEquipment();
        if (shared != null) {
            return shared.get(slot);
        }
        return original.call(instance, slot);
    }

    @WrapOperation(method = "isEmpty", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityEquipment;isEmpty()Z"))
    private boolean twists$sharedInventories$teamInventoryItemsRedirect$isEmpty(PlayerEquipment instance, Operation<Boolean> original) {
        EntityEquipment shared = this.getSharedEquipment();
        if (shared != null) {
            return shared.isEmpty();
        }
        return original.call(instance);
    }


    @Unique
    @Nullable
    private EntityEquipment getSharedEquipment() {
        if (this.player.getTeam() instanceof PlayerTeam team) {
            SharedInventoryTeamExtension extension = ExtensionUtilsKt.getExtension(team, SharedInventoryTeamExtension.class);
            if (extension.getShareLevel() == SharedInventoryTeamExtension.ShareLevel.InventoryAndEquipment) {
                return extension.getInventory().getEquipment();
            }
        }
        return null;
    }
}

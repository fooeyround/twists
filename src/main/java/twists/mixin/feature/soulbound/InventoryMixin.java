package twists.mixin.feature.soulbound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import twists.Twists;


@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow @Final public Player player;

    @ModifyExpressionValue(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean twists$soulbound$keepSoulboundItemsInInventory(boolean original, @Local ItemStack stack) {
        try {
            return original || stack.getEnchantments().getLevel(this.player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Twists.SOULBOUND_ENCHANTMENT)) > 0;
        } catch (Exception e) {
            Twists.getLOGGER().error("Soulbound enchantment not found: {}", String.valueOf(e));
            return original;
        }
    }
}

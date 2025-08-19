package twists.mixin.feature.soulbound;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twists.Twists;

@Mixin(EntityEquipment.class)
public class EntityEquipmentMixin {

    @WrapWithCondition(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static boolean twists$soulbound$entityEquipmentKeepSoulbound(LivingEntity entity, ItemStack stack, boolean randomizeMotion, boolean includeThrower) {
        if (entity instanceof Player) {
            try {
                return stack.getEnchantments().getLevel(entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Twists.SOULBOUND_ENCHANTMENT)) <= 0;
            } catch (Exception e) {
                Twists.getLOGGER().error("Soulbound enchantment not found: {}", String.valueOf(e));
            }
        }
        return true;
    }

}

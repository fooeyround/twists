package twists.mixin.feature.worldless;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twists.worldless.WorldlessUtil;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {


    @ModifyExpressionValue(method = "isOverworldOrNether", at = {
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;")})
    private static RegistryKey<World> twists$worldless$portalLightingFix(RegistryKey<World> original, @Local(argsOnly = true) World world) {
        return WorldlessUtil.getActiveWorldForVanilla(world.getServer(), original);

    }
}

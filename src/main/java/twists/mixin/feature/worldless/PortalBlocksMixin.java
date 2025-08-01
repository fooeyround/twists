package twists.mixin.feature.worldless;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import twists.worldless.WorldlessUtil;


@Mixin({NetherPortalBlock.class, EndPortalBlock.class})
public class PortalBlocksMixin {

    @SuppressWarnings("MixinAnnotationTarget")
    @ModifyExpressionValue(method = "createTeleportTarget", at = {
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;")})
    private RegistryKey<World> twists$worldless$portalFix(RegistryKey<World> original, @Local(argsOnly = true)ServerWorld world) {
        return WorldlessUtil.getActiveWorldForVanilla(world.getServer(), original);
    }
}

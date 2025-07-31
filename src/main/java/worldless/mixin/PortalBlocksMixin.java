package worldless.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import worldless.Worldless;
import worldless.WorldlessState;
import worldless.WorldlessStateHolder;


@Mixin({NetherPortalBlock.class, EndPortalBlock.class})
public class PortalBlocksMixin {

    @ModifyExpressionValue(method = "createTeleportTarget", at = {
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;")})
    private RegistryKey<World> worldless$portalFix(RegistryKey<World> original, @Local(argsOnly = true)ServerWorld world) {

        if (world.getServer() instanceof WorldlessStateHolder holder) {
            WorldlessState worldlessState = holder.worldless$getWorldlessState();
            if (!worldlessState.isEnabled()) return original;
            if (original == World.OVERWORLD && worldlessState.overworldHandle != null) return worldlessState.overworldHandle.getRegistryKey();
            if (original == World.NETHER && worldlessState.netherHandle != null) return worldlessState.netherHandle.getRegistryKey();
            if (original == World.END && worldlessState.endHandle != null) return worldlessState.endHandle.getRegistryKey();
        }
        Worldless.LOGGER.error("portal level fell back to vanilla defaults. WorldlessState may not be applied to server");
        return original;
    }
}

package twists.mixin.feature.worldless;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import twists.worldless.WorldlessUtil;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract ServerWorld getWorld();

    @ModifyExpressionValue(method = "teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;", at = {
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;")})
    private RegistryKey<World> twists$worldless$subspaceBubbleFix$teleport(RegistryKey<World> original) {
        return WorldlessUtil.getActiveWorldForVanilla(this.getWorld().getServer(), original);
    }
    @ModifyExpressionValue(method = "worldChanged", at = {
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"),
            @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;")})
    private RegistryKey<World> twists$worldless$subspaceBubbleFix$worldChanged(RegistryKey<World> original) {
        return WorldlessUtil.getActiveWorldForVanilla(this.getWorld().getServer(), original);
    }


}

package twists.mixin.feature.worldless;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import twists.worldless.WorldlessStateHolder;
import twists.worldless.WorldlessState;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements WorldlessStateHolder {
    @Unique WorldlessState worldlessState = new WorldlessState();
    @Override
    public WorldlessState twists$worldless$getWorldlessState() {
        return worldlessState;
    }

    @ModifyReturnValue(method = "getOverworld", at = @At("RETURN"))
    private ServerWorld worldless$getActiveOverworld(ServerWorld original) {
        if (this.worldlessState.isEnabled() && this.worldlessState.overworldHandle != null) return this.worldlessState.overworldHandle.asWorld();
        return original;
    }


}

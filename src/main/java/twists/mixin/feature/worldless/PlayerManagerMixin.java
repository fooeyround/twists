package twists.mixin.feature.worldless;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @ModifyExpressionValue(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
    private static boolean twists$worldless$bugfix$vanillaPositionKeptWhenDefaultingToOverworld(boolean original, @Local RegistryKey<World> registryKey) {
        //TODO: is this sufficient? Should we run WorldlessUtil.getSafeSpawnNearPos() here?
        return original || registryKey.getValue().getNamespace().equals("fantasy");
    }

}

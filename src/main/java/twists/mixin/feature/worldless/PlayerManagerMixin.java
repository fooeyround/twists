package twists.mixin.feature.worldless;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.logging.Level;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @ModifyExpressionValue(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isEmpty()Z"))
    private static boolean twists$worldless$bugfix$vanillaPositionKeptWhenDefaultingToOverworld(boolean original, @Local ResourceKey<Level> resourceKey) {
        //TODO: is this sufficient? Should we run WorldlessUtil.getSafeSpawnNearPos() here?
        return original || resourceKey.location().getNamespace().equals("worldless");
    }

}

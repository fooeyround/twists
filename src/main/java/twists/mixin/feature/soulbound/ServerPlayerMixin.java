package twists.mixin.feature.soulbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setHealth(F)V"))
    private void twists$soulbound$transferItemsOnPlayerRecreation(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        if (((ServerPlayer)(Object)this).level().getGameRules().get(GameRules.KEEP_INVENTORY) || that.isSpectator()) return;
        ((ServerPlayer)(Object)this).getInventory().replaceWith(that.getInventory());

    }
}

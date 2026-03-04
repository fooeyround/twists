package twists.mixin.feature.event.waypointTransmitterReceiveEvent;

import net.casual.arcade.events.GlobalEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twists.event.WaypointTransmitterReceiveEvent;

@Mixin(WaypointTransmitter.class)
public interface WaypointTransmitterMixin {

    @Inject(method = "doesSourceIgnoreReceiver", at = @At("HEAD"), cancellable = true)
    private static void twists$waypointTransmitterReceiveEvent$doesSourceIgnoreReceiver(LivingEntity source, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        WaypointTransmitterReceiveEvent event = new WaypointTransmitterReceiveEvent(receiver, source);
        GlobalEventHandler.Server.broadcast(event);
        if (event.getHideSource()) {
            cir.setReturnValue(true);
        }
    }

}

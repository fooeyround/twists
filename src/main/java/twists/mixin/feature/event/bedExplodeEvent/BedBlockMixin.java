package twists.mixin.feature.event.bedExplodeEvent;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.casual.arcade.events.GlobalEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twists.event.BedExplodeEvent;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @ModifyExpressionValue(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;explodes()Z"))
    private boolean it(boolean original, @Local(name = "bedRule") BedRule bedRule,
                       @Local(argsOnly = true) BlockState state,
                       @Local(argsOnly = true) Level level,
                       @Local(argsOnly = true) BlockPos pos,
                       @Local(argsOnly = true) Player player,
                       @Local(argsOnly = true) BlockHitResult hitResult
    ) {

        BedExplodeEvent event = new BedExplodeEvent(bedRule, state, level, pos, player, hitResult);
        GlobalEventHandler.Server.broadcast(event);
        return original && !event.isCancelled();
    }


}

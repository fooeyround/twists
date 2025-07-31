package worldless.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import worldless.WorldlessStateHolder;
import worldless.WorldlessState;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements WorldlessStateHolder {
    @Unique WorldlessState worldlessState = new WorldlessState();
    @Override
    public WorldlessState worldless$getWorldlessState() {
        return worldlessState;
    }
}

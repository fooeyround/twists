package twists.mixin;

import com.mojang.datafixers.DataFixer;
import net.casual.arcade.events.GlobalEventHandler;
import net.casual.arcade.extensions.ExtensionHolder;
import net.casual.arcade.extensions.ExtensionMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twists.event.MinecraftServerExtensionEvent;

import java.net.Proxy;
import java.util.Optional;

/// TODO: Implement Serialization for Extensions
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ExtensionHolder {
    @Unique private final ExtensionMap twists$extensionMap = new ExtensionMap();
    @Override
    public @NotNull ExtensionMap getExtensionMap() {
        return this.twists$extensionMap;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void twists$onMinecraftServerInit(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        GlobalEventHandler.Server.broadcast(new MinecraftServerExtensionEvent(server));
    }

}

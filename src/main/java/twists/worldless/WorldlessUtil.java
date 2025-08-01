package twists.worldless;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Optional;

public class WorldlessUtil {

    public static boolean isVanillaWorld(RegistryKey<World> worldRegistryKey) {
        return worldRegistryKey == World.OVERWORLD || worldRegistryKey == World.NETHER || worldRegistryKey == World.END;
    }
    public static boolean isWorldInActivePool(MinecraftServer server,RegistryKey<World> worldRegistryKey) {
        if (server instanceof WorldlessStateHolder holder) {
            WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
            return worldRegistryKey == worldlessState.overworldHandle.getRegistryKey() ||
                    worldRegistryKey == worldlessState.netherHandle.getRegistryKey() ||
                    worldRegistryKey == worldlessState.endHandle.getRegistryKey();
        }
        return false;
    }

    public static RegistryKey<World> getActiveWorldForVanilla(MinecraftServer server, RegistryKey<World> original) {
        if (server instanceof WorldlessStateHolder holder) {
            WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
            if (!worldlessState.isEnabled()) return original;
            if (original == World.OVERWORLD && worldlessState.overworldHandle != null) return worldlessState.overworldHandle.getRegistryKey();
            if (original == World.NETHER && worldlessState.netherHandle != null) return worldlessState.netherHandle.getRegistryKey();
            if (original == World.END && worldlessState.endHandle != null) return worldlessState.endHandle.getRegistryKey();
        }
        return original;
    }

    public static BlockPos getSafeSpawnNearPos(WorldView world, BlockPos pos) {
        final BlockPos originTop = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
        final Optional<BlockPos> saferPos = BlockPos.streamOutwards(originTop, 150, 10, 150)
                .filter(blockPos ->
                        (Block.isFaceFullSquare(world.getBlockState(blockPos.down()).getCollisionShape(world, blockPos), Direction.UP) &&
                                //Hardcoded check is sad: other blocks may have this property.
                                !world.getBlockState(blockPos.down()).isOf(Blocks.POWDER_SNOW) &&
                                world.getBlockState(blockPos).isAir() &&
                                world.getBlockState(blockPos.up()).isAir()
                        )).findFirst();

        return saferPos.orElse(originTop);
    }

}

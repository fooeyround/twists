package twists.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.Heightmap


object WorldlessUtil {


    /*
    *
    *         final BlockPos originTop = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
        final Optional<BlockPos> saferPos = BlockPos.withinManhattanStream(originTop, 150, 10, 150)
                .filter(blockPos ->
                        (Block.isFaceFull(world.getBlockState(blockPos.below()).getCollisionShape(world, blockPos), Direction.UP) &&
                                //Hardcoded check is sad: other blocks may have this property.
                                !world.getBlockState(blockPos.below()).is(Blocks.POWDER_SNOW) &&
                                world.getBlockState(blockPos).isAir() &&
                                world.getBlockState(blockPos.above()).isAir()
                        )).findFirst();

        return saferPos.orElse(originTop);
        *
    * */



    fun BlockPos.findTopNearestSafeSpawn(level: LevelReader, xSize: Int, ySize: Int, zSize: Int): BlockPos? {
        val originTop = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, this)
        val searchList = BlockPos.withinManhattan(
            originTop,
            xSize,
            ySize,
            zSize
        )
        for (blockPos in searchList) {
            if (Block.isFaceFull(
                level.getBlockState( blockPos.below()).getCollisionShape(level, blockPos.below()),
                Direction.UP
            ) &&
                    !level.getBlockState(blockPos.below()).`is`(Blocks.POWDER_SNOW) &&
                    level.getBlockState(blockPos).isAir &&
                    level.getBlockState(blockPos.above()).isAir
                    ) return blockPos;
        }

        return null
    }



}
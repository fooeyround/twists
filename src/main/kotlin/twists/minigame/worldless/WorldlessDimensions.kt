package twists.minigame.worldless

import net.casual.arcade.dimensions.level.CustomLevel
import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevelsBuilder
import net.casual.arcade.utils.IdentifierUtils
import net.minecraft.server.MinecraftServer

class WorldlessDimensions(
    val overworld: CustomLevel,
    val nether: CustomLevel,
    val end: CustomLevel,
): Iterable<CustomLevel> {
    override fun iterator(): Iterator<CustomLevel> {
        return listOf(overworld, nether, end).iterator()
    }





    companion object {

        fun createNewWorldlessDimensions(server: MinecraftServer, seed: Long? = null): WorldlessDimensions {
            val vanillaLikeLevels = VanillaLikeLevelsBuilder.build(server) {
                for (dimension in listOf(VanillaDimension.Overworld, VanillaDimension.Nether, VanillaDimension.End)) {
                    this.set(dimension) {
                        dimensionKey(IdentifierUtils.random { "${dimension.getDimensionKey().identifier().path}_$it" })
                        if (seed != null) {
                            seed(seed)
                        } else {
                            randomSeed()
                        }
                        //TODO: should there be an option to kept them?
                        persistence(LevelPersistence.Temporary)
                    }
                }
            }

            return WorldlessDimensions(
                vanillaLikeLevels.getOrThrow(VanillaDimension.Overworld),
                vanillaLikeLevels.getOrThrow(VanillaDimension.Nether),
                vanillaLikeLevels.getOrThrow(VanillaDimension.End)
            )
        }

    }
}
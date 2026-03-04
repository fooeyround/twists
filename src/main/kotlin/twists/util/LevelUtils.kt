package twists.util

import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevelsBuilder
import net.casual.arcade.utils.IdentifierUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.gamerules.GameRules




object LevelUtils {
    fun createNewVanillaLikeLevels(server: MinecraftServer, seed: Long? = null, locatorBar: Boolean = false, immediateRespawn: Boolean = false): VanillaLikeLevels {
        return VanillaLikeLevelsBuilder.build(server) {
            for (dimension in listOf(VanillaDimension.Overworld, VanillaDimension.Nether, VanillaDimension.End)) {
                this.set(dimension) {
                    dimensionKey(IdentifierUtils.random(TwistsUtils.MOD_ID) { "${dimension.getDimensionKey().identifier().path}_$it" })
                    if (seed != null) {
                        seed(seed)
                    } else {
                        randomSeed()
                    }
                    gameRules {
                        set(GameRules.IMMEDIATE_RESPAWN, immediateRespawn, server)
                        set(GameRules.LOCATOR_BAR, locatorBar, server)
                    }
                    //TODO: should there be an option to kept them?
                    persistence(LevelPersistence.Temporary)
                }
            }
        }
    }
}
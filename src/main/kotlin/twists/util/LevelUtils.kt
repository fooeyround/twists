package twists.util

import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevelsBuilder
import net.casual.arcade.utils.IdentifierUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.gamerules.GameRules
import twists.minigame.shared.VanillaLikeTwistedMinigame


object LevelUtils {
    data class VanillaLikeLevelSettings(
        var seed: Long? = null,
        var chunkGenerator: ChunkGenerator? = null,
        var locatorBar: Boolean = false,
        var immediateRespawn: Boolean = false
    )
    fun createNewVanillaLikeLevels(server: MinecraftServer, levelSettings: VanillaLikeLevelSettings?): VanillaLikeLevels {
        return VanillaLikeLevelsBuilder.build(server) {
            for (dimension in listOf(VanillaDimension.Overworld, VanillaDimension.Nether, VanillaDimension.End)) {
                this.set(dimension) {
                    dimensionKey(IdentifierUtils.random(TwistsUtils.MOD_ID) { "${dimension.getDimensionKey().identifier().path}_$it" })
                    if (levelSettings?.seed != null) {
                        seed(levelSettings.seed!!)
                    } else {
                        randomSeed()
                    }
                    if (levelSettings?.chunkGenerator != null) {
                        chunkGenerator(chunkGenerator)
                    }
                    gameRules {
                        set(GameRules.IMMEDIATE_RESPAWN, levelSettings?.immediateRespawn ?: false, server)
                        set(GameRules.LOCATOR_BAR, levelSettings?.locatorBar ?: false, server)
                    }
                    //TODO: should there be an option to kept them?
                    persistence(LevelPersistence.Temporary)
                }
            }
        }
    }
}
package twists.minigame.shared

import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.minigame.managers.MinigameLevelManager
import net.casual.arcade.utils.player.server
import net.casual.arcade.utils.math.location.LocationWithLevel
import net.casual.arcade.utils.math.location.LocationWithLevel.Companion.asLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.phys.Vec3

class VanillaLikeLevelsSpawnLocation(val level: ServerLevel, val allowedLevels: VanillaLikeLevels? = null): MinigameLevelManager.SpawnLocation {
    override val overridesPlayerSpawnPoint: Boolean = true
    override fun get(player: ServerPlayer): LocationWithLevel<ServerLevel> {
        val respawnData: LevelData.RespawnData? = player.respawnConfig?.respawnData()
        if (respawnData != null) {
            val serverLevel: ServerLevel? = player.server.getLevel(respawnData.dimension())
            if (allowedLevels != null && allowedLevels.all().any { it == serverLevel }) {
                return this.level.asLocation(respawnData.globalPos().pos.bottomCenter)
            }
        }

        this.level.getChunk(0, 0)
        val y = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, 0, 0)
        return this.level.asLocation(Vec3(0.0, y.toDouble(), 0.0))
    }
}
package twists.minigame.shared

import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.utils.deleteCustomLevel
import net.casual.arcade.events.BuiltInEventPhases
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.events.MinigameAddNewPlayerEvent
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.events.MinigameStartEvent
import net.casual.arcade.minigame.gamemode.ExtendedGameMode
import net.casual.arcade.minigame.gamemode.ExtendedGameMode.Companion.extendedGameMode
import net.casual.arcade.utils.player.resetExperience
import net.casual.arcade.utils.player.resetHunger
import net.casual.arcade.utils.player.revokeAllAdvancements
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameType
import net.minecraft.world.level.chunk.ChunkGenerator
import twists.util.LevelUtils
import java.util.*

abstract class VanillaLikeTwistedMinigame(
    server: MinecraftServer,
    uuid: UUID,
    open var levelSettings: LevelUtils.VanillaLikeLevelSettings? = null,
    var dimensions: VanillaLikeLevels = LevelUtils.createNewVanillaLikeLevels(server, levelSettings),
): TwistedMinigame(server, uuid) {

    val overworld: ServerLevel
        get() = this.dimensions.getOrThrow(VanillaDimension.Overworld)


    init {
        this.levels.spawn = VanillaLikeLevelsSpawnLocation(this.overworld, this.dimensions)
    }

    @Listener
    private fun addCustomLevels(event: MinigameStartEvent) {
        this.levels.addAll(this.dimensions.all())

    }

    @Listener
    private fun onMinigameClose(event: MinigameCloseEvent) {
        if (event.minigame == this) {
            for (level in this.dimensions.all()) {
                this.server.deleteCustomLevel(level)
            }
        }
    }


    @Listener
    private fun onMinigamePlayerJoin(event: MinigameAddNewPlayerEvent) {
        event.player.setGameMode(GameType.SURVIVAL)
        if (event.minigame is VanillaLikeTwistedMinigame) {
            event.player.teleportTo( this.levels.spawn.get(event.player)!!)
        }
    }

    @Listener
    private fun onSetPlaying(event: MinigameSetPlayingEvent) {
        val player = event.player
        player.isInvisible = false
        player.closeContainer()

        player.resetHunger()
        player.resetExperience()
        player.revokeAllAdvancements()
//        player.clearPlayerInventory()
        player.removeAllEffects()

        player.removeVehicle()
        player.setGlowingTag(false)

        player.extendedGameMode = ExtendedGameMode.Survival
    }

}
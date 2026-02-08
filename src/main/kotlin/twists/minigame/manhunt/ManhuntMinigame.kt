package twists.minigame.manhunt

import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevelsBuilder
import net.casual.arcade.dimensions.utils.deleteCustomLevel
import net.casual.arcade.events.server.player.PlayerRespawnEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.events.MinigameAddNewPlayerEvent
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.managers.MinigameLevelManager
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.IdentifierUtils
import net.casual.arcade.utils.PlayerUtils.clearPlayerInventory
import net.casual.arcade.utils.PlayerUtils.grantAdvancement
import net.casual.arcade.utils.PlayerUtils.grantAllRecipesSilently
import net.casual.arcade.utils.PlayerUtils.resetExperience
import net.casual.arcade.utils.PlayerUtils.resetHealth
import net.casual.arcade.utils.PlayerUtils.resetHunger
import net.casual.arcade.utils.PlayerUtils.revokeAllAdvancements
import net.casual.arcade.utils.math.location.LocationWithLevel.Companion.asLocation
import net.casual.arcade.utils.set
import net.casual.arcade.utils.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameType
import net.minecraft.world.level.gamerules.GameRules
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall
import twists.minigame.TwistedMinigame
import twists.util.TwistsUtils
import twists.util.twists
import java.util.*

class ManhuntMinigame(
    server: MinecraftServer,
    uuid: UUID,
    private var dimensions: VanillaLikeLevels,
    private val factory: ManhuntMinigameFactory? = null
): TwistedMinigame(server, uuid) {
    override val id = ID
    override val settings = ManhuntSettings(this)

    override fun phases(): Collection<Phase<out Minigame>> {
        return ManhuntPhase.entries
    }

    val overworld: ServerLevel
        get() = this.dimensions.getOrThrow(VanillaDimension.Overworld)


    init {
        this.tickrate.useGlobalManager = false
        this.levels.addAll(this.dimensions.all())
        this.players.keepPlayerData = false
        
        this.levels.spawn = ManhuntSpawnLocation(this.overworld, this.dimensions)
        this.settings.canPvp.set(false)
    }

    @Listener
    private fun onMinigameClose(event: MinigameCloseEvent) {
        for (level in this.dimensions.all()) {
                this.server.deleteCustomLevel(level)
        }
    }


    @Listener
    private fun onMinigamePlayerJoin(event: MinigameAddNewPlayerEvent) {
        if (event.minigame is ManhuntMinigame && event.minigame.phase > ManhuntPhase.Initialization) {
                event.player.teleportTo( this.levels.spawn.get(event.player)!!)
        }
        event.player.resetHealth()
        event.player.resetHunger()
    }

    @Listener
    private fun onSetPlaying(event: MinigameSetPlayingEvent) {
        val player = event.player
        player.isInvisible = false
        player.closeContainer()

        player.resetHunger()
        player.resetExperience()
        player.clearPlayerInventory()
        player.removeAllEffects()

        player.removeVehicle()
        player.setGlowingTag(false)

        player.setGameMode(GameType.SURVIVAL)
    }





    companion object {
        val ID = twists("manhunt")

        fun createNewVanillaLikeLevels(server: MinecraftServer, seed: Long? = null): VanillaLikeLevels {
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
                            set(GameRules.LOCATOR_BAR, false)
                        }
                        persistence(LevelPersistence.Temporary)
                    }
                }
            }
        }
    }
}
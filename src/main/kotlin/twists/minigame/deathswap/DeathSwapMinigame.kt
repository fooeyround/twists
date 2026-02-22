package twists.minigame.deathswap

import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.utils.deleteCustomLevel
import net.casual.arcade.events.BuiltInEventPhases
import net.casual.arcade.events.server.player.PlayerDeathEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.events.MinigameAddNewPlayerEvent
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.gamemode.ExtendedGameMode
import net.casual.arcade.minigame.gamemode.ExtendedGameMode.Companion.extendedGameMode
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.utils.PlayerUtils.clearPlayerInventory
import net.casual.arcade.utils.PlayerUtils.resetExperience
import net.casual.arcade.utils.PlayerUtils.resetHealth
import net.casual.arcade.utils.PlayerUtils.resetHunger
import net.casual.arcade.utils.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameType
import twists.minigame.shared.TwistedMinigame
import twists.minigame.shared.VanillaLikeLevelsSpawnLocation
import twists.util.twists
import java.util.*

class DeathSwapMinigame(
    server: MinecraftServer,
    uuid: UUID,
    private var dimensions: VanillaLikeLevels,
    private val factory: DeathSwapMinigameFactory? = null
): TwistedMinigame(server, uuid) {
    override val id = ID
    override val settings = DeathSwapSettings(this)

    override fun phases(): Collection<Phase<out Minigame>> {
        return DeathSwapPhase.entries
    }

    val overworld: ServerLevel
        get() = this.dimensions.getOrThrow(VanillaDimension.Overworld)


    init {
        this.tickrate.useGlobalManager = false
        this.levels.addAll(this.dimensions.all())

        this.levels.spawn = VanillaLikeLevelsSpawnLocation(this.overworld, this.dimensions)
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
        event.player.resetHealth()
        event.player.resetHunger()
        event.player.setGameMode(GameType.SURVIVAL)
        if (event.minigame is DeathSwapMinigame && event.minigame.phase > DeathSwapPhase.Initialization) {
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
        player.clearPlayerInventory()
        player.removeAllEffects()

        player.removeVehicle()
        player.setGlowingTag(false)


//        player.setGameMode(GameType.SURVIVAL)
        player.extendedGameMode = ExtendedGameMode.Survival
    }


    @Listener(flags = ListenerFlags.IS_PLAYING, phase = BuiltInEventPhases.POST)
    private fun onPlayerDeath(event: PlayerDeathEvent) {
        this.players.setSpectating(event.player)
    }





    companion object {
        val ID = twists("death_swap")
    }
}
package twists.minigame.manhunt

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
import twists.event.BedExplodeEvent
import twists.minigame.shared.VanillaLikeLevelsSpawnLocation
import twists.minigame.shared.TwistedMinigame
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.twists
import java.util.*

class ManhuntMinigame(
    server: MinecraftServer,
    uuid: UUID,
    dimensions: VanillaLikeLevels,
    private val factory: ManhuntMinigameFactory? = null
): VanillaLikeTwistedMinigame(server, uuid, dimensions) {
    override val id = ID
    override val settings = ManhuntSettings(this)

    override fun phases(): Collection<Phase<out Minigame>> {
        return ManhuntPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
        this.settings.canPvp.set(false)
    }

    @Listener(flags = ListenerFlags.IS_PLAYING, phase = BuiltInEventPhases.POST)
    private fun onPlayerDeath(event: PlayerDeathEvent) {
        if (event.player.team?.name == "runners") {
            this.players.setSpectating(event.player)
        }
    }


    @Listener
    private fun intentionalGameDesign(event: BedExplodeEvent) {
        if (!this.settings.intentionalGameDesign) {
            event.cancel()
        }
    }



    companion object {
        val ID = twists("manhunt")
    }
}
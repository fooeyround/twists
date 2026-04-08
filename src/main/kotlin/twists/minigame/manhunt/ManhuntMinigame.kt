package twists.minigame.manhunt

import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.events.BuiltInEventPhases
import net.casual.arcade.events.server.player.PlayerDeathEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.utils.MinigameUtils.getMinigame
import net.minecraft.server.MinecraftServer
import twists.event.BedExplodeEvent
import twists.event.WaypointTransmitterReceiveEvent
import twists.extension.SharedInventoryTeamExtension
import twists.extension.SharedInventoryTeamExtension.Companion.sharedInventoryExtension
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.twists
import java.util.*

class ManhuntMinigame(
    server: MinecraftServer,
    uuid: UUID,
): VanillaLikeTwistedMinigame(server, uuid) {
    override val id = ManhuntMinigame.id
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
        if (event.player.team?.name == "runners" || this.settings.hardcoreHunters) {
            this.players.setSpectating(event.player)
        }
    }

    @Listener
    private fun hidePlayersOnDifferentTeamsFromLocatorBar(event: WaypointTransmitterReceiveEvent) {
        event.hideSource = event.player.team != event.source.team
    }

    @Listener
    private fun intentionalGameDesign(event: BedExplodeEvent) {
        if (!this.settings.intentionalGameDesign) {
            event.cancel()
        }
    }

    //TODO: this jankily overrides Twisted minigame...
    @Listener(phase = BuiltInEventPhases.POST)
    private fun communalPocketsOnSetPlaying(event: MinigameSetPlayingEvent) {
        event.player.team?.let {
            it.sharedInventoryExtension.shareLevel = if (!this.settings.communalPocketsRunnersOnly || event.player.team?.name == "runners") {
                this.settings.communalPockets
            } else {
                SharedInventoryTeamExtension.ShareLevel.None
            }
        }
    }






    companion object {
        val id = twists("manhunt")
    }
}
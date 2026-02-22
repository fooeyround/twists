package twists.minigame

import net.casual.arcade.events.server.player.PlayerSetSneakingEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.events.MinigameAddNewPlayerEvent
import net.casual.arcade.minigame.gamemode.ExtendedGameMode
import net.casual.arcade.minigame.gamemode.ExtendedGameMode.Companion.extendedGameMode
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import twists.stats.TwistsStats
import java.util.UUID
import kotlin.math.abs

abstract class TwistedMinigame(
    server: MinecraftServer,
    uuid: UUID,
): Minigame(server, uuid) {
    override val settings = TwistSettings(this)

    init {
        this.players.keepPlayerData = true
    }


    @Listener(flags = ListenerFlags.IS_SPECTATOR)
    private fun onPlayerSneak(event: PlayerSetSneakingEvent) {
        val (player, sneaking) = event
        if (!player.isShiftKeyDown && sneaking) {
            val last = this.stats.getOrCreateStat(player, TwistsStats.LAST_SNEAK_TIME)
            if (abs(this.server.tickCount - last.value) < 7) {
                val mode = when (player.extendedGameMode) {
                    ExtendedGameMode.AdventureSpectator -> ExtendedGameMode.NoClipSpectator
                    else -> ExtendedGameMode.AdventureSpectator
                }
                player.extendedGameMode = mode
            } else {
                last.modify { this.server.tickCount }
            }
        }
    }




}
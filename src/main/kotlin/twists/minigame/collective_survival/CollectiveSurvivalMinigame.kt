package twists.minigame.skyblock

import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.events.BuiltInEventPhases
import net.casual.arcade.events.server.player.PlayerDeathEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.phase.Phase
import net.minecraft.server.MinecraftServer
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.twists
import java.util.*

class SkyBlockMinigame(
    server: MinecraftServer,
    uuid: UUID,
    private val factory: SkyBlockMinigameFactory? = null
): VanillaLikeTwistedMinigame(server, uuid) {
    override val id = ID
    override val settings = SkyBlockSettings(this)

    override fun phases(): Collection<Phase<out Minigame>> {
        return SkyBlockPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
    }

    companion object {
        val ID = twists("skyblock")
    }
}
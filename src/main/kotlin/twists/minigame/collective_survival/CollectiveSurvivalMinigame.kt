package twists.minigame.collective_survival

import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.phase.Phase
import net.minecraft.server.MinecraftServer
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.twists
import java.util.*

class CollectiveSurvivalMinigame(
    server: MinecraftServer,
    uuid: UUID,
): VanillaLikeTwistedMinigame(server, uuid) {
    override val id = ID
    override val settings = CollectiveSurvivalSettings(this)

    override fun phases(): Collection<Phase<out Minigame>> {
        return CollectiveSurvivalPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
    }

    companion object {
        val ID = twists("collective_survival")
    }
}
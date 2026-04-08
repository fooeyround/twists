package twists.minigame.worldless

import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevelsBuilder
import net.casual.arcade.dimensions.utils.deleteCustomLevel
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.utils.IdentifierUtils
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.gamerules.GameRules
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall
import twists.minigame.shared.VanillaLikeLevelsSpawnLocation
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.LevelUtils
import twists.util.TwistsUtils
import twists.util.twists
import java.util.*

class WorldlessMinigame(
    server: MinecraftServer,
    uuid: UUID,
): VanillaLikeTwistedMinigame(server, uuid) {
    override val id = WorldlessMinigame.id
    override val settings = WorldlessSettings(this)
    override fun phases(): Collection<Phase<out Minigame>> {
        return WorldlessPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
    }

    internal fun switchToNewWorld() {
        val newDimensions = LevelUtils.createNewVanillaLikeLevels(this.server, this.levelSettings)
        this.levels.addAll(newDimensions.all())

        this.dimensions.all().forEach { this.server.deleteCustomLevel(it) }
        this.dimensions = newDimensions

        this.levels.spawn = VanillaLikeLevelsSpawnLocation(this.overworld, this.dimensions)

        players.forEach {
            it.teleportTo(this.levels.spawn.get(it)!!)
            it.takeNoDamageOnNextFall() //TODO: hopefully unneeded now.
        }
    }

    companion object {
        val id = twists("worldless")
    }
}
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
    private var dimensions: VanillaLikeLevels,
    private val factory: WorldlessMinigameFactory? = null
): VanillaLikeTwistedMinigame(server, uuid, dimensions) {
    override val id = WorldlessMinigame.id
    override val settings = WorldlessSettings(this)
    override fun phases(): Collection<Phase<out Minigame>> {
        return WorldlessPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
    }

    internal fun switchToNewWorld() {
        val newDimensions = LevelUtils.createNewVanillaLikeLevels(this.server)
        this.levels.addAll(newDimensions.all())

        this.dimensions.all().forEach { this.server.deleteCustomLevel(it) }
        this.dimensions = newDimensions

        this.overworld.getChunk(0, 0)
        this.levels.spawn = VanillaLikeLevelsSpawnLocation(this.overworld, this.dimensions)

        players.forEach {
            it.teleportTo(this.levels.spawn.get(it)!!)
            it.takeNoDamageOnNextFall() //TODO: hopefully unneeded now.
        }
    }

    /*
    @Listener(flags = ListenerFlags.HAS_PLAYER)
    private fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        player.lastDeathLocation.ifPresent { pos ->
            val level = player.server.getLevel(pos.dimension)
            if (level != null && this.levels.has(level)) {
                val location = pos.pos.center.withRotation(player.rotationVector).with(level)
                player.teleportTo(location)
            }
        }
    }
    */

    companion object {
        val id = twists("worldless")

        @Deprecated("use LevelUtils")
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
                            set(GameRules.IMMEDIATE_RESPAWN, true, server)
                        }
                        persistence(LevelPersistence.Temporary)
                    }
                }
            }
        }
    }
}
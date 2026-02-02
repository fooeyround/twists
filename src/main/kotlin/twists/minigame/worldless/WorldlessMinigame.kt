package twists.minigame.worldless

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
import net.casual.arcade.minigame.events.MinigameInitializeEvent
import net.casual.arcade.minigame.managers.MinigameLevelManager
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.template.teleporter.EntityTeleporter.Companion.teleport
import net.casual.arcade.utils.IdentifierUtils
import net.casual.arcade.utils.math.location.LocationWithLevel.Companion.asLocation
import net.casual.arcade.utils.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.Ticket
import net.minecraft.server.level.TicketType
import net.minecraft.world.entity.Relative
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall
import twists.minigame.TwistSettings
import twists.minigame.TwistedMinigame
import twists.util.twists
import java.util.*

class WorldlessMinigame(
    server: MinecraftServer,
    uuid: UUID,
    private var dimensions: VanillaLikeLevels,
    private val factory: WorldlessMinigameFactory? = null
): TwistedMinigame(server, uuid) {
    override val id = ID
    override val settings = WorldlessSettings(this)
    override fun phases(): Collection<Phase<out Minigame>> {
        return WorldlessPhase.entries
    }

    val overworld: ServerLevel
        get() = this.dimensions.getOrThrow(VanillaDimension.Overworld)


    init {
        this.tickrate.useGlobalManager = false

        this.levels.addAll(this.dimensions.all())

    }




    internal fun switchToNewWorld() {
        val newDimensions = createNewVanillaLikeLevels(this.server)
        this.levels.addAll(newDimensions.all())

//        this.dimensions.all().forEach { this.server.deleteCustomLevel(it) }
        this.dimensions = newDimensions


        this.overworld.chunkSource.addTicket(Ticket(TicketType.SPAWN_SEARCH, 1), ChunkPos(0,0))
        this.overworld.tick { true }
        val y =  this.overworld.getHeight(Heightmap.Types.WORLD_SURFACE, 0, 0)
        this.levels.spawn = MinigameLevelManager.SpawnLocation.global(
            location = this.overworld.asLocation(Vec3(0.0, y.toDouble(), 0.0)),
            overridesPlayerSpawnPoint = true
        )

        players.forEach {
            it.teleportTo(this.overworld, 0.0, y.toDouble() + 10.0, 0.0, Relative.ALL, 0F, 0F, false)
            it.takeNoDamageOnNextFall()
        }
    }


    @Listener
    private fun onInitialize(event: MinigameInitializeEvent) {
        this.overworld.chunkSource.addTicket(Ticket(TicketType.SPAWN_SEARCH, 1), ChunkPos(0,0))
        this.overworld.tick { true }
        val y =  this.overworld.getHeight(Heightmap.Types.WORLD_SURFACE, 0, 0)
        this.levels.spawn = MinigameLevelManager.SpawnLocation.global(
            location = this.overworld.asLocation(Vec3(0.0, y.toDouble(), 0.0)),
            overridesPlayerSpawnPoint = true
        )
    }

    @Listener
    private fun onMinigamePlayerJoin(event: MinigameAddNewPlayerEvent) {
        if (event.minigame is WorldlessMinigame && event.minigame.phase > WorldlessPhase.ResettingWorld) {
            this.players.forEach {
                it.teleportTo( this.levels.spawn.get(it)!!)
            }
        }
    }

    @Listener(flags = ListenerFlags.HAS_PLAYER)
    private fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
//
//        player.lastDeathLocation.ifPresent { pos ->
//            val level = player.server.getLevel(pos.dimension)
//            if (level != null && this.levels.has(level)) {
//                val location = pos.pos.center.withRotation(player.rotationVector).with(level)
//                player.teleportTo(location)
//            }
//        }
    }




    companion object {
        val ID = twists("worldless")

        fun createNewVanillaLikeLevels(server: MinecraftServer, seed: Long? = null): VanillaLikeLevels {
            return VanillaLikeLevelsBuilder.build(server) {
                for (dimension in listOf(VanillaDimension.Overworld, VanillaDimension.Nether, VanillaDimension.End)) {
                    this.set(dimension) {
                        dimensionKey(IdentifierUtils.random { "${dimension.getDimensionKey().identifier().path}_$it" })
                        if (seed != null) {
                            seed(seed)
                        } else {
                            randomSeed()
                        }
                        //TODO: should there be an option to kept them?
                        persistence(LevelPersistence.Temporary)
                    }
                }
            }
        }
    }
}
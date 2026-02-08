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
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameInitializeEvent
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.utils.IdentifierUtils
import net.casual.arcade.utils.PlayerUtils.resetHealth
import net.casual.arcade.utils.PlayerUtils.resetHunger
import net.casual.arcade.utils.set
import net.casual.arcade.utils.teleportTo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.gamerules.GameRule
import net.minecraft.world.level.gamerules.GameRules
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall
import twists.minigame.TwistedMinigame
import twists.util.TwistsUtils
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
        this.players.keepPlayerData = false
        
        this.levels.spawn = WorldlessSpawnLocation(this.overworld)


    }




    internal fun switchToNewWorld() {
        val newDimensions = createNewVanillaLikeLevels(this.server)
        this.levels.addAll(newDimensions.all())

        this.dimensions.all().forEach { this.server.deleteCustomLevel(it) }
        this.dimensions = newDimensions

        this.overworld.getChunk(0, 0)
        this.levels.spawn = WorldlessSpawnLocation(this.overworld)

        players.forEach {
            it.teleportTo(this.levels.spawn.get(it)!!)
            it.takeNoDamageOnNextFall() //TODO: hopefully unneeded now.
        }
    }


    @Listener
    private fun onMinigameClose(event: MinigameCloseEvent) {
        for (level in this.dimensions.all()) {
                this.server.deleteCustomLevel(level)
        }
    }


    @Listener
    private fun onMinigamePlayerJoin(event: MinigameAddNewPlayerEvent) {
        if (event.minigame is WorldlessMinigame && event.minigame.phase > WorldlessPhase.Initialization) {
                event.player.teleportTo( this.levels.spawn.get(event.player)!!)
        }
        event.player.resetHealth()
        event.player.resetHunger()
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
                        dimensionKey(IdentifierUtils.random(TwistsUtils.MOD_ID) { "${dimension.getDimensionKey().identifier().path}_$it" })
                        if (seed != null) {
                            seed(seed)
                        } else {
                            randomSeed()
                        }
                        gameRules {
                            set(GameRules.IMMEDIATE_RESPAWN, true)
                        }
                        //TODO: should there be an option to kept them?
                        persistence(LevelPersistence.Temporary)
                    }
                }
            }
        }
    }
}
package twists.minigame.lobby

import kotlinx.coroutines.withContext
import net.casual.arcade.dimensions.level.CustomLevel
import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator
import net.casual.arcade.events.server.player.PlayerVoidDamageEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.area.BoxedArea
import net.casual.arcade.minigame.data.MinigameDataModules
import net.casual.arcade.minigame.data.MinigameDataModules.Companion.get
import net.casual.arcade.minigame.data.module.MinigameWorldData
import net.casual.arcade.minigame.events.MinigameAddNewPlayerEvent
import net.casual.arcade.minigame.events.MinigameAddPlayerEvent
import net.casual.arcade.minigame.events.MinigameInitializeEvent
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.gamemode.ExtendedGameMode
import net.casual.arcade.minigame.gamemode.ExtendedGameMode.Companion.extendedGameMode
import net.casual.arcade.minigame.managers.MinigameLevelManager
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.utils.MinigameUtils.transferAdminAndSpectatorTeamsTo
import net.casual.arcade.scheduler.utils.asCoroutineDispatcher
import net.casual.arcade.utils.*
import net.casual.arcade.utils.TimeUtils.Seconds
import net.casual.arcade.utils.coroutine.delay
import net.casual.arcade.utils.coroutine.launch
import net.casual.arcade.utils.file.ReadableArchive
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.gamerules.GameRules
import twists.command.LobbyCommand
import twists.util.TwistsUtils
import twists.util.twists
import java.util.*
import kotlin.reflect.KProperty0


class LobbyMinigame(
    server: MinecraftServer,
    uuid: UUID,
    next: KProperty0<Minigame?>,
    val modules: MinigameDataModules
) : Minigame(server, uuid) {
    override val id: Identifier = twists("lobby")

    private val lobbyData: LobbyData
        get() = this.modules.get<LobbyData>() ?: LobbyData.DEFAULT

    override fun phases(): Collection<Phase<out Minigame>> {
        return LobbyPhase.entries
    }

    val next by next

    init {
        this.players.keepPlayerData = false
    }

    private var lobbyLevel: CustomLevel = CustomLevelBuilder.build(server) {
        spoofedDimensionKey(twists("lobby"))
        val dimension = IdentifierUtils.random(TwistsUtils.MOD_ID) { "lobby_$it"}.toKey(Registries.DIMENSION)
        this@LobbyMinigame.extractLobbyWorld(dimension)
        dimensionKey(dimension)
        dimensionType(BuiltinDimensionTypes.OVERWORLD)
        chunkGenerator(VoidChunkGenerator(server))
        defaultLevelProperties()
        persistence(LevelPersistence.Temporary)
        viewDistance(20)
        gameRules {
            resetToDefault()
            set(GameRules.SPAWN_PHANTOMS, false)
            set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0)
            set(GameRules.SPAWN_MOBS, false)
            set(GameRules.FALL_DAMAGE, false)
            set(GameRules.DROWNING_DAMAGE, false)
            set(GameRules.ENTITY_DROPS, false)
            set(GameRules.ADVANCE_WEATHER, false)
            set(GameRules.SPAWN_WANDERING_TRADERS, false)
            set(GameRules.MOB_DROPS, false)
            set(GameRules.BLOCK_DROPS, false)
            set(GameRules.COMMAND_BLOCK_OUTPUT, false)
            set(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, 0)
            set(GameRules.RANDOM_TICK_SPEED, 0)
            set(GameRules.LOCATOR_BAR, false)
        }
    }

    private fun extractLobbyWorld(destination: ResourceKey<Level>) {
        val world = this.modules.get<MinigameWorldData>() ?: return
        world.extract(this.server, destination)
    }

    @Listener
    private fun onMinigameInit(event: MinigameInitializeEvent) {
        this.levels.add(this.lobbyLevel)

        this.levels.spawn = MinigameLevelManager.SpawnLocation.global(this.lobbyData.spawn.get().with(this.lobbyLevel))


        this.settings.pauseOnServerStop = false
        this.settings.canPvp.set(false)
        this.settings.canGetHungry.set(false)
        this.settings.canBreakBlocks.set(false)
        this.settings.canPlaceBlocks.set(false)
        this.settings.canDropItems.set(false)
        this.settings.canPickupItems.set(false)
        this.settings.canTakeDamage.set(false)
        this.settings.canAttackEntities.set(true)
        this.settings.canInteractAll = false
        this.settings.daylightCycle = 0

        if (this.modules.get<MinigameWorldData>() == null) {
            BoxedArea(Vec3i(0, -1, 0), 10, 3, this.lobbyLevel).place()
        }

        this.property("next_minigame") { this.next?.id?.toString() }

        this.commands.register(LobbyCommand(this))

    }


    @Listener
    private fun onMinigameAddPlayer(event: MinigameAddNewPlayerEvent) {
        event.player.extendedGameMode = ExtendedGameMode.Adventure
        this.players.setPlaying(event.player)
        TwistsUtils.logger.info("event.player: ${event.player}")
        this.teleport(event.player)
    }


    @Listener
    private fun onPlayerVoidDamage(event: PlayerVoidDamageEvent) {
        this.teleport(event.player)
        event.cancel()
    }

    fun teleport(player: ServerPlayer) {
        player.teleportTo(this.levels.spawn.get(player)!!)
    }

    fun startCountdown() {
        this.server.launch {
            withContext(scheduler.asPhasedScheduler().asCoroutineDispatcher()) {
                visuals.countdown.transition(players = players::all)
                delay(1.Seconds)
                moveToNextMinigame()
            }
        }
    }


    fun moveToNextMinigame(): Boolean {
        val next = this.next
        if (next == null || next.closed) {
            TwistsUtils.logger.error("Failed to move to next minigame, it was not specified or closed!")
            return false
        }

        this.transferAdminAndSpectatorTeamsTo(next)
        this.players.transferTo(next, players)
        next.start()

        this.setPhase(LobbyPhase.Waiting)
        return true
    }


    companion object {
        private val lobbies = TwistsUtils.resolve("lobbies")

        fun create(
            lobby: String,
            next: KProperty0<Minigame?>,
            context: MinigameCreationContext
        ): LobbyMinigame {
            val server = context.server
            val modules = try {
                val archive = ReadableArchive.from(this.lobbies, lobby)
                MinigameDataModules.from(archive, server)
            } catch (exception: Exception) {
                TwistsUtils.logger.error("Failed to read lobby $lobby", exception)
                MinigameDataModules.empty()
            }
            return LobbyMinigame(server, context.uuid, next, modules)
        }

    }


}


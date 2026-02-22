package twists.minigame.shared

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.casual.arcade.events.ListenerRegistry
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.player.PlayerJoinEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameInitializeEvent
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.minigame.utils.MinigameUtils.getMinigame
import net.casual.arcade.scheduler.GlobalTickedScheduler
import net.casual.arcade.utils.JsonUtils
import net.minecraft.server.MinecraftServer
import twists.minigame.lobby.LobbyMinigame
import twists.minigame.manhunt.ManhuntMinigameFactory
import twists.util.TwistsUtils
import java.nio.file.Path
import kotlin.io.path.notExists


class TwistsMinigameManager(
    private val path: Path
) {

    data class EventConfiguration(
        val minigame: MinigameFactory = ManhuntMinigameFactory.DEFAULT,
        val lobby: String = "default"
        ) {
        companion object {
            val CODEC: Codec<EventConfiguration> = RecordCodecBuilder.create { instance ->
                instance.group(
                    MinigameFactory.CODEC.fieldOf("minigame").forGetter(EventConfiguration::minigame),
                    Codec.STRING.fieldOf("lobby").forGetter(EventConfiguration::lobby)
                ).apply(instance, ::EventConfiguration)
            }
        }
    }

    private lateinit var config: EventConfiguration

    private lateinit var lobby: LobbyMinigame
    private var minigame: Minigame? = null

    val current: Minigame get() {
            val minigame = this.minigame ?: return this.lobby
            if (minigame.started && !minigame.closed) {
                return minigame
            }
            return this.lobby
    }

    fun isInLobby(): Boolean {
        return this.current == this.lobby
    }

    fun returnToLobby() {
        if (this.current != this.lobby) {
            this.current.players.transferTo(this.lobby)
            this.current.close()

            GlobalTickedScheduler.later {
                this.reloadMinigame(this.lobby.server)
            }
        }
    }

    internal fun registerEvents(registry: ListenerRegistry) {
        registry.register<MinigameInitializeEvent>(::onMinigameInitialize)
        registry.register<PlayerJoinEvent>(phase = PlayerJoinEvent.PHASE_INITIALIZED, listener = ::onPlayerJoinEarly)
        registry.register<PlayerJoinEvent>(::onPlayerJoin)
        registry.register<MinigameCloseEvent>(::onLobbyClose)
    }


    internal fun load(server: MinecraftServer) {
        server.isUsingWhitelist = true

        this.reloadConfiguration()
        if (this.minigame == null) {
            this.reloadMinigame(server)
        }
        this.lobby = this.createLobby(server)
    }

    internal fun reload(server: MinecraftServer) {
        this.reloadConfiguration()
        this.reloadLobby(server)
        this.reloadMinigame(server)
    }

    private fun reloadConfiguration() {
        this.config = this.readEventConfig()
    }

    private fun reloadMinigame(server: MinecraftServer) {
        this.minigame?.close()
        val minigame = this.config.minigame.create(MinigameCreationContext(server))
        minigame.tryInitialize()
        this.minigame = minigame
    }

    private fun reloadLobby(server: MinecraftServer) {
        val previous = this.lobby
        this.lobby = this.createLobby(server)
        previous.players.transferTo(this.lobby)
        previous.close()
    }

    private fun createLobby(server: MinecraftServer): LobbyMinigame {
        val lobby = LobbyMinigame.create(this.config.lobby, this::minigame, MinigameCreationContext(server))
        lobby.start()
        return lobby
    }

    private fun onLobbyClose(event: MinigameCloseEvent) {
        if (event.minigame == this.lobby) {
            reloadLobby(event.minigame.server)
        }

    }


    private fun onMinigameInitialize(event: MinigameInitializeEvent) {
        event.minigame.events.register<MinigameCloseEvent> {
            this.returnToLobby()
        }
    }

    private fun onPlayerJoinEarly(event: PlayerJoinEvent) {
        event.joinMessageModification = PlayerJoinEvent.JoinMessageModification.Delay
    }

    private fun onPlayerJoin(event: PlayerJoinEvent) {
        //Don't bring the player to the lobby if they are in a minigame outside the lobby.
        if (event.player.getMinigame()?.let { !it.closed } ?: false) return
        this.current.players.add(event.player) // , admin = event.player.hasPermission(PermissionLevel.ADMINS)
    }

    private fun readEventConfig(): EventConfiguration {
        val path = this.path.resolve("config.json")
        if (path.notExists()) {
            JsonUtils.encodeWith(EventConfiguration(), EventConfiguration.CODEC, path)
        }
        return when (val result = JsonUtils.decodeWith(EventConfiguration.CODEC, path)) {
            is DataResult.Success -> result.value
            is DataResult.Error -> {
                TwistsUtils.logger.error("Failed to read event config: ${result.message()}")
                result.partialValue.orElseGet(::EventConfiguration)
            }
        }
    }



}
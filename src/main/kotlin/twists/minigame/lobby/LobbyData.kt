package twists.minigame.lobby

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.casual.arcade.minigame.data.MinigameDataModule
import net.casual.arcade.utils.file.ReadableArchive
import net.casual.arcade.utils.file.ReadableArchive.Companion.parseJson
import net.casual.arcade.utils.math.location.providers.LocationProvider
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import twists.util.twists

class LobbyData(
    val spawn: LocationProvider = LocationProvider.DEFAULT,
): MinigameDataModule {
    companion object: MinigameDataModule.Provider {
        private const val LOBBY_DATA = "twists_lobby_data.json"

        private val CODEC: Codec<LobbyData> = RecordCodecBuilder.create { instance ->
            instance.group(
                LocationProvider.CODEC.fieldOf("spawn").forGetter(LobbyData::spawn),
            ).apply(instance, ::LobbyData)
        }

        val DEFAULT = LobbyData()

        override val id: Identifier = twists("lobby_data")

        override fun get(archive: ReadableArchive, server: MinecraftServer): MinigameDataModule {
            return archive.parseJson(LOBBY_DATA, CODEC, server).getOrThrow()
        }
    }
}
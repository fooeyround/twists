package twists.minigame.worldless

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier
import twists.util.LevelUtils


class WorldlessMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return codec
    }

    override fun create(context: MinigameCreationContext): WorldlessMinigame {
        val worldless = LevelUtils.createNewVanillaLikeLevels(context.server)
        return WorldlessMinigame(
            context.server,
            context.uuid,
            worldless
        )

    }

    companion object : CodecProvider<WorldlessMinigameFactory> {
        override val id: Identifier
            get() = WorldlessMinigame.id
        override val codec: MapCodec<out WorldlessMinigameFactory>
            get() = MapCodec.unit(WorldlessMinigameFactory())
    }

}


package twists.minigame.worldless

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier


class WorldlessMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return CODEC
    }


    override fun create(context: MinigameCreationContext): WorldlessMinigame {
        val worldless = WorldlessMinigame.createNewVanillaLikeLevels(context.server)
        return WorldlessMinigame(
            context.server,
            context.uuid,
            worldless
        )

    }

    companion object : CodecProvider<WorldlessMinigameFactory> {
        override val ID: Identifier
            get() = WorldlessMinigame.ID
        override val CODEC: MapCodec<out WorldlessMinigameFactory>
            get() = MapCodec.unit(WorldlessMinigameFactory())
    }


}


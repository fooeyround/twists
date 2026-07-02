package twists.minigame.fatal_charity

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier


class FatalCharityMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return codec
    }

    override fun create(context: MinigameCreationContext): FatalCharityMinigame {
        return FatalCharityMinigame(
            context.server,
            context.uuid,
        )

    }

    companion object : CodecProvider<FatalCharityMinigameFactory> {
        override val id: Identifier
            get() = FatalCharityMinigame.id
        override val codec: MapCodec<out FatalCharityMinigameFactory>
            get() = MapCodec.unit(FatalCharityMinigameFactory())
    }

}


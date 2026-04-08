package twists.minigame.deathswap

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier
import twists.util.LevelUtils


class DeathSwapMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return codec
    }

    override fun create(context: MinigameCreationContext): DeathSwapMinigame {
        return DeathSwapMinigame(
            context.server,
            context.uuid,
        )

    }

    companion object : CodecProvider<DeathSwapMinigameFactory> {
        override val id: Identifier
            get() = DeathSwapMinigame.ID
        override val codec: MapCodec<out DeathSwapMinigameFactory>
            get() = MapCodec.unit(DeathSwapMinigameFactory())
    }

}


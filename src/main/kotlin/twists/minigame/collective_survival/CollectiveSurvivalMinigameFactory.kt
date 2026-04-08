package twists.minigame.collective_survival

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier


class SkyBlockMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return codec
    }


    override fun create(context: MinigameCreationContext): CollectiveSurvivalMinigame {
        return CollectiveSurvivalMinigame(
            context.server,
            context.uuid,
        )

    }

    companion object : CodecProvider<SkyBlockMinigameFactory> {
        override val id: Identifier
            get() = CollectiveSurvivalMinigame.ID
        override val codec: MapCodec<out SkyBlockMinigameFactory>
            get() = MapCodec.unit(SkyBlockMinigameFactory())
    }


}


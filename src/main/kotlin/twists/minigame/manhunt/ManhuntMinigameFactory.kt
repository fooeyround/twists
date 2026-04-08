package twists.minigame.manhunt

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier
import twists.util.LevelUtils


class ManhuntMinigameFactory(val seed: Long?) : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return codec
    }


    override fun create(context: MinigameCreationContext): ManhuntMinigame {
        val worlds = LevelUtils.createNewVanillaLikeLevels(context.server, locatorBar = true, seed = seed)
        return ManhuntMinigame(
            context.server,
            context.uuid,
            worlds
        )

    }

    companion object : CodecProvider<ManhuntMinigameFactory> {
        override val id: Identifier
            get() = ManhuntMinigame.id
        override val codec: MapCodec<out ManhuntMinigameFactory>
            get() = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                        Codec.LONG.optionalFieldOf("seed", null).forGetter(ManhuntMinigameFactory::seed)
                ).apply(instance, ::ManhuntMinigameFactory)
            }
        val DEFAULT = ManhuntMinigameFactory(null)
    }


}


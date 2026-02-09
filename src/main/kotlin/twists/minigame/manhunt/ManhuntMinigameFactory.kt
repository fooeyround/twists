package twists.minigame.manhunt

import com.mojang.serialization.MapCodec
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeLevels
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier
import twists.util.LevelUtils


class ManhuntMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return CODEC
    }


    override fun create(context: MinigameCreationContext): ManhuntMinigame {
        val worldless = LevelUtils.createNewVanillaLikeLevels(context.server)

        return ManhuntMinigame(
            context.server,
            context.uuid,
            worldless
        )

    }

    companion object : CodecProvider<ManhuntMinigameFactory> {
        override val ID: Identifier
            get() = ManhuntMinigame.ID
        override val CODEC: MapCodec<out ManhuntMinigameFactory>
            get() = MapCodec.unit(ManhuntMinigameFactory())
        val DEFAULT = ManhuntMinigameFactory()
    }


}


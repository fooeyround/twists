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
        return codec
    }


    override fun create(context: MinigameCreationContext): ManhuntMinigame {
        val worlds = LevelUtils.createNewVanillaLikeLevels(context.server, locatorBar = true)
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
            get() = MapCodec.unit(ManhuntMinigameFactory())
        val DEFAULT = ManhuntMinigameFactory()
    }


}


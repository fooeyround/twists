package twists.minigame.deathswap.deathswap

import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.serialization.MinigameFactory
import net.casual.arcade.utils.serialization.codec.CodecProvider
import net.minecraft.resources.Identifier
import twists.util.LevelUtils


class DeathSwapMinigameFactory : MinigameFactory {
    override fun codec(): MapCodec<out MinigameFactory> {
        return CODEC
    }


    override fun create(context: MinigameCreationContext): DeathSwapMinigame {
        val worlds = LevelUtils.createNewVanillaLikeLevels(context.server)
        return DeathSwapMinigame(
            context.server,
            context.uuid,
            worlds
        )

    }

    companion object : CodecProvider<DeathSwapMinigameFactory> {
        override val ID: Identifier
            get() = DeathSwapMinigame.ID
        override val CODEC: MapCodec<out DeathSwapMinigameFactory>
            get() = MapCodec.unit(DeathSwapMinigameFactory())
        val DEFAULT = DeathSwapMinigameFactory()
    }


}


package twists.stats

import net.casual.arcade.minigame.stats.StatType
import net.casual.arcade.minigame.utils.MinigameRegistries
import net.minecraft.core.Holder
import twists.util.twists
import net.minecraft.core.Registry


object TwistsStats {
    val LAST_SNEAK_TIME = this.register("last_sneak_time", StatType.int32())



    internal fun load() {}

    @Suppress("UNCHECKED_CAST")
    private fun <T: Any> register(name: String, type: StatType<T>): Holder.Reference<StatType<T>> {
        return Registry.registerForHolder(MinigameRegistries.STAT_TYPES, twists(name), type)
                as Holder.Reference<StatType<T>>
    }

}
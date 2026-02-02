package twists.util

import net.casual.arcade.utils.Identifier
import net.minecraft.resources.Identifier


fun twists(path: String): Identifier {
    return Identifier(TwistsUtils.MOD_ID, path)
}

object TwistsUtils {
    const val MOD_ID = "twists"

}
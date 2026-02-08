package twists.util

import net.casual.arcade.utils.Identifier
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path


fun twists(path: String): Identifier {
    return Identifier(TwistsUtils.MOD_ID, path)
}

object TwistsUtils {
    private val root = FabricLoader.getInstance().configDir.resolve("twists")

    const val MOD_ID = "twists"

    @JvmStatic
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    fun resolve(next: String): Path {
        return this.root.resolve(next)
    }

}
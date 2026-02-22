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

    fun <T> List<T>.randomDerangementBruteForce(): Map<T, T>? {
        if (this.size <= 1) {
            return null
        }

        val originalList = this
        var shuffledList: List<T>

        //TODO: DERANGED way to this. Use a better solution
        //
        do {
            shuffledList = originalList.shuffled()
        } while (originalList.indices.any { i -> originalList[i] == shuffledList[i] })

        return originalList.zip(shuffledList).toMap()
    }

    fun <T> List<T>.singleLeftShiftedDerangement(): Map<T, T>? {
        if (this.size <= 1) {
            return null
        }

        val listA = this.shuffled()
        val listB = ArrayList<T>()
        listA.forEach { listB.add(it) }
        listB.add(listB.removeAt(0))

        return listA.zip(listB).toMap()
    }




}
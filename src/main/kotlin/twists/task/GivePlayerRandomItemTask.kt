package twists.task

import net.casual.arcade.minigame.Minigame
import net.casual.arcade.scheduler.task.Task
import net.casual.arcade.utils.TimeUtils.Days
import net.casual.arcade.utils.TimeUtils.Seconds
import net.minecraft.world.item.ItemStack
import twists.util.ItemUtils
import twists.util.TwistsUtils

class RecurringMinigameTaskTest(
    val minigame: Minigame
): Task {
    override fun run() {
        minigame.scheduler.scheduleInLoop(10.Seconds, 10.Seconds, 99999.Days) {
            minigame.players.playing.forEach {
                val item = ItemUtils.randomItem(it.random)
                if (item != null) {
                    it.inventory.add(ItemStack(item))
                } else {
                    TwistsUtils.logger.error("Could not give player random item!")
                }
            }
        }
    }
}



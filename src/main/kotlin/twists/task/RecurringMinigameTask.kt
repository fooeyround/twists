package twists.task

import net.casual.arcade.minigame.Minigame
import net.casual.arcade.scheduler.task.Task
import net.casual.arcade.utils.time.MinecraftTimeDuration

/**
 * This schedules a [task] in a loop with a given [interval] between
 * each invocation of the [task] until the [minigame] is closed.
 *
 * @param interval The amount of time between each [task].
 * @param task The task to be scheduled.
 */
class RecurringMinigameTask(
    val minigame: Minigame,
    val interval: MinecraftTimeDuration,
    val task: Task,
): Task {
    override fun run() {
        if (!minigame.closed) {
            task.run()
            minigame.scheduler.schedule(interval, this)
        }
    }
}
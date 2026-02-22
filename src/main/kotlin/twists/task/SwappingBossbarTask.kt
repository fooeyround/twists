package twists.task

import com.google.gson.JsonObject
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.task.MinigameTaskCreationContext
import net.casual.arcade.minigame.task.MinigameTaskFactory
import net.casual.arcade.minigame.task.impl.BossbarTask
import net.casual.arcade.scheduler.task.SavableTask
import net.casual.arcade.scheduler.task.Task
import net.casual.arcade.scheduler.task.serialization.TaskSerializationContext
import twists.ui.SwappingBossbar
import twists.util.twists

class SwappingBossbarTask(
    minigame: Minigame
): BossbarTask<SwappingBossbar>(minigame, SwappingBossbar()), SavableTask {
    override val id = Companion.id

    override fun serialize(context: TaskSerializationContext): JsonObject {
        return this.bar.writeData(context)
    }

    companion object: MinigameTaskFactory<Minigame> {
        override val id = twists("swapping_boss_bar_task")

        override fun create(context: MinigameTaskCreationContext<Minigame>): Task {
            return SwappingBossbarTask(context.minigame).readData(context)
        }
    }
}
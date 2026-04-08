package twists.task

import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.task.MinigameTaskCreationContext
import net.casual.arcade.minigame.task.MinigameTaskFactory
import net.casual.arcade.minigame.task.impl.BossbarTask
import net.casual.arcade.scheduler.task.SavableTask
import net.casual.arcade.scheduler.task.Task
import net.casual.arcade.scheduler.task.serialization.TaskSerializationContext
import net.casual.arcade.utils.error.RichResult
import net.minecraft.network.chat.Component
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import twists.ui.SwappingBossbar
import twists.util.twists

class TitledBossbarTask(
    minigame: Minigame,
    val title: Component,
): BossbarTask<SwappingBossbar>(minigame, SwappingBossbar(title)), SavableTask {
    override val id = Companion.id

    override fun serialize(output: ValueOutput, context: TaskSerializationContext) {
        return this.bar.writeData(output,context)
    }

    companion object: MinigameTaskFactory<Minigame> {
        override val id = twists("titled_boss_bar_task")

        override fun create(input: ValueInput, context: MinigameTaskCreationContext<Minigame>): RichResult<Task> {
            return RichResult.success(TitledBossbarTask(context.minigame, Component.literal("TOODFIXTHIS")).readData(input, context))
        }
    }
}
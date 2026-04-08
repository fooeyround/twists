package twists.minigame.worldless

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.then
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.withDuration
import net.casual.arcade.minigame.task.impl.PhaseChangeTask
import net.casual.arcade.utils.TimeUtils.Ticks
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.network.chat.Component
import twists.task.TitledBossbarTask

internal const val INITIALIZATION_ID = "initialization"
internal const val RESETTING_WORLD_ID = "resetting_world"
internal const val PLAYING_ID = "playing"

enum class WorldlessPhase(override val id: String): Phase<WorldlessMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: WorldlessMinigame, previous: Phase<WorldlessMinigame>) {
//            WorldlessTeleporter.teleport(minigame.overworld, minigame.players.playing, false)
            minigame.players.forEach {
               it.teleportTo( minigame.levels.spawn.get(it)!!)
            }
            minigame.setPhase(ResettingWorld)
        }
    },
    ResettingWorld(RESETTING_WORLD_ID) {
        override fun start(minigame: WorldlessMinigame, previous: Phase<WorldlessMinigame>) {
            minigame.switchToNewWorld()
            minigame.setPhase(Playing)
        }
    },
    Playing(PLAYING_ID) {
        override fun start(minigame: WorldlessMinigame, previous: Phase<WorldlessMinigame>) {
            val task = TitledBossbarTask(minigame, Component.literal("Swapping"))
                .withDuration(minigame.settings.worldResetTime - 1.Ticks)
                .then(PhaseChangeTask(minigame, ResettingWorld))
            minigame.scheduler.schedulePhasedCancellable(minigame.settings.worldResetTime, task).runIfCancelled()
        }
    }
}
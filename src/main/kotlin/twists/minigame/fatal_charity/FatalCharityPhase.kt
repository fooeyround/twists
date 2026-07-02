package twists.minigame.fatal_charity

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.then
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.withDuration
import net.casual.arcade.minigame.task.impl.PhaseChangeTask
import net.casual.arcade.utils.TimeUtils.Ticks
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.network.chat.Component
import twists.task.TitledBossbarTask

internal const val INITIALIZATION_ID = "initialization"
internal const val PLAYING_ID = "playing"
internal const val COMPLETE_ID = "complete"


enum class FatalCharityPhase(override val id: String): Phase<FatalCharityMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: FatalCharityMinigame, previous: Phase<FatalCharityMinigame>) {
            minigame.players.forEach {
               it.teleportTo( minigame.levels.spawn.get(it)!!)
            }
            minigame.setPhase(Playing)
        }
    },
    Playing(PLAYING_ID) {
        override fun start(minigame: FatalCharityMinigame, previous: Phase<FatalCharityMinigame>) {

        }
    },
    Complete(COMPLETE_ID) {}
}
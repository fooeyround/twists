package twists.minigame.collective_survival

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.scheduler.GlobalTickedScheduler
import net.casual.arcade.utils.entity.teleportTo
import net.casual.arcade.utils.player.clearPlayerInventory
import net.minecraft.world.level.GameType

internal const val INITIALIZATION_ID = "initialization"
internal const val PLAYING_ID = "playing"

enum class CollectiveSurvivalPhase(override val id: String) : Phase<CollectiveSurvivalMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: CollectiveSurvivalMinigame, previous: Phase<CollectiveSurvivalMinigame>) {
            minigame.settings.canBreakBlocks.set(true)
            minigame.settings.tickFreezeOnPause.set(true)

            minigame.players.forEach {
                it.teleportTo(minigame.levels.spawn.get(it)!!)
                //TODO: this should be unneeded
                it.clearPlayerInventory()
            }

            GlobalTickedScheduler.later {
                minigame.players.playing.forEach {
                    it.setGameMode(GameType.SURVIVAL)
                }
            }

            minigame.setPhase(Playing)
        }
    },
    Playing(PLAYING_ID),
}
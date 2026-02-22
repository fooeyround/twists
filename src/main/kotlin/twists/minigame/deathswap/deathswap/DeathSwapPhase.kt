package twists.minigame.deathswap.deathswap

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.then
import net.casual.arcade.minigame.task.impl.BossbarTask.Companion.withDuration
import net.casual.arcade.minigame.task.impl.PhaseChangeTask
import net.casual.arcade.scheduler.GlobalTickedScheduler
import net.casual.arcade.utils.PlayerUtils.clearPlayerInventory
import net.casual.arcade.utils.TimeUtils.Seconds
import net.casual.arcade.utils.TimeUtils.Ticks
import net.casual.arcade.utils.math.location.LocationWithLevel.Companion.locationWithLevel
import net.casual.arcade.utils.teleportTo
import net.minecraft.world.level.GameType
import twists.task.SwappingBossbarTask
import twists.util.TwistsUtils
import twists.util.TwistsUtils.singleLeftShiftedDerangement

internal const val INITIALIZATION_ID = "initialization"
internal const val PLAYING_ID = "playing"
internal const val SWAPPING_PHASE = "swapping"

enum class DeathSwapPhase(override val id: String) : Phase<DeathSwapMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: DeathSwapMinigame, previous: Phase<DeathSwapMinigame>) {
            minigame.settings.canPvp.set(false)
            minigame.settings.canBreakBlocks.set(true)
            minigame.settings.tickFreezeOnPause.set(true)


            minigame.overworld.dayTime = 1000
            minigame.overworld.getChunk(0, 0)
            minigame.players.forEach {
                it.teleportTo(minigame.levels.spawn.get(it)!!)
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
    Playing(PLAYING_ID) {
        override fun start(minigame: DeathSwapMinigame, previous: Phase<DeathSwapMinigame>) {
            val task = SwappingBossbarTask(minigame)
                .withDuration(minigame.settings.deathSwapCooldown - 1.Ticks)
                .then(PhaseChangeTask(minigame, Swapping))
            minigame.scheduler.schedulePhasedCancellable(minigame.settings.deathSwapCooldown, task).runIfCancelled()
        }
    },
    Swapping(SWAPPING_PHASE) {
        override fun start(minigame: DeathSwapMinigame, previous: Phase<DeathSwapMinigame>) {

            //TODO: It should be OKAY here to hold the players, as if MC is single threaded nothing should change!
            //HOPEFULLY THIS DOESN'T BECOME A PROBLEM :(
            val originalLocations = minigame.players.playing.zip(minigame.players.playing.map { it.locationWithLevel }).toMap()
            if (minigame.players.playing.size < 2) {
                TwistsUtils.logger.error("Cannot swap with less than 2 players")
            }
            val map = minigame.players.playing.singleLeftShiftedDerangement() ?: return
            map.forEach { (p1, p2) ->
                originalLocations[p1]?.let { p2.teleportTo(it) }
            }

            minigame.setPhase(Playing)
        }
    }
}
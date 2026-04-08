package twists.minigame.shared

import net.casual.arcade.events.server.block.BlockDropLootEvent
import net.casual.arcade.events.server.player.PlayerSetSneakingEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.events.MinigameSetPlayingEvent
import net.casual.arcade.minigame.events.MinigameSetSpectatingEvent
import net.casual.arcade.minigame.events.MinigameStartEvent
import net.casual.arcade.minigame.gamemode.ExtendedGameMode
import net.casual.arcade.minigame.gamemode.ExtendedGameMode.Companion.extendedGameMode
import net.casual.arcade.utils.TimeUtils.Days
import net.casual.arcade.utils.TimeUtils.Seconds
import net.casual.arcade.utils.entity.teleportTo
import net.casual.arcade.utils.player.revokeAllAdvancements
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.ItemStack
import twists.extension.SharedInventoryTeamExtension
import twists.extension.SharedInventoryTeamExtension.Companion.sharedInventoryExtension
import twists.stats.TwistsStats
import twists.task.RecurringMinigameTask
import twists.util.ItemUtils
import twists.util.TwistsUtils
import java.util.*
import kotlin.math.abs


abstract class TwistedMinigame(
    server: MinecraftServer,
    uuid: UUID,
): Minigame(server, uuid) {
    override val settings = TwistSettings(this)

    init {
        this.players.keepPlayerData = false
    }

    @Listener
    private fun twistedMinigameSetPlaying(event: MinigameSetPlayingEvent) {
        //TODO: this should not be needed if `keepPlayerData = true`, but it is.
        event.player.revokeAllAdvancements()
    }

    //TODO: allow the setting to be updated mid game
    @Listener
    private fun communalPocketsOnSetPlaying(event: MinigameSetPlayingEvent) {
        event.player.team?.let {
            it.sharedInventoryExtension.shareLevel = this.settings.communalPockets
        }
    }

    @Listener
    private fun communalPocketsOnMinigameClose(event: MinigameCloseEvent) {
        (event.minigame.teams.getPlayingTeams() + event.minigame.teams.getEliminatedTeams()).forEach {
            it.sharedInventoryExtension.shareLevel = SharedInventoryTeamExtension.ShareLevel.None
        }
    }

    @Listener
    private fun itemsEveryMinuteTwist(event: MinigameStartEvent) {
        val (minigame) = event
        if (minigame.settings.randomItemOnInterval) {
            val task = RecurringMinigameTask(minigame, 10.Seconds) {
                minigame.players.playing.forEach {
                    val item = ItemUtils.randomItem(it.random)
                    if (item != null) {
                        it.inventory.add(ItemStack(item))
                    } else {
                        TwistsUtils.logger.error("Could not give player random item!")
                    }
                }
            }
            minigame.scheduler.schedule(10.Seconds, task)
        }

    }






    @Listener(flags = ListenerFlags.IS_SPECTATOR)
    private fun onPlayerSneak(event: PlayerSetSneakingEvent) {
        val (player, sneaking) = event
        if (!player.isShiftKeyDown && sneaking) {
            val last = this.stats.getOrCreateStat(player, TwistsStats.LAST_SNEAK_TIME)
            if (abs(this.server.tickCount - last.value) < 7) {
                val mode = when (player.extendedGameMode) {
                    ExtendedGameMode.AdventureSpectator -> ExtendedGameMode.NoClipSpectator
                    else -> ExtendedGameMode.AdventureSpectator
                }
                player.extendedGameMode = mode
            } else {
                last.modify { this.server.tickCount }
            }
        }
    }

    @Listener
    private fun onSetSpectating(event: MinigameSetSpectatingEvent) {
        event.player.extendedGameMode = ExtendedGameMode.AdventureSpectator
        this.effects.addFullbright(event.player)
        if (!this.levels.has(event.player.level())) {
            this.levels.spawn.get(event.player)?.let {
                event.player.teleportTo(it)
            }
        }
    }

    @Listener
    private fun ff(event: BlockDropLootEvent) {
//        val lootTableKey: ResourceKey<LootTable> = event.level.server.reloadableRegistries().
//        val lootTable: LootTable? = event.level.server?.reloadableRegistries()?.getLootTable(this.drops.get())
    }


}
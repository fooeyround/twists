package twists.event

import net.casual.arcade.events.common.CancellableEvent
import net.casual.arcade.events.common.Event
import net.minecraft.core.BlockPos
import net.minecraft.world.attribute.BedRule
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class BedExplodeEvent(
    bedRule: BedRule,
    state: BlockState,
    level: Level,
    pos: BlockPos,
    player: Player,
    hitResult: BlockHitResult
) : Event, CancellableEvent.Default()
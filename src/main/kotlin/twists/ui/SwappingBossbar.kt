package twists.ui

import net.casual.arcade.utils.MathUtils
import net.casual.arcade.utils.TimeUtils.formatMMSS
import net.casual.arcade.visuals.bossbar.TimerBossbar
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.BossEvent

class SwappingBossbar: TimerBossbar() {
    override fun getTitle(player: ServerPlayer): Component {
        return Component.literal("Swap in ${this.getRemainingDuration().formatMMSS()}")
    }

    override fun getProgress(player: ServerPlayer): Float {
        return MathUtils.centeredScale(super.getProgress(player), 1F)
    }

    override fun getColor(player: ServerPlayer): BossEvent.BossBarColor {
        return BossEvent.BossBarColor.RED
    }

    override fun getOverlay(player: ServerPlayer): BossEvent.BossBarOverlay {
        return BossEvent.BossBarOverlay.PROGRESS
    }
}
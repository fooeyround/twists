package twists.event

import net.casual.arcade.events.server.player.PlayerEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity

/**
 * Called to check if a waypoint connection should be left broken.
 * This is useful if you want to hide certain players from others in a minigame or alike.
 * @param player the receiver of the waypoint.
 * @param source the source entity of the waypoint.
 * @property hideSource determines if source should be hidden from player even if vanilla's conditions are met.
 */
class WaypointTransmitterReceiveEvent(override val player: ServerPlayer, val source: LivingEntity) : PlayerEvent {
    var hideSource: Boolean = false
}
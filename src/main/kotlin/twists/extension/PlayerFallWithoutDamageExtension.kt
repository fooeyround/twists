package twists.extension

import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.player.PlayerDamageEvent
import net.casual.arcade.events.server.player.PlayerFallEvent
import net.casual.arcade.extensions.Extension
import net.casual.arcade.extensions.event.PlayerExtensionEvent
import net.casual.arcade.extensions.utils.getExtension
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes

class PlayerFallWithoutDamageExtension(
    private var takeNoDamageNextFall: Boolean = false
) : Extension {

    companion object {
        fun ServerPlayer.takeNoDamageOnNextFall() {
            this.playerFallWithoutDamageExtension.takeNoDamageNextFall = true
        }

        val ServerPlayer.playerFallWithoutDamageExtension: PlayerFallWithoutDamageExtension
            get() = this.getExtension()

        internal fun registerEvents() {
            GlobalEventHandler.Server.register<PlayerExtensionEvent> { event ->
                event.addExtension(PlayerFallWithoutDamageExtension())
            }
            GlobalEventHandler.Server.register<PlayerDamageEvent> { event ->
                if (event.source.`is`(DamageTypes.FALL) && event.player.playerFallWithoutDamageExtension.takeNoDamageNextFall) {
                    event.player.playerFallWithoutDamageExtension.takeNoDamageNextFall = false
                    event.cancel()
                }
            }
            GlobalEventHandler.Server.register<PlayerFallEvent> { event ->
                if (event.onGround) event.player.playerFallWithoutDamageExtension.takeNoDamageNextFall = false
            }
        }
    }


}
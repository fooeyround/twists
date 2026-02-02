package twists.extension

import com.mojang.serialization.Codec
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.player.PlayerDamageEvent
import net.casual.arcade.events.server.player.PlayerFallEvent
import net.casual.arcade.extensions.PlayerExtension
import net.casual.arcade.extensions.SerializableExtension
import net.casual.arcade.extensions.event.PlayerExtensionEvent
import net.casual.arcade.extensions.utils.getExtension
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import twists.util.twists
import kotlin.jvm.optionals.getOrNull

class PlayerFallWithoutDamageExtension(
    player: ServerPlayer,
    private var takeNoDamageNextFall: Boolean = false
) : PlayerExtension(player), SerializableExtension {
    override fun id(): Identifier = twists("player_fall_without_damage_extension")

    override fun serialize(output: ValueOutput) {
        output.storeNullable("take_no_damage_next_fall", Codec.BOOL, this.takeNoDamageNextFall)
    }

    override fun deserialize(input: ValueInput) {
        this.takeNoDamageNextFall = input.read("take_no_damage_next_fall", Codec.BOOL).getOrNull() ?: false
    }

    companion object {
        fun ServerPlayer.takeNoDamageOnNextFall() {
            this.playerFallWithoutDamageExtension.takeNoDamageNextFall = true
        }

        val ServerPlayer.playerFallWithoutDamageExtension: PlayerFallWithoutDamageExtension
            get() = this.getExtension()

        internal fun registerEvents() {
            GlobalEventHandler.Server.register<PlayerExtensionEvent> { event ->
                event.addExtension(PlayerFallWithoutDamageExtension(event.player))
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
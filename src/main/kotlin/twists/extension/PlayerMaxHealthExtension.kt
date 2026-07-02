package twists.extension

import com.mojang.serialization.Codec
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.player.PlayerAttributeUpdatedEvent
import net.casual.arcade.events.server.player.PlayerDamageEvent
import net.casual.arcade.events.server.player.PlayerFallEvent
import net.casual.arcade.extensions.PlayerExtension
import net.casual.arcade.extensions.SerializableExtension
import net.casual.arcade.extensions.event.PlayerExtensionEvent
import net.casual.arcade.extensions.utils.getExtension
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import twists.util.TwistsUtils
import twists.util.twists
import kotlin.jvm.optionals.getOrNull

class PlayerMaxHealthExtension(
    player: ServerPlayer,
    private var maxHealthModifier: Double = 0.0
) : PlayerExtension(player), SerializableExtension {
    override fun id(): Identifier = twists("player_max_health_extension")

    override fun serialize(output: ValueOutput) {
        output.storeNullable("twists_max_health_modifier", Codec.DOUBLE, this.maxHealthModifier)
    }

    override fun deserialize(input: ValueInput) {
        this.maxHealthModifier = input.read("twists_max_health_modifier", Codec.DOUBLE).getOrNull() ?: 0.0
    }

    companion object {
        fun ServerPlayer.grantHeart() {
            this.playerMaxHealthExtension.maxHealthModifier += 2.0
            updateAttributes(this)

        }
        fun ServerPlayer.revokeHeart() {
            this.playerMaxHealthExtension.maxHealthModifier -= 2.0
            updateAttributes(this)
        }

        fun ServerPlayer.addHearts(value: Double) {
            this.playerMaxHealthExtension.maxHealthModifier += value
            updateAttributes(this)
        }



        val ServerPlayer.playerMaxHealthExtension: PlayerMaxHealthExtension
            get() = this.getExtension()

        internal fun registerEvents() {
            GlobalEventHandler.Server.register<PlayerExtensionEvent> { event ->
                event.addExtension(PlayerMaxHealthExtension(event.player))
            }
            GlobalEventHandler.Server.register<PlayerAttributeUpdatedEvent> { event ->
                updateAttributes(event.player)
            }
        }

        internal fun updateAttributes(player: ServerPlayer) {
            val playerMaxHealthAttribute = player.attributes.getInstance(Attributes.MAX_HEALTH)

            val playerMaxHealthModifierAmount = playerMaxHealthAttribute?.getModifier(twists("fatal_charity")) ?: 0.0

            if (playerMaxHealthModifierAmount != player.playerMaxHealthExtension.maxHealthModifier) {
                playerMaxHealthAttribute?.addOrReplacePermanentModifier(
                    AttributeModifier(
                        twists("fatal_charity"),
                        player.playerMaxHealthExtension.maxHealthModifier,
                        AttributeModifier.Operation.ADD_VALUE
                    )
                )
            }
        }

    }


}
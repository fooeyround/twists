package twists.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.casual.arcade.utils.uuid
import net.minecraft.core.GlobalPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.LodestoneTracker
import net.minecraft.world.level.Level
import xyz.nucleoid.packettweaker.PacketContext
import java.util.*

class TrackingCompassItem(properties: Properties) : Item(properties), PolymerItem {
    override fun getPolymerItem(
        itemStack: ItemStack,
        context: PacketContext
    ): Item {
        return Items.COMPASS
    }

    override fun getPolymerItemStack(
        itemStack: ItemStack,
        tooltipType: TooltipFlag,
        context: PacketContext
    ): ItemStack {
        val stack = super.getPolymerItemStack(itemStack, tooltipType, context)
        if (itemStack.get(DataComponents.LODESTONE_TRACKER) != null) {
            stack.set(DataComponents.LODESTONE_TRACKER, itemStack.get(DataComponents.LODESTONE_TRACKER))
        } else {
            stack.set(DataComponents.LODESTONE_TRACKER, LodestoneTracker(Optional.empty(), false))
        }
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
        return stack
    }

    override fun getPolymerItemModel(stack: ItemStack?, context: PacketContext?): Identifier? {
        return null
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val item = player.getItemInHand(hand)
//        val profile = item.components.get(DataComponents.PROFILE)
        val customData: CustomData? = item.components.get(DataComponents.CUSTOM_DATA)
        val trackablePlayers = level.players().filter {
            val data = customData?.copyTag()?.getString("trackingTeam")
            return@filter data != null && data.isPresent && data.get() == it.team?.name
        }.sortedBy {
            player.distanceTo(it)
        }
        if (trackablePlayers.isNotEmpty()) {
            val trackedPlayer = trackablePlayers.first()
            val playerPos = GlobalPos.of(trackedPlayer.level().dimension(), trackedPlayer.blockPosition())
            item.set(DataComponents.LODESTONE_TRACKER, LodestoneTracker(Optional.of(playerPos), false))
        }

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.LODESTONE_COMPASS_LOCK,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )

        return InteractionResult.CONSUME

        /*


        val loadStoneTracker = item.components.get(DataComponents.LODESTONE_TRACKER)
        if (profile != null) {
            val id = profile.uuid()
            if (id.isPresent) {
                val trackedPlayer = level.getPlayerByUUID(id.get())
                if (trackedPlayer != null) {
                    val playerPos = GlobalPos.of(trackedPlayer.level().dimension(), trackedPlayer.blockPosition())
                    var trackedPosition: Optional<GlobalPos> = Optional.empty()
                    if (loadStoneTracker != null && loadStoneTracker.target.isPresent) {
                        trackedPosition = Optional.of(loadStoneTracker.target.get())
                    }
                    if (playerPos.dimension == player.level().dimension()) {
                        trackedPosition = Optional.of(playerPos)
                    }
                    level.playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.LODESTONE_COMPASS_LOCK,
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f
                    )

                    item.set(DataComponents.LODESTONE_TRACKER, LodestoneTracker(trackedPosition, false))
                    return InteractionResult.CONSUME

                }
            }
        }
        //TODO: option to not let the player know if it failed or not.
//        if (player is ServerPlayer && false) {
//            player.sendSystemMessage(Component.literal("Tracking Failed").withStyle(ChatFormatting.RED), true)
//        }

        return super.use(level, player, hand)
         */
    }


    override fun getName(stack: ItemStack): Component {
        return Component.literal("Tracking Compass")
    }
}
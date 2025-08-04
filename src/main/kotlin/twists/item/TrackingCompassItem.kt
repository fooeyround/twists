package twists.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.ChatFormatting
import net.minecraft.core.GlobalPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.LodestoneTracker
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.SkullBlockEntity
import xyz.nucleoid.packettweaker.PacketContext
import java.util.*

class TrackingCompassItem(properties: Properties) : Item(properties), PolymerItem {
    override fun getPolymerItem(
        p0: ItemStack?,
        p1: PacketContext?
    ): Item {
        return Items.COMPASS
    }

    override fun getPolymerItemStack(
        itemStack: ItemStack,
        tooltipType: TooltipFlag,
        context: PacketContext
    ): ItemStack {
        val stack =  super.getPolymerItemStack(itemStack, tooltipType, context)
        if (itemStack.get(DataComponents.LODESTONE_TRACKER) != null) {
            stack.set(DataComponents.LODESTONE_TRACKER, itemStack.get(DataComponents.LODESTONE_TRACKER))
        } else {
            stack.set(DataComponents.LODESTONE_TRACKER, LodestoneTracker(Optional.empty(), false))
        }
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, itemStack.get(DataComponents.PROFILE) != null)
        return stack
    }

    override fun getPolymerItemModel(stack: ItemStack?, context: PacketContext?): ResourceLocation? {
        return null
    }

    override fun verifyComponentsAfterLoad(stack: ItemStack) {
        val resolvableProfile = stack.get(DataComponents.PROFILE)
        if (resolvableProfile != null && !resolvableProfile.isResolved) {
            resolvableProfile.resolve()
                .thenAcceptAsync({ profile ->
                    stack.set(
                        DataComponents.PROFILE,
                        profile
                    )
                }, SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR)
        }
    }


    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult? {
        val item = player.getItemInHand(hand)
        val profile = item.components.get(DataComponents.PROFILE)
        if (profile != null) {
            val id = profile.id
            if (id.isPresent) {
                val trackedPlayer = level.getPlayerByUUID(id.get())
                if (trackedPlayer != null && trackedPlayer.level().dimension() == player.level().dimension()) {
                    item.set(DataComponents.LODESTONE_TRACKER, LodestoneTracker(Optional.of(GlobalPos.of(trackedPlayer.level().dimension(), trackedPlayer.blockPosition())), false))
                    level.playSound(null, player.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0f, 1.0f)
                    return InteractionResult.CONSUME
                }
            }
        }
        if (player is ServerPlayer) {
            player.sendSystemMessage(Component.literal("Tracking Failed").withStyle(ChatFormatting.RED), true)
        }

        return super.use(level, player, hand)
    }



    override fun getName(stack: ItemStack): Component? {
        return Component.literal("Tracking Compass")
    }
}
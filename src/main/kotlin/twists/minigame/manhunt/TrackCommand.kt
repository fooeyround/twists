package twists.minigame.manhunt

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.casual.arcade.commands.CommandTree
import net.casual.arcade.commands.literal
import net.casual.arcade.utils.component.unitalicize
import net.casual.arcade.utils.player.server
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.enchantment.Enchantment
import twists.Twists

object TrackCommand: CommandTree<CommandSourceStack> {
    override fun create(buildContext: CommandBuildContext): LiteralArgumentBuilder<CommandSourceStack> {
        return CommandTree.buildLiteral("track") {
            literal("runners") {
                executes {
                    val player = it.source.player ?: return@executes 1

                    if (player.team?.name != "hunters" || player.inventory.contains { stack ->  stack.item == Twists.TRACKING_COMPASS }) return@executes 0

                    val soulboundEnchantment: Holder.Reference<Enchantment> = player.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Twists.SOULBOUND_ENCHANTMENT)

                    val stack = ItemStack(Twists.TRACKING_COMPASS)
                    val tag = CompoundTag()

                    tag.putString("trackingTeam", "runners")
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal("Tracking Compass").unitalicize())

                    stack.enchant(soulboundEnchantment, 1)

                    player.inventory.add(stack)

                    return@executes 0
                }
            }
        }
    }
}
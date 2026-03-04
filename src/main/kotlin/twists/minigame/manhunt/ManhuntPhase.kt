package twists.minigame.manhunt

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.scheduler.GlobalTickedScheduler
import net.casual.arcade.utils.component.unitalicize
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.GameType
import twists.Twists
import twists.util.TwistsUtils

internal const val INITIALIZATION_ID = "initialization"
internal const val PLAYING_ID = "playing"

enum class ManhuntPhase(override val id: String) : Phase<ManhuntMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: ManhuntMinigame, previous: Phase<ManhuntMinigame>) {
            minigame.settings.canPvp.set(true)
            minigame.settings.canBreakBlocks.set(true)


            minigame.overworld.dayTime = 0
            minigame.overworld.getChunk(0, 0)
            minigame.players.forEach {
                it.teleportTo(minigame.levels.spawn.get(it)!!)
            }

            //TODO: allow better dynamic teams
            val runners = minigame.players.playing.filter { it.team?.name == "runners" }

            var soulboundEnchantment: Holder.Reference<Enchantment>? = null

            try {
                soulboundEnchantment =
                    minigame.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Twists.SOULBOUND_ENCHANTMENT)
            } catch (e: Exception) {
                TwistsUtils.logger.error("Failed to enchant with soulbound: $e")
            }


            minigame.players.playing.filter { !runners.contains(it) }.forEach { player ->
                    var stack = ItemStack(Twists.TRACKING_COMPASS)
                    var tag = CompoundTag()
                    tag.putString("trackingTeam", "runners")
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal("Tracking Compass").unitalicize())
                    if (soulboundEnchantment != null) {
                        stack.enchant(soulboundEnchantment, 1)
                    }
                    player.inventory.add(stack)
            }


//            if (event.player.team?.name == "hunter") {
//                var stack = ItemStack(Twists.TRACKING_COMPASS)
//                stack.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(event.player.uuid))
//                event.player.inventory.add(stack)
//            }

            GlobalTickedScheduler.later {
                minigame.players.playing.forEach {
                    it.setGameMode(GameType.SURVIVAL)
                }
            }

            minigame.setPhase(Playing)
        }
    },
    Playing(PLAYING_ID) {
        override fun start(minigame: ManhuntMinigame, previous: Phase<ManhuntMinigame>) {

        }
    }
}
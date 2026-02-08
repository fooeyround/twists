package twists.minigame.manhunt

import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.utils.PlayerUtils.server
import net.casual.arcade.utils.teleportTo
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import twists.Twists
import twists.util.TwistsUtils

internal const val INITIALIZATION_ID = "initialization"
internal const val PLAYING_ID = "playing"

enum class ManhuntPhase(override val id: String) : Phase<ManhuntMinigame> {
    Initialization(INITIALIZATION_ID) {
        override fun start(minigame: ManhuntMinigame, previous: Phase<ManhuntMinigame>) {
            minigame.overworld.dayTime = 0
            minigame.overworld.getChunk(0, 0)
            minigame.players.forEach {
                it.teleportTo(minigame.levels.spawn.get(it)!!)
            }


            //TOOD: allow better dynamic teams
            val runner = minigame.players.playing.find { it.team?.name == "runners" }

            if (runner != null) {
                minigame.players.playing.filter { it != runner }.forEach { player ->
                    var stack = ItemStack(Twists.TRACKING_COMPASS)
                    stack.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(runner.uuid))
                    stack.set(DataComponents.ITEM_NAME, Component.literal("Tracking Compass"))
                    try {
                        val soulboundEnchantment = runner.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                            .getOrThrow(Twists.SOULBOUND_ENCHANTMENT)
                        stack.enchant(soulboundEnchantment, 1)
                    } catch (e: Exception) {
                        TwistsUtils.logger.error("Failed to enchant with soulbound: $e")
                    }
                    player.inventory.add(stack)
                }
            }


//            if (event.player.team?.name == "hunter") {
//                var stack = ItemStack(Twists.TRACKING_COMPASS)
//                stack.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(event.player.uuid))
//                event.player.inventory.add(stack)
//            }


            minigame.setPhase(Playing)
        }
    },
    Playing(PLAYING_ID) {
        override fun start(minigame: ManhuntMinigame, previous: Phase<ManhuntMinigame>) {

        }
    }
}
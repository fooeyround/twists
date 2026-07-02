package twists.minigame.fatal_charity

import net.casual.arcade.dimensions.utils.deleteCustomLevel
import net.casual.arcade.events.BuiltInEventPhases
import net.casual.arcade.events.server.player.PlayerDeathEvent
import net.casual.arcade.minigame.Minigame
import net.casual.arcade.minigame.annotation.Listener
import net.casual.arcade.minigame.annotation.ListenerFlags
import net.casual.arcade.minigame.phase.Phase
import net.casual.arcade.utils.entity.teleportTo
import net.minecraft.client.gui.screens.social.PlayerEntry
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall
import twists.extension.PlayerMaxHealthExtension.Companion.grantHeart
import twists.extension.PlayerMaxHealthExtension.Companion.revokeHeart
import twists.minigame.shared.VanillaLikeLevelsSpawnLocation
import twists.minigame.shared.VanillaLikeTwistedMinigame
import twists.util.LevelUtils
import twists.util.TwistsUtils
import twists.util.twists
import java.util.*

class FatalCharityMinigame(
    server: MinecraftServer,
    uuid: UUID,
): VanillaLikeTwistedMinigame(server, uuid) {
    override val id = FatalCharityMinigame.id
    override val settings = FatalCharitySettings(this)
    override fun phases(): Collection<Phase<out Minigame>> {
        return FatalCharityPhase.entries
    }

    init {
        this.tickrate.useGlobalManager = false
    }



    @Listener(flags = ListenerFlags.IS_PLAYING, phase = BuiltInEventPhases.POST)
    private fun playerDeathEvent(event: PlayerDeathEvent) {
        val (victim, source)  = event
        val attacker = source.entity


        if (attacker is ServerPlayer) {
            TwistsUtils.logger.info("v ${victim.maxHealth} a ${attacker.maxHealth}")

            if (attacker.maxHealth <= 1F) {
                val aaa = attacker.maxHealth
                //Player wins, TODO: show who won
                this.setPhase(FatalCharityPhase.Complete)
            } else {
                attacker.revokeHeart()
                victim.grantHeart()
            }

        }


    }


    companion object {
        val id = twists("fatal_charity")
    }
}
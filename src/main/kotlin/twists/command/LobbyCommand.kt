package twists.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.casual.arcade.commands.CommandTree
import net.casual.arcade.commands.fail
import net.casual.arcade.commands.hidden.HiddenCommandContext
import net.casual.arcade.commands.literal
import net.casual.arcade.commands.success
import net.casual.arcade.minigame.utils.MinigameUtils.requiresAdminOrPermission
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import twists.minigame.lobby.LobbyMinigame
import twists.minigame.lobby.LobbyPhase

class LobbyCommand(val lobby: LobbyMinigame) : CommandTree {
    override fun create(buildContext: CommandBuildContext): LiteralArgumentBuilder<CommandSourceStack> {
        return CommandTree.buildLiteral("lobby") {
            requiresAdminOrPermission()
            literal("tp") {
                executes(::teleportToLobby)
            }
            literal("next") {
                literal("settings") {
                    executes(::viewNextMinigameSettings)
                }
            }
            literal("countdown") {
                executes(::startCountdown)
            }
            literal("start") {
                executes(::startNextMinigame)
            }
        }
    }

    private fun viewNextMinigameSettings(context: CommandContext<CommandSourceStack>): Int {
        val next = this.lobby.next ?: throw NO_MINIGAME.create()
        val player = context.source.playerOrException
        next.settings.gui(player).open()
        return Command.SINGLE_SUCCESS
    }

    private fun teleportToLobby(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        this.lobby.teleport(player)
        return context.source.success("Successfully teleported to the lobby")
    }

    private fun startCountdown(context: CommandContext<CommandSourceStack>): Int {
        this.lobby.next ?: return context.source.fail("Cannot move to next minigame, it has not been set!")
        this.lobby.setPhase(LobbyPhase.Countdown)
        return context.source.success("Successfully started the countdown")
    }

    private fun startNextMinigame(context: CommandContext<CommandSourceStack>): Int {
        this.lobby.next ?: return context.source.fail("Cannot move to next minigame, it has not been set!")
        val success = this.lobby.moveToNextMinigame()
        if (success) return context.source.success("Successfully moving to next minigame")

        return context.source.fail("Failed to move to next minigame, it was not specified or closed!")
    }

    @Suppress("unused_parameter")
    private fun startCountdown(context: HiddenCommandContext) {
        this.lobby.setPhase(LobbyPhase.Countdown)
    }

    companion object {
        private val NO_MINIGAME =
            SimpleCommandExceptionType(Component.translatable("minigame.lobby.command.noNextMinigame"))
    }
}
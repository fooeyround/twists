package twists.minigame.manhunt


import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.casual.arcade.commands.*
import net.casual.arcade.utils.PlayerUtils.addToTeam
import net.casual.arcade.utils.TeamUtils
import net.casual.arcade.utils.chat.ChatFormatter
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component

internal object TeamCommandModifier : CommandTree {
    override fun create(buildContext: CommandBuildContext): LiteralArgumentBuilder<CommandSourceStack> {
        return CommandTree.buildLiteral("team") {
            literal("manhunt") {
                literal("with") {
                    argument("players", EntityArgument.players()) {
                        argument("runner", EntityArgument.player()) {
                            executes { createManhuntTeams(it) }
                        }
                        executes { createManhuntTeams(it) }
                    }
                }
                literal("delete") {
                    executes(::deleteManhuntTeams)
                }
            }
        }
    }

    private fun createManhuntTeams(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, "players")
        val runner = EntityArgument.getPlayer(context, "runner")

        val server = context.source.server

        val huntersTeam = server.scoreboard.addPlayerTeam("hunters")
        val runnersTeam = server.scoreboard.addPlayerTeam("runners")

        huntersTeam.displayName = Component.literal("Hunters").withStyle(ChatFormatting.RED)
        huntersTeam.color = ChatFormatting.RED
        huntersTeam.setPlayerPrefix(Component.literal("").withStyle(ChatFormatting.RED))



        runnersTeam.displayName = Component.literal("Runners").withStyle(ChatFormatting.BLUE)
        runnersTeam.color = ChatFormatting.BLUE
        runnersTeam.setPlayerPrefix(Component.literal("").withStyle(ChatFormatting.BLUE))

        players.filter { it != runner }.forEach {
            it.addToTeam(huntersTeam)
        }

        runner.addToTeam(runnersTeam)



//        val generated = teams.joinToComponent { it.formattedDisplayName }
        return context.source.success(
            Component.literal("Players added to teams"), true
        )
    }

    private fun deleteManhuntTeams(context: CommandContext<CommandSourceStack>): Int {
        TeamUtils.deleteAllRandomTeams(context.source.server.scoreboard)
        return context.source.success(Component.translatable("minigame.command.team.randomizer.deleted"))
    }
}
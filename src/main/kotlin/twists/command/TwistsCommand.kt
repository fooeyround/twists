package twists.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.casual.arcade.commands.CommandTree
import net.casual.arcade.commands.literal
import net.casual.arcade.commands.success
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import twists.Twists

object TwistsCommand : CommandTree<CommandSourceStack> {
    override fun create(buildContext: CommandBuildContext): LiteralArgumentBuilder<CommandSourceStack> {
        return CommandTree.buildLiteral("twists") {
            literal("reload") {
                executes(::reloadAll)
                literal("minigame") {
                    executes(::reloadMinigame)
                }
            }

        }
    }


    private fun reloadAll(context: CommandContext<CommandSourceStack>): Int {
        Twists.reload(context.source.server)
        return context.source.success("Successfully reloaded config", true)
    }

    private fun reloadMinigame(context: CommandContext<CommandSourceStack>): Int {
        Twists.minigames.reload(context.source.server)
        return context.source.success("Successfully reloaded minigame")
    }
}
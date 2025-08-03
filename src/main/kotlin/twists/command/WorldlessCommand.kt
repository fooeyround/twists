package twists.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.casual.arcade.commands.CommandTree
import net.casual.arcade.commands.argument
import net.casual.arcade.commands.literal
import net.casual.arcade.commands.requiresPermission
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.TimeArgument
import twists.Twists

import twists.worldless.WorldlessMinecraftServerExtension.Companion.worldlessState

object WorldlessCommand {


    fun createWorldlessCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return CommandTree.buildLiteral("worldless") {
            requiresPermission(2)
            literal("start") {
                executes { it -> it.source.server.worldlessState.enabled = true; 1 }
            }
            literal("stop") {
                executes { it -> it.source.server.worldlessState.enabled = false; 1 }
            }
            literal("pause") {
                executes { it -> it.source.server.worldlessState.paused = true; 1 }
            }
            literal("resume") {
                executes { it -> it.source.server.worldlessState.paused = false; 1 }
            }

            literal("time") {
                literal("add") {
                    argument("time", TimeArgument.time()) {
                        executes { it ->
                            val time = it.getArgument("time", Int::class.java)
                            it.source.server.worldlessState.modifyTicksUntilReset(time.toLong())
                            1
                        }
                    }
                }
                literal("remove") {
                    argument("time", TimeArgument.time()) {
                        executes { it ->
                            val time = it.getArgument("time", Int::class.java)
                            it.source.server.worldlessState.modifyTicksUntilReset(-time.toLong())
                            1
                        }
                    }
                }
                literal("set") {
                    argument("time", TimeArgument.time()) {
                        executes { it ->
                            val time = it.getArgument("time", Int::class.java)
                            it.source.server.worldlessState.setTicksUntilReset(time.toLong())
                            1
                        }
                    }
                }
                literal("setdefault") {
                    argument("time", TimeArgument.time()) {
                        executes { it ->
                            val time = it.getArgument("time", Int::class.java)
                            it.source.server.worldlessState.setTicksPerWorld(time.toLong())
                            1
                        }
                    }
                }
            }

        }
    }


}
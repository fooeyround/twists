package twists.worldless;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

/// I wish I wrote this in Kotlin
public class WorldlessCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("twist").then(literal("worldless").then(
                literal("start").executes(context -> {
                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                        worldlessState.setEnabled(true);
                    }
                    return 1;
                }))
                .then(literal("stop").executes(context -> {
                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                        worldlessState.setEnabled(false);
                    }
                    return 1;
                }))
                .then(literal("pause").executes(context -> {
                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                        worldlessState.setPaused(true);
                    }
                    return 1;
                }))
                .then(literal("resume").executes(context -> {
                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                        worldlessState.setPaused(false);
                    }
                    return 1;
                }))
                .then(
                        literal("time")
                                .then(literal("add").then(argument("time", TimeArgumentType.time()).executes(context -> {
                                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                                        long time = context.getArgument("time", Integer.class);
                                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                                        worldlessState.modifyTicksUntilReset(time);

                                    }
                                    return 1;
                                }))
                                )
                                .then(literal("remove").then(argument("time", TimeArgumentType.time()).executes(context -> {
                                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                                        long time = context.getArgument("time", Integer.class);
                                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                                        worldlessState.modifyTicksUntilReset(-time);

                                    }
                                    return 1;
                                })))
                                .then(literal("set").then(argument("time", TimeArgumentType.time()).executes(context -> {
                                            if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                                                long time = context.getArgument("time", Integer.class);
                                                WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                                                worldlessState.setTicksUntilReset(time);

                                            }
                                            return 1;
                                        })))
                                .then(literal("setdefault").then(argument("time", TimeArgumentType.time()).executes(context -> {
                                    if (context.getSource().getServer() instanceof WorldlessStateHolder holder) {
                                        long time = context.getArgument("time", Integer.class);
                                        WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();
                                        worldlessState.setTicksPerWorld(time);

                                    }
                                    return 1;
                                })))
                )

        ));

    }
}

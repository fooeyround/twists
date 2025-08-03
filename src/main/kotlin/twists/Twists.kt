package twists

import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.ServerRegisterCommandEvent
import net.fabricmc.api.ModInitializer
import net.minecraft.commands.Commands
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import twists.command.WorldlessCommand
import twists.worldless.WorldlessMinecraftServerExtension

object Twists: ModInitializer {

    @JvmStatic
    val MOD_ID: String = "twists"
    @JvmStatic
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)


    override fun onInitialize() {


        GlobalEventHandler.Server.register<ServerRegisterCommandEvent> { event ->
            event.dispatcher.register(Commands.literal("twist")
                .then(WorldlessCommand.createWorldlessCommand())
            )
        }
        WorldlessMinecraftServerExtension.registerEvents()



/*        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<CommandSourceStack?>?, commandBuildContext: CommandBuildContext?, commandSelection: CommandSelection? ->
            WorldlessCommand.register(
                dispatcher,
                commandBuildContext,
                commandSelection
            )
        })
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server: MinecraftServer? ->
            Worldless.tick(
                server
            )
        })*/


        LOGGER.info("$MOD_ID loaded!")
    }
}
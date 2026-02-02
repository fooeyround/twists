package twists

import net.casual.arcade.commands.requiresPermission
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.ServerRegisterCommandEvent
import net.casual.arcade.minigame.utils.MinigameRegistries
import net.casual.arcade.scheduler.task.utils.TaskRegistries
import net.casual.arcade.utils.serialization.codec.CodecProvider.Companion.register
import net.fabricmc.api.ModInitializer
import net.minecraft.commands.Commands
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.permissions.PermissionLevel
import net.minecraft.world.item.enchantment.Enchantment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import twists.command.WorldlessCommand
import twists.extension.PlayerFallWithoutDamageExtension
import twists.item.TrackingCompassItem
import twists.minigame.worldless.WorldlessMinigameFactory
import twists.task.WorldlessBossbarTask
import twists.util.ItemUtil
import twists.util.TwistsUtils
import twists.util.twists
import twists.worldless.WorldlessMinecraftServerExtension

object Twists: ModInitializer {

    @JvmField
    val SOULBOUND_ENCHANTMENT: ResourceKey<Enchantment> = ResourceKey.create(
        Registries.ENCHANTMENT,
        twists("soulbound")
    )


    @JvmStatic
    val LOGGER: Logger = LoggerFactory.getLogger(TwistsUtils.MOD_ID)


    override fun onInitialize() {


        ItemUtil.registerItem("tracking_compass", ::TrackingCompassItem)


        //TODO: generify "twists" separate from minigame (worldless, manhunt, ...)
        GlobalEventHandler.Server.register<ServerRegisterCommandEvent> { event ->
            event.dispatcher.register(Commands.literal("twist")
                .requiresPermission(PermissionLevel.MODERATORS)
                .then(WorldlessCommand.createWorldlessCommand())
            )
        }
        WorldlessMinecraftServerExtension.registerEvents()
        PlayerFallWithoutDamageExtension.registerEvents()

        WorldlessMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        Registry.register(TaskRegistries.TASK_FACTORY, WorldlessBossbarTask.id, WorldlessBossbarTask)


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


        LOGGER.info("${TwistsUtils.MOD_ID} loaded!")
    }
}
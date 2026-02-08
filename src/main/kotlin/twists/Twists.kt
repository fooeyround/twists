package twists

import net.casual.arcade.commands.register
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.ServerRegisterCommandEvent
import net.casual.arcade.events.server.ServerStartEvent
import net.casual.arcade.events.server.player.PlayerJoinEvent
import net.casual.arcade.minigame.data.MinigameDataModule.Provider.Companion.register
import net.casual.arcade.minigame.events.MinigameCloseEvent
import net.casual.arcade.minigame.serialization.MinigameCreationContext
import net.casual.arcade.minigame.utils.MinigameRegistries
import net.casual.arcade.minigame.utils.MinigameUtils.getMinigame
import net.casual.arcade.scheduler.task.utils.TaskRegistries
import net.casual.arcade.utils.PlayerUtils.players
import net.casual.arcade.utils.serialization.codec.CodecProvider.Companion.register
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import twists.extension.PlayerFallWithoutDamageExtension
import twists.item.TrackingCompassItem
import twists.minigame.lobby.LobbyData
import twists.minigame.lobby.LobbyMinigame
import twists.minigame.manhunt.ManhuntMinigameFactory
import twists.minigame.manhunt.TeamCommandModifier
import twists.minigame.worldless.WorldlessMinigameFactory
import twists.task.WorldlessBossbarTask
import twists.util.ItemUtil
import twists.util.TwistsUtils
import twists.util.twists
import twists.worldless.WorldlessMinecraftServerExtension

object Twists: ModInitializer {

    lateinit var lobby: LobbyMinigame

    @JvmField
    val SOULBOUND_ENCHANTMENT: ResourceKey<Enchantment> = ResourceKey.create(
        Registries.ENCHANTMENT,
        twists("soulbound")
    )
    val TRACKING_COMPASS =  ItemUtil.registerItem("tracking_compass", ::TrackingCompassItem)

    override fun onInitialize() {

        LobbyData.register(MinigameRegistries.MINIGAME_DATA_MODULE_PROVIDER)


        //TODO: generify "twists" separate from minigame (worldless, manhunt, ...)
//        GlobalEventHandler.Server.register<ServerRegisterCommandEvent> { event ->
//            event.dispatcher.register(Commands.literal("twist")
//                .requiresPermission(PermissionLevel.MODERATORS)
//                .then(WorldlessCommand.createWorldlessCommand())
//            )
//        }
        WorldlessMinecraftServerExtension.registerEvents()
        PlayerFallWithoutDamageExtension.registerEvents()

        WorldlessMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        ManhuntMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        Registry.register(TaskRegistries.TASK_FACTORY, WorldlessBossbarTask.id, WorldlessBossbarTask)

        GlobalEventHandler.Server.register<ServerStartEvent> {
            this.lobby = LobbyMinigame.create("default", MinigameCreationContext(it.server))
            this.lobby.start()
        }
        GlobalEventHandler.Server.register<PlayerJoinEvent> {
            if (it.player.getMinigame() == null) {
                this.lobby.players.add(it.player)
            }
        }
        GlobalEventHandler.Server.register<MinigameCloseEvent> {
            if (it.minigame.uuid == this.lobby.uuid) {
                this.lobby = LobbyMinigame.create("default", MinigameCreationContext(it.minigame.server))
            }
            it.minigame.server.players.forEach { player ->
                this.lobby.players.add(player)
            }
        }

        GlobalEventHandler.Server.register<ServerRegisterCommandEvent> { event ->
            event.register(TeamCommandModifier)
        }

        TwistsUtils.logger.info("${TwistsUtils.MOD_ID} loaded!")
    }

}
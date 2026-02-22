package twists

import net.casual.arcade.commands.register
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.ServerRegisterCommandEvent
import net.casual.arcade.events.server.ServerStartEvent
import net.casual.arcade.minigame.data.MinigameDataModule.Provider.Companion.register
import net.casual.arcade.minigame.utils.MinigameRegistries
import net.casual.arcade.scheduler.task.utils.TaskRegistries
import net.casual.arcade.utils.serialization.codec.CodecProvider.Companion.register
import net.fabricmc.api.DedicatedServerModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.enchantment.Enchantment
import twists.command.TwistsCommand
import twists.extension.PlayerFallWithoutDamageExtension
import twists.item.TrackingCompassItem
import twists.minigame.TwistsMinigameManager
import twists.minigame.deathswap.DeathSwapMinigameFactory
import twists.minigame.lobby.LobbyData
import twists.minigame.manhunt.ManhuntMinigameFactory
import twists.minigame.manhunt.TeamCommandModifier
import twists.minigame.worldless.WorldlessMinigameFactory
import twists.stats.TwistsStats
import twists.task.SwappingBossbarTask
import twists.util.ItemUtil
import twists.util.TwistsUtils
import twists.util.twists

object Twists: DedicatedServerModInitializer {

    val minigames = TwistsMinigameManager(TwistsUtils.resolve("event"))

    @JvmField
    val SOULBOUND_ENCHANTMENT: ResourceKey<Enchantment> = ResourceKey.create(
        Registries.ENCHANTMENT,
        twists("soulbound")
    )
    val TRACKING_COMPASS =  ItemUtil.registerItem("tracking_compass", ::TrackingCompassItem)

    fun reload(server: MinecraftServer) {
        this.minigames.reload(server)
    }

    override fun onInitializeServer() {

        TwistsStats.load()

        LobbyData.register(MinigameRegistries.MINIGAME_DATA_MODULE_PROVIDER)

        PlayerFallWithoutDamageExtension.registerEvents()

        WorldlessMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        ManhuntMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        DeathSwapMinigameFactory.register(MinigameRegistries.MINIGAME_FACTORY)
        Registry.register(TaskRegistries.TASK_FACTORY, SwappingBossbarTask.id, SwappingBossbarTask)


        this.minigames.registerEvents(GlobalEventHandler.Server)

        GlobalEventHandler.Server.register<ServerStartEvent>(priority = 10_000) {
            this.minigames.load(it.server)
        }

        GlobalEventHandler.Server.register<ServerRegisterCommandEvent> { event ->
            event.register(TeamCommandModifier, TwistsCommand)
        }

        TwistsUtils.logger.info("${TwistsUtils.MOD_ID} loaded!")
    }

}
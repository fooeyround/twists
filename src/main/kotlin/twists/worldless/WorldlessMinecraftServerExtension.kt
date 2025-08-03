package twists.worldless

import net.casual.arcade.dimensions.level.CustomLevel
import net.casual.arcade.dimensions.level.LevelGenerationOptions
import net.casual.arcade.dimensions.level.LevelPersistence
import net.casual.arcade.dimensions.level.LevelProperties
import net.casual.arcade.dimensions.level.vanilla.VanillaDimension
import net.casual.arcade.dimensions.level.vanilla.VanillaDimensionMapper
import net.casual.arcade.dimensions.level.vanilla.VanillaLikeCustomLevelFactory
import net.casual.arcade.dimensions.utils.addCustomLevel
import net.casual.arcade.dimensions.utils.removeCustomLevel
import net.casual.arcade.dimensions.utils.setSpoofedDimension
import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.events.server.ServerTickEvent
import net.casual.arcade.events.server.level.LevelTickEvent
import net.casual.arcade.extensions.Extension
import net.casual.arcade.scheduler.GlobalTickedScheduler
import net.casual.arcade.utils.TimeUtils.Seconds
import net.casual.arcade.utils.TimeUtils.Ticks
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.Ticket
import net.minecraft.server.level.TicketType
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Relative
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.WorldOptions
import org.apache.commons.lang3.mutable.MutableLong
import twists.event.MinecraftServerExtensionEvent
import twists.event.MinecraftServerExtensionEvent.Companion.getExtension
import twists.util.WorldlessUtil.findTopNearestSafeSpawn

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.max

class WorldlessMinecraftServerExtension(
    var server: MinecraftServer
): Extension {

    var enabled  = false
    var paused = false
    private var ticksPerWorld: Long = 6000
    private var ticksUntilReset: Long = 0

    var overworld: CustomLevel? = null
    var nether: CustomLevel? = null
    var end: CustomLevel? = null


    fun isEnabled(): Boolean {
        return this.enabled && !paused
    }

    fun setTicksPerWorld(value: Long) {
        this.ticksPerWorld = max(0, value)
    }

    fun modifyTicksUntilReset(diff: Long) {
        this.ticksUntilReset = max(0, this.ticksUntilReset + diff)
    }

    fun setTicksUntilReset(value: Long) {
        this.ticksUntilReset = max(0, value)
    }

    //Returns if the world should reset
    fun tick(): Boolean {
        if (this.enabled && --this.ticksUntilReset <= 0) {
            ticksUntilReset = ticksPerWorld
            return true
        }
        return false
    }

    companion object {

        private val TIME_FORMAT: NumberFormat = DecimalFormat("00")

        val MinecraftServer.worldlessState get() = this.getExtension<WorldlessMinecraftServerExtension>()

        internal fun registerEvents() {
            GlobalEventHandler.Server.register<MinecraftServerExtensionEvent> {
                it.addExtension(::WorldlessMinecraftServerExtension)
            }
            GlobalEventHandler.Server.register<ServerTickEvent>(phase = "post") { (server) ->

                if (!server.tickRateManager().runsNormally() || !server.worldlessState.enabled) return@register


                val shouldReset = server.worldlessState.tick();
                val minutes = (server.worldlessState.ticksUntilReset / 20) / 60;
                val seconds = (server.worldlessState.ticksUntilReset / 20) % 60;
                for (player in  server.playerList.players) {
                    player.sendSystemMessage(Component.literal(TIME_FORMAT.format(minutes)+":"+TIME_FORMAT.format(seconds)), true);
                }

                if (shouldReset) {
                    val newSeed = WorldOptions.randomSeed()

                    val overworldKey = ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.fromNamespaceAndPath("worldless", "overworld_$newSeed")
                    )
                    val netherKey = ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.fromNamespaceAndPath("worldless", "nether_$newSeed")
                    )
                    val endKey = ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.fromNamespaceAndPath("worldless", "end_$newSeed")
                    )

                    val dimensionMapper = VanillaDimensionMapper(mapOf(VanillaDimension.Overworld to overworldKey, VanillaDimension.Nether to netherKey, VanillaDimension.End to endKey))

                    val newLevelProperties = LevelProperties(Optional.of(MutableLong(1200)), Optional.of(LevelProperties.WeatherProperties()))

                    val newOverworld = VanillaLikeCustomLevelFactory.constructor(VanillaDimension.Overworld, dimensionMapper).construct(
                        newLevelProperties,
                        LevelGenerationOptions(
                            stem = server.registryAccess().lookup(Registries.LEVEL_STEM).flatMap { it.get(
                                LevelStem.OVERWORLD) }.orElse(null),
                            seed = newSeed,
                            flat = false,
                            tickTime = true,
                            generateStructures = true,
                            debug = false,
                            customSpawners = VanillaDimension.Overworld.getCustomSpawners()
                        ), LevelPersistence.Temporary
                    ).create(server, overworldKey)

                    val newNether = VanillaLikeCustomLevelFactory.constructor(VanillaDimension.Nether, dimensionMapper).construct(
                        newLevelProperties,
                        LevelGenerationOptions(
                            stem = server.registryAccess().lookup(Registries.LEVEL_STEM).flatMap { it.get(
                                LevelStem.NETHER) }.orElse(null),
                            seed = newSeed,
                            flat = false,
                            tickTime = true,
                            generateStructures = true,
                            debug = false,
                            customSpawners = VanillaDimension.Nether.getCustomSpawners()
                        ), LevelPersistence.Temporary
                    ).create(server, netherKey)

                    val newEnd = VanillaLikeCustomLevelFactory.constructor(VanillaDimension.End, dimensionMapper).construct(
                        newLevelProperties,
                        LevelGenerationOptions(
                            stem = server.registryAccess().lookup(Registries.LEVEL_STEM).flatMap { it.get(
                                LevelStem.END) }.orElse(null),
                            seed = newSeed,
                            flat = false,
                            tickTime = true,
                            generateStructures = true,
                            debug = false,
                            customSpawners = VanillaDimension.End.getCustomSpawners()
                        ), LevelPersistence.Temporary
                    ).create(server, endKey)

                    newOverworld.setSpoofedDimension(Level.OVERWORLD)
                    newNether.setSpoofedDimension(Level.NETHER)
                    newEnd.setSpoofedDimension(Level.END)

                    server.addCustomLevel(newOverworld)
                    server.addCustomLevel(newNether)
                    server.addCustomLevel(newEnd)

                    newOverworld.chunkSource.addTicket(Ticket(TicketType.PLAYER_LOADING,31), ChunkPos.ZERO);
                    newOverworld.tick { false }


                    var giveSlowFalling = true;
                    //TODO: this really should not break... but I need a better fix than the player being randomly spawned in the air (or in a tall mountain) even with the slow fall
                    val initPos = BlockPos.ZERO.findTopNearestSafeSpawn(newOverworld, 150,100,150) ?: {
                        giveSlowFalling = true
                        BlockPos(0,180,0)
                    }()



                    for (player in server.playerList.players) {
                        player.teleportTo(newOverworld, initPos.x+0.5,
                            initPos.y + 0.0, initPos.z + 0.5, Relative.ROTATION, 0.0F, 0.0F, false)
                        if (giveSlowFalling) {
                            player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false))
                        }
                    }


                    val overworld = server.worldlessState.overworld
                    val nether = server.worldlessState.nether
                    val end = server.worldlessState.end

                    if (overworld != null) server.removeCustomLevel(overworld)
                    if (nether != null) server.removeCustomLevel(nether)
                    if (end != null) server.removeCustomLevel(end)


                    server.worldlessState.overworld = newOverworld
                    server.worldlessState.nether = newNether
                    server.worldlessState.end = newEnd


                }


            }
        }
    }
}



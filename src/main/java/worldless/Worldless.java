package worldless;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.joml.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Worldless implements ModInitializer {
	public static final String MOD_ID = "worldless";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final NumberFormat TIME_FORMAT = new DecimalFormat("00");


	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register(WorldlessCommand::register);

		ServerTickEvents.END_SERVER_TICK.register(Worldless::tick);


		LOGGER.info(MOD_ID + " loaded!");
	}

	public static void tick(MinecraftServer server) {

		if (server instanceof WorldlessStateHolder holder) {
			WorldlessState worldlessState = holder.worldless$getWorldlessState();

			if (worldlessState.isEnabled()) {
				boolean shouldReset = worldlessState.tick();
				long minutes = (worldlessState.getTicksUntilReset() / 20) / 60;
				long seconds = (worldlessState.getTicksUntilReset() / 20) % 60;
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					player.sendMessage(Text.literal(TIME_FORMAT.format(minutes)+":"+TIME_FORMAT.format(seconds)), true);
				}

				if (shouldReset) {
					Fantasy fantasy = Fantasy.get(server);

					long newSeed = Random.newSeed();
					RuntimeWorldConfig overworldConfig = new RuntimeWorldConfig()
							.setDimensionType(DimensionTypes.OVERWORLD)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getWorld(World.OVERWORLD).getChunkManager().getChunkGenerator())
							.setSeed(newSeed);
					RuntimeWorldConfig netherConfig = new RuntimeWorldConfig()
							.setDimensionType(DimensionTypes.THE_NETHER)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getWorld(World.NETHER).getChunkManager().getChunkGenerator())
							.setSeed(newSeed);
					RuntimeWorldConfig endWorldConfig = new RuntimeWorldConfig()
							.setDimensionType(DimensionTypes.THE_END)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getWorld(World.END).getChunkManager().getChunkGenerator())
							.setSeed(newSeed);


					RuntimeWorldHandle overworldHandle = fantasy.openTemporaryWorld(overworldConfig);
					RuntimeWorldHandle netherHandle = fantasy.openTemporaryWorld(netherConfig);
					RuntimeWorldHandle endHandle = fantasy.openTemporaryWorld(endWorldConfig);

					/// Dragon fight has hardcoded checks for the *real* end.
					endHandle.asWorld().setEnderDragonFight(new EnderDragonFight(endHandle.asWorld(), endHandle.asWorld().getSeed(), EnderDragonFight.Data.DEFAULT));
					if (worldlessState.endHandle != null) worldlessState.endHandle.asWorld().setEnderDragonFight(null);

					overworldHandle.asWorld().getChunkManager().addTicket(new ChunkTicket(ChunkTicketType.PLAYER_LOADING,31), ChunkPos.ORIGIN);


					for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//						BlockPos initPos = overworldHandle.asWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(0, 100, 0));
						BlockPos initPos = new BlockPos(0, 100, 0);
						player.teleport(overworldHandle.asWorld(), initPos.getX(), initPos.getY(), initPos.getZ(), PositionFlag.DELTA, 0.0F, 0.0F, false);
						player.setSpawnPoint(new ServerPlayerEntity.Respawn(overworldHandle.getRegistryKey(), initPos, 0.0F, true), false);

						//Bossbars seem to be persistent?
//						player.networkHandler.send(BossBarS2CPacket.remove(endHandle.asWorld()));

					}

					if (worldlessState.overworldHandle != null) worldlessState.overworldHandle.delete();
					if (worldlessState.netherHandle != null) worldlessState.netherHandle.delete();
					if (worldlessState.endHandle != null) worldlessState.endHandle.delete();
					worldlessState.overworldHandle = overworldHandle;
					worldlessState.netherHandle = netherHandle;
					worldlessState.endHandle = endHandle;


				}
			}
		}
	}
}
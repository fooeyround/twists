package twists.worldless;

/*
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;


import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.WorldOptions;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;*/

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Worldless {

/*	public static void tick(MinecraftServer server) {

		if (server instanceof WorldlessStateHolder holder) {
			WorldlessState worldlessState = holder.twists$worldless$getWorldlessState();

			if (worldlessState.isEnabled()) {
				boolean shouldReset = worldlessState.tick();
				long minutes = (worldlessState.getTicksUntilReset() / 20) / 60;
				long seconds = (worldlessState.getTicksUntilReset() / 20) % 60;
				for (ServerPlayer player : server.getPlayerList().getPlayers()) {
					player.sendSystemMessage(Component.literal(TIME_FORMAT.format(minutes)+":"+TIME_FORMAT.format(seconds)), true);
				}

				if (shouldReset) {
					Fantasy fantasy = Fantasy.get(server);

					long newSeed = WorldOptions.randomSeed();
					RuntimeWorldConfig overworldConfig = new RuntimeWorldConfig()
							.setDimensionType(BuiltinDimensionTypes.OVERWORLD)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getLevel(Level.OVERWORLD).getChunkSource().getGenerator())
							.setSeed(newSeed);
					RuntimeWorldConfig netherConfig = new RuntimeWorldConfig()
							.setDimensionType(BuiltinDimensionTypes.NETHER)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getLevel(Level.NETHER).getChunkSource().getGenerator())
							.setSeed(newSeed);
					RuntimeWorldConfig endWorldConfig = new RuntimeWorldConfig()
							.setDimensionType(BuiltinDimensionTypes.END)
							.setMirrorOverworldDifficulty(true)
							.setMirrorOverworldGameRules(true)
							.setShouldTickTime(true)
							.setGenerator(server.getLevel(Level.END).getChunkSource().getGenerator())
							.setSeed(newSeed);


					RuntimeWorldHandle overworldHandle = fantasy.openTemporaryWorld(overworldConfig);
					RuntimeWorldHandle netherHandle = fantasy.openTemporaryWorld(netherConfig);
					RuntimeWorldHandle endHandle = fantasy.openTemporaryWorld(endWorldConfig);


					overworldHandle.asWorld().getChunkSource().addTicket(new Ticket(TicketType.PLAYER_LOADING,31), ChunkPos.ZERO);

					overworldHandle.asWorld().tick(() -> (false));


					BlockPos initPos = WorldlessUtil.getSafeSpawnNearPos(overworldHandle.asWorld(), BlockPos.ZERO);


					for (ServerPlayer player : server.getPlayerList().getPlayers()) {
						player.teleportTo(overworldHandle.asWorld(), initPos.getX()+0.5, initPos.getY(), initPos.getZ()+0.5, Relative.ROTATION, 0.0F, 0.0F, false);
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
	}*/
}
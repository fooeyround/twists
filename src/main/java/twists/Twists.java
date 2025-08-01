package twists;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twists.worldless.Worldless;
import twists.worldless.WorldlessCommand;

public class Twists implements ModInitializer {

    public static final String MOD_ID = "twists";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register(WorldlessCommand::register);
        ServerTickEvents.END_SERVER_TICK.register(Worldless::tick);


        Twists.LOGGER.info(Twists.MOD_ID + " loaded!");
    }
}

package terreal;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Terreal implements ModInitializer {
	public static final String MOD_ID = "terreal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final WorldInfoStorage worldInfoStorage = new WorldInfoStorage();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Terreal...");

		Path configDir = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve(MOD_ID);
		configDir.toFile().mkdirs();
		LOGGER.info("Created config folder at {}", configDir);

		ServerWorldEvents.LOAD.register((server, world) -> {
			if (worldInfoStorage.getServerWorld() != null && worldInfoStorage.getServerWorld().getServer().getOverworld() == server.getOverworld())
				return; // Already initialized for this world (prevents this going off for three times. Idk why it does that lol)

			LOGGER.info("Intervening in world load: {}", world.getRegistryKey().getRegistry());
			LOGGER.info("Saving current ServerWorld and seed...");
			worldInfoStorage.setCurrent(server.getOverworld());

			LOGGER.info("Using viable config file...");
			try {
				worldInfoStorage.searchAndInitConfig();
			} catch (IOException e) {
				LOGGER.error("Failed to initialize Terreal config for world {}: {}", world.getRegistryKey().getValue(), e.getMessage());
				LOGGER.error("This does not prevent Terreal from working, however, if the config file is changed, it may cause issues with world generation!");
				LOGGER.error("Please check the world directory for sufficient permissions! The world being a ZIP may also cause this issue!");
			}
		});

		LOGGER.info("Terreal initialized successfully!");
	}
}
package terreal;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Terreal implements ModInitializer {
	public static final String MOD_ID = "terreal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Terreal...");

		LOGGER.info("Terreal initialized successfully!");
	}
}
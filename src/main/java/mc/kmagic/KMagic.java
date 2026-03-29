package mc.kmagic;

import mc.kmagic.item.ModItemGroups;
import mc.kmagic.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMagic implements ModInitializer {
	public static final String MOD_ID = "kmagic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
	}
}
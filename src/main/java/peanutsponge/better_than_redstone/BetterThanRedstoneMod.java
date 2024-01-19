package peanutsponge.better_than_redstone;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.util.ConfigHandler;

import java.util.Properties;


public class BetterThanRedstoneMod implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "better_than_redstone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigHandler config;
	static {
		Properties prop = new Properties();
		prop.setProperty("starting_block_id","3000");
		prop.setProperty("starting_item_id","19000");
		config = new ConfigHandler(MOD_ID,prop);
		config.updateConfig();
	}
	public static Block blockRedstoneConductor;
	public static BlockBuilder signalBlockBuilder = new BlockBuilder(MOD_ID)
		.setLightOpacity(0)
		.setHardness(1.5f)
		.setVisualUpdateOnMetadata()
		.setTags(BlockTags.MINEABLE_BY_PICKAXE);
    @Override
    public void onInitialize() {
        LOGGER.info("Better than redstone initialized.");

	}


	@Override
	public void beforeGameStart() {
		int blockNum = config.getInt("starting_block_id");
		blockRedstoneConductor = signalBlockBuilder
			.build(new BlockSignalReceiver("signal_conductor", blockNum++));
		blockRedstoneConductor = signalBlockBuilder
			.build(new BlockSignalReceiver("signal_display", blockNum++));
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}
}

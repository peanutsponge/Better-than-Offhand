package peanutsponge.better_than_redstone;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.core.block.Block;
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

	public static BlockBuilder signalBlockBuilder = new BlockBuilder(MOD_ID)
		.setLightOpacity(0)
		.setHardness(1.5f)
		.setVisualUpdateOnMetadata()
		.setTags(BlockTags.MINEABLE_BY_PICKAXE);

	public static Block blockSignalConductor;
	public static Block blockSignalDisplay;
	public static Block blockSignalInverter;
	public static Block blockSignalRelay;
	public static Block blockSignalExtender;
	public static Block blockSignalAnalogInput;
	public static Block blockSignalToggle;
	public static Block blockSignalPulse;
    @Override
    public void onInitialize() {
        LOGGER.info("Better than redstone initialized.");

	}


	@Override
	public void beforeGameStart() {
		int blockNum = config.getInt("starting_block_id");
		blockSignalConductor = signalBlockBuilder
			.build(new BlockSignalConductor("signal_conductor", blockNum++));
		blockSignalDisplay = signalBlockBuilder
			.setBlockModel(new BlockModelRenderBlocks(36))
			.build(new BlockSignalDisplay("signal_display", blockNum++));
		blockSignalInverter = signalBlockBuilder
			.setBlockModel(new BlockModelRenderBlocks(36))
			.build(new BlockSignalInverter("signal_inverter", blockNum++));
		blockSignalRelay = signalBlockBuilder
			.setBlockModel(new BlockModelRenderBlocks(36))
			.build(new BlockSignalRelay("signal_relay", blockNum++));
		blockSignalExtender = signalBlockBuilder
			.setBlockModel(new BlockModelRenderBlocks(36))
			.build(new BlockSignalExtender("signal_extender", blockNum++));
		blockSignalAnalogInput = signalBlockBuilder
			.setBlockModel(new BlockModelRenderBlocks(36))
			.build(new BlockSignalAnalogInput("signal_analog_input", blockNum++));
		blockSignalToggle = signalBlockBuilder
			.build(new BlockSignalToggle("signal_toggle", blockNum++));
		blockSignalPulse = signalBlockBuilder
			.build(new BlockSignalPulse("signal_pulse", blockNum++));
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}
}

package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static peanutsponge.better_than_redstone.Signal.hasCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalInverter extends BlockDirectional {
	public int[] atlasIndicesOutput = new int[16];
	public BlockSignalInverter(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			this.atlasIndicesOutput[i] = getOrCreateBlockTextureIndex(MOD_ID, "signal_conductor" + " ("+ i +").png");
		}
	}
	@Override
	public int getFaceTexture(int data) {
		return this.atlasIndicesOutput[getSignalCode(data)];
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		int signalCode = getSignalCode(data);
		int directionCode = getDirectionCode(data);
		int signalGot = getMaxCurrent(world, x, y, z);
		if (signalCode != signalGot) {
			LOGGER.info("\nsignal: " + signalCode + "\nmaxSignal: " + signalGot+ "\nmeta made: "+makeMetaData(directionCode, signalGot));
			world.setBlockMetadataWithNotify(x, y, z, makeMetaData(directionCode, signalGot));
			LOGGER.info("\nupdated signal: " + getSignalCode(world.getBlockMetadata(x, y, z)) + "\nmaxSignal: " + getMaxCurrent(world, x, y, z)+"\nmeta set: "+world.getBlockMetadata(x, y, z));
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x,  y,  z, side, entity, sideHeight);
		world.scheduleBlockUpdate(x, y, z, this.id, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		world.scheduleBlockUpdate(x, y, z, this.id, 1);
	}

	/**
	 * Combines the given direction code and signal to create a single integer of data.
	 *
	 * @param directionCode The direction code to be combined.
	 * @param signalCode        The signal to be combined.
	 * @return The result of combining direction code and signal.
	 */
	public static int makeMetaData(int directionCode, int signalCode) {
		directionCode &= 0x0F;
		signalCode &= 0x0F;
		return directionCode | (signalCode<<4);
	}

	/**
	 * Extracts the signal from the given data.
	 *
	 * @param data The input data containing both direction code and signal.
	 * @return The extracted signal.
	 */
	public static int getSignalCode(int data) {
		return (data>>4) & 0x0F;
	}
}

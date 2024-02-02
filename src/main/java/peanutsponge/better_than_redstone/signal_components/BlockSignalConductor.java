package peanutsponge.better_than_redstone.signal_components;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static peanutsponge.better_than_redstone.Signal.spawnParticles;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;


public class BlockSignalConductor extends Block {
	public int[] atlasIndices = new int[16];
	public BlockSignalConductor(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			this.atlasIndices[i] = getOrCreateBlockTextureIndex(MOD_ID, key + " ("+ i +").png");
			LOGGER.info("[" + i + "]:" + atlasIndices[i] + "["+ key +" ("+ i +").png]");
		}
	}
	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		return this.atlasIndices[data % 16];
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.updateCurrent(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.updateCurrent(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}
	/**
	 * Calculates the current it should have, updates its metadata accordingly and notifies neighbors
	 */
	private void updateCurrent(World world, int x, int y, int z) {
		int oldCurrent = world.getBlockMetadata(x, y, z);
		int newCurrent = getMaxCurrent(world,x,y,z);
		if (newCurrent != oldCurrent){
			world.setBlockMetadataWithNotify(x, y, z, newCurrent);
		}
	}


	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int current = world.getBlockMetadata(x, y, z);
		int random = rand.nextInt(16);
		if (current > random){
			spawnParticles(world, x, y, z);
		}
	}
}

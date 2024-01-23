package peanutsponge.better_than_redstone;

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
		this.propagateCurrent(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.propagateCurrent(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}
	/**
	 * Calculates the current it should have, updates its metadata accordingly and notifies neighbors
	 */
	private void propagateCurrent(World world, int x, int y, int z) {
		int oldCurrent = world.getBlockMetadata(x, y, z);
		int newCurrent = getMaxCurrent(world,x,y,z);
		if (newCurrent > 0 & !world.isBlockGettingPowered(x,y,z)){
				newCurrent--;
			}
		if (newCurrent != oldCurrent){
			world.setBlockMetadata(x, y, z, newCurrent);
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
//		LOGGER.info("Done PropagateCurrent (" + x +","+ y+"," + z+")\n old & new -> out:" + oldCurrent + " & " + newCurrent + " -> " + world.getBlockMetadata(x, y, z));
	}


	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int l0 = world.getBlockMetadata(x, y, z);
		if (l0>0){ //Should roll random number and compare against current
			spawnParticles(world, x, y, z);
		}
	}


}

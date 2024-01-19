package peanutsponge.better_than_redstone;

import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;


public class BlockSignalReceiver extends Block {
	public static int[] atlasIndices = new int[16];
	public BlockSignalReceiver(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			atlasIndices[i] = getOrCreateBlockTextureIndex(MOD_ID, key + " ("+ i +").png");
			LOGGER.info("[" + i + "]:" + atlasIndices[i] + "["+ key +" ("+ i +").png]");
		}
	}
	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		return atlasIndices[data % 16];
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.updateAndPropagateCurrentStrength(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.updateAndPropagateCurrentStrength(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}

	private int getMaxCurrentStrength(World world, int x, int y, int z, int l) {
		int blockId = world.getBlockId(x, y, z);
		int data = world.getBlockMetadata(x, y, z);
		LOGGER.info("getMaxCurrentStrength: (" + x +","+ y+"," + z+") " + "[" + blockId +";"+data+"]" + l + ": " + blockRedstoneConductor.id);
		if (blockId == blockRedstoneConductor.id - 1) {
			LOGGER.info("TRIGGERED blockRedstoneConductor");
			int l2 = data%16;
			return Math.max(l2, l);
		} else {
			return l;
		}
	}

	private void updateAndPropagateCurrentStrength(World world, int x, int y, int z) {

		int l0 = world.getBlockMetadata(x, y, z);
		LOGGER.info("Start PropagateCurrent (" + x +","+ y+"," + z+") " + l0);
		int l1;
		if (world.isBlockGettingPowered(x,y,z)){
			l1 = 15;
		} else {
			l1 = this.getMaxCurrentStrength(world, x+1, y, z, 0);
			l1 = this.getMaxCurrentStrength(world, x-1, y, z, l1);
			l1 = this.getMaxCurrentStrength(world, x, y+1, z, l1);
			l1 = this.getMaxCurrentStrength(world, x, y-1, z, l1);
			l1 = this.getMaxCurrentStrength(world, x, y, z+1, l1);
			l1 = this.getMaxCurrentStrength(world, x, y, z-1, l1);
			if (this.id == blockRedstoneConductor.id - 1 & l1>0){
				l1--;
			}
		}
		if (l1 != l0){
			world.setBlockMetadata(x, y, z, l1);
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
		LOGGER.info("Done PropagateCurrent (" + x +","+ y+"," + z+") " + world.getBlockMetadata(x, y, z));
	}


	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int l0 = world.getBlockMetadata(x, y, z);
		if (l0>0){ //Should roll random number and compare
			this.spawnParticles(world, x, y, z);
		}

	}

	private void spawnParticles(World world, int x, int y, int z) {
		Random random = world.rand;
		Color color = Colors.allRedstoneColors[10];
		if (color != null) {
			float red = (float) color.getRed() / 255.0F;
			float green = (float) color.getGreen() / 255.0F;
			float blue = (float) color.getBlue() / 255.0F;
			double d = 0.0625;

			for (int i = 0; i < 3; ++i) {
				double px = (double) ((float) x + random.nextFloat());
				double py = (double) ((float) y + random.nextFloat());
				double pz = (double) ((float) z + random.nextFloat());
				if (i == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
					py = (double) (y + 1) + d;
				}

				if (i == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
					py = (double) (y + 0) - d;
				}

				if (i == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
					pz = (double) (z + 1) + d;
				}

				if (i == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
					pz = (double) (z + 0) - d;
				}

				if (i == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
					px = (double) (x + 1) + d;
				}

				if (i == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
					px = (double) (x + 0) - d;
				}

				if (px < (double) x || px > (double) (x + 1) || py < 0.0 || py > (double) (y + 1) || pz < (double) z || pz > (double) (z + 1)) {
					world.spawnParticle("reddust", px, py, pz, (double) red, (double) green, (double) blue);
				}
			}

		}
	}
}

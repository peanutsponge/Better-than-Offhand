package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;


public class RedstoneConductor extends Block {
	public RedstoneConductor(String key, int id, Material material) {
		super(key, id, material);
		this.setTicking(true);

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
		LOGGER.info("getMaxCurrentStrength (" + x +","+ y+"," + z+") " + l);
		int blockId = world.getBlockId(x, y, z);
		if (blockId == this.id) {
			int l2 = world.getBlockMetadata(x, y, z)%16;
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
			if (l1>0){
				l1--;
			}
		}
		if (l1 != l0){
			world.setBlockMetadata(x, y, z, l1);
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
		LOGGER.info("Done PropagateCurrent (" + x +","+ y+"," + z+") " + world.getBlockMetadata(x, y, z));
	}
}

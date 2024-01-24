package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.BlockDirectional.*;
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalDisplay extends Block {
	public int[] atlasIndices = new int[18];
	public BlockSignalDisplay(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			this.atlasIndices[i] = getOrCreateBlockTextureIndex(MOD_ID, key + " ("+ i +").png");
		}
		this.atlasIndices[16] = getOrCreateBlockTextureIndex(MOD_ID, key + "_side.png");
		this.atlasIndices[17] = getOrCreateBlockTextureIndex(MOD_ID, key + "_input.png");

	}

	public int getFaceTexture(int data) {
		return this.atlasIndices[getSignalCode(data)];
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		Direction placementDirection = getPlacementDirection(getDirectionCode(data));
		if (side.getId() == placementDirection.getId()) { // face texture
			return getFaceTexture(data);
		} else if (side.getId() == placementDirection.getOpposite().getId()) {
			return this.atlasIndices[17];
		} else return this.atlasIndices[16];
	}
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		Direction horizontalDirection = Direction.getHorizontalDirection(entity.yRot);
		LOGGER.info("On block placed: " + horizontalDirection +" , "+ horizontalDirection.getHorizontalIndex());
		world.setBlockMetadataWithNotify(x, y, z, makeDirectionAndSignalCode(makeDirectionCode(placementDirection, horizontalDirection), 0));
		this.propagateCurrent(world, x, y, z);
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
		int data = world.getBlockMetadata(x, y, z);
		int oldCurrent = getSignalCode(data);
		int newCurrent = getMaxCurrent(world,x,y,z);
		if (newCurrent != oldCurrent){
			world.setBlockMetadata(x, y, z, makeDirectionAndSignalCode(getDirectionCode(data), newCurrent));
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
	}
}

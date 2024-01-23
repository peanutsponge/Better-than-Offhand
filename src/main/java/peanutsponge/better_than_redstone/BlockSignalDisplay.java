package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.logic.PistonDirections;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static peanutsponge.better_than_redstone.Signal.hasCurrent;
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
	public static int getDirection(int data) {
		return ((byte) data) >> 4;
	}
	public static int getCurrent(int data) {
		return ((byte) data) & 0b00001111;
	}
	public static int combineDirectionAndSignal(int direction, int signal) {
		direction &= 0b00001111;
		signal &= 0b00001111;
		byte signalByte = (byte) signal;
		byte directionByte = (byte) direction;
		// shift directionByte 4 places left
		return signalByte | (directionByte << 4);
	}

	public int getFaceTexture(int data) {
		return this.atlasIndices[getCurrent(data)];
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		int direction = getDirection(data);
		if (direction > 5) {
			return texCoordToIndex(13, 6);
		} else if (side.getId() == direction) { // face texture
			return getFaceTexture(data);
		} else {
			return side.getId() != PistonDirections.directionMap[direction] ? this.atlasIndices[16] : this.atlasIndices[17];
		}
	}
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		world.setBlockMetadataWithNotify(x, y, z, combineDirectionAndSignal(placementDirection.getId(), 0));
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
		int oldCurrent = getCurrent(data);
		int newCurrent = getMaxCurrent(world,x,y,z);
		if (newCurrent != oldCurrent){
			world.setBlockMetadata(x, y, z, combineDirectionAndSignal(getDirection(data), newCurrent));
			world.notifyBlocksOfNeighborChange(x,y,z,this.id);
		}
	}
}

package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.io.File;

import static net.minecraft.core.util.helper.Direction.*;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockDirectional extends Block {
	public int[] atlasIndices = new int[4];
	public BlockDirectional(String key, int id, Material material) {
		super(key, id, material);

		this.atlasIndices[0] = getTextureIndexWithFallback(key + "_input.png", "default_input.png");
		this.atlasIndices[1] = getTextureIndexWithFallback(key + "_side.png", "default_side.png");
		this.atlasIndices[3] = getTextureIndexWithFallback(key + "_output.png", "default_output.png");
	}

	private int getTextureIndexWithFallback(String textureKey, String fallbackTextureKey) {
		// Check if the file exists before obtaining the texture index.
		if ((new File(textureKey)).exists()) {
			return getOrCreateBlockTextureIndex(MOD_ID, textureKey);
		} else {
			// If the file doesn't exist, use the fallback texture index.
			return getOrCreateBlockTextureIndex(MOD_ID, fallbackTextureKey);
		}
	}

	public int getOutputTexture(int data) {
		return this.atlasIndices[3];
	}
	public int getInputTexture(int data) {
		return this.atlasIndices[0];
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		Direction placementDirection = getPlacementDirection(getDirectionCode(data));
		if (side.getId() == placementDirection.getId()) { // face texture
			return getOutputTexture(data);
		} else if (side.getId() == placementDirection.getOpposite().getId()) {
			return getInputTexture(data);
		} else return this.atlasIndices[1]; //TODO differing side support
	}
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		Direction horizontalDirection = Direction.getHorizontalDirection(entity.yRot);
//		LOGGER.info("On block placed: " + horizontalDirection +" , "+ horizontalDirection.getHorizontalIndex());
		world.setBlockMetadataWithNotify(x, y, z, makeDirectionCode(placementDirection, horizontalDirection));
	}


	/**
	 * Extracts the direction code from the given data.
	 *
	 * @param data The input data containing both direction code and other stuff.
	 * @return The extracted direction code.
	 */
	public static int getDirectionCode(int data) {
		return data & 0x0F;
	}

	public static int makeDirectionCode(Direction placementDirection, Direction horizontalDirection) {
		switch (placementDirection) {
			case NORTH://
				return 0;
			case EAST://
				return 1;
			case SOUTH://
				return 2;
			case WEST://
				return 3;
			case UP://
				switch (horizontalDirection) {
					case NORTH://
						return 4;
					case EAST://
						return 5;
					case SOUTH://
						return 6;
					case WEST://
						return 7;
				}
			case DOWN://
				switch (horizontalDirection) {
					case NORTH://
						return 8;
					case EAST://
						return 9;
					case SOUTH://
						return 10;
					case WEST://
						return 11;
				}
			default:
				return 0;
		}
	}
	public static Direction getPlacementDirection(int directionCode) {
		switch (directionCode) {
			case 0:
				return NORTH;
			case 1:
				return EAST;
			case 2:
				return SOUTH;
			case 3:
				return WEST;
			case 4:
			case 5:
			case 6:
			case 7:
				return UP;
			case 8:
			case 9:
			case 10:
			case 11:
				return DOWN;
			default:
				return NORTH;
		}
	}
	public static Direction getHorizontalDirection(int directionCode) {
		switch (directionCode) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				return NORTH;
			case 5:
				return EAST;
			case 6:
				return SOUTH;
			case 7:
				return WEST;
			case 8:
				return NORTH;
			case 9:
				return EAST;
			case 10:
				return SOUTH;
			case 11:
				return WEST;
			default:
				return NORTH;
		}
	}
}

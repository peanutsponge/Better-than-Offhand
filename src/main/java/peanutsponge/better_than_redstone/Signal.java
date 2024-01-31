package peanutsponge.better_than_redstone;

import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;

import java.util.Arrays;
import java.util.Random;

import static net.minecraft.core.util.helper.Direction.getDirectionById;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static peanutsponge.better_than_redstone.Directions.getDirectionCode;
import static peanutsponge.better_than_redstone.Directions.getPlacementDirection;

public class Signal {
	/**
	 * Gets the current a block receives from a neighbor
	 */
	public static int getCurrent(World world, int x, int y, int z, Direction direction) {
		int blockIdTarget = world.getBlockId(x, y, z);
		System.out.println("getCurrent(world, "+ x + ", " + y +", " + z + ", " + direction + ")");
		switch (direction.getOpposite()) {
			case DOWN://
				y--;
				break;
			case UP:
				y++;
				break;
			case NORTH://
				z--;
				break;
			case SOUTH://
				z++;
				break;
			case WEST://
				x--;
				break;
			case EAST://
				x++;
				break;
		}
		int blockId = world.getBlockId(x, y, z);
		int data = world.getBlockMetadata(x, y, z);
		Direction blockDirection = getPlacementDirection(getDirectionCode(data));
		System.out.println("( " + x +","+ y+"," + z+") " + "[" + blockId +";"+data+"]");
		if (blockId == 0)
			return 0;
		else if (Block.blocksList[blockId].isPoweringTo(world, x, y , z, direction.getOpposite().getId()))
			return 15;
		else if (blockId == blockSignalConductor.id){
			if (blockIdTarget == blockSignalConductor.id)
				data--;
			return data%16;
		}

		else if (blockId == blockSignalRelay.id | blockId == blockSignalInverter.id | blockId == blockSignalAnalogInput.id){
			if (blockDirection == direction)
				return (getSignalCode(data)) % 16;
		}
        return 0;
    }
	/**
	 * Calculates the highest current a block receives
	 */
	public static int getMaxCurrent(World world, int x, int y, int z){
		int maxCurrent = 0;
		for (int i=0; i< 6 ; i++){
			maxCurrent = Math.max(getCurrent(world, x, y, z, getDirectionById(i)), maxCurrent);
		}
		return maxCurrent;
	}
	/**
	 * Calculates the current a block receives from the input side
	 */
	public static int getInputCurrent(World world, int x, int y, int z){
		int data = world.getBlockMetadata(x, y, z);
		Direction placementDirection = getPlacementDirection(getDirectionCode(data));
		return getCurrent(world, x, y, z, placementDirection);
	}
	/**
	 * Calculates the highest current a block receives from the sides (not input and not output)
	 */
	public static int getMaxSideCurrent(World world, int x, int y, int z){
		return Arrays.stream(getSideCurrents(world, x, y, z))
			.max()
			.getAsInt();
	}
	/**
	 * Calculates the sum of the currents a block receives from the sides (not input and not output)
	 */
	public static int getSumSideCurrent(World world, int x, int y, int z){
		return Arrays.stream(getSideCurrents(world, x, y, z))
			.sum();
	}
	/**
	 * Calculates all currents a block receives from the sides (not input and not output)
	 */
	public static int[] getSideCurrents(World world, int x, int y, int z){
		int[] sideCurrents = new int[4];
		int data = world.getBlockMetadata(x, y, z);
		Direction[] sideDirections = new Direction[4];
		Direction placementDirection = getPlacementDirection(getDirectionCode(data));
		switch (placementDirection) {
			case NORTH:
			case SOUTH:
				sideDirections[0] = Direction.WEST;
				sideDirections[1] = Direction.EAST;
				sideDirections[2] = Direction.UP;
				sideDirections[3] = Direction.DOWN;
				break;
			case WEST:
			case EAST:
				sideDirections[0] = Direction.NORTH;
				sideDirections[1] = Direction.SOUTH;
				sideDirections[2] = Direction.UP;
				sideDirections[3] = Direction.DOWN;
				break;
			case UP:
			case DOWN:
				sideDirections[0] = Direction.WEST;
				sideDirections[1] = Direction.EAST;
				sideDirections[2] = Direction.NORTH;
				sideDirections[3] = Direction.SOUTH;
				break;
		}
		for (int i=0; i< 4 ; i++){
			sideCurrents[i] = getCurrent(world, x, y, z, sideDirections[i]);
		}
		return sideCurrents;
	}



	/**
	 * Calculates if a block receives current
	 */
	public static boolean hasCurrent(World world, int x, int y, int z) {
		return getMaxCurrent(world, x, y, z) > 0;
	}
	/**
	 * Calculates if a block receives input current
	 */
	public static boolean hasInputCurrent(World world, int x, int y, int z) {
		return getInputCurrent(world, x, y, z) > 0;
	}
	/**
	 * Calculates if a block receives side current
	 */
	public static boolean hasSideCurrent(World world, int x, int y, int z) {
		return getMaxSideCurrent(world, x, y, z) > 0;
	}

	public static void spawnParticles(World world, int x, int y, int z) {
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

package peanutsponge.better_than_redstone;

import net.minecraft.core.util.helper.Direction;

import static net.minecraft.core.util.helper.Direction.*;

public class BlockDirectional {
	// Constants for bit masks
	private static final int DIRECTION_MASK = 0xF0;
	private static final int SIGNAL_MASK = 0x0F;

	/**
	 * Combines the given direction code and signal to create a single integer of data.
	 *
	 * @param directionCode The direction code to be combined.
	 * @param signal        The signal to be combined.
	 * @return The result of combining direction code and signal.
	 */
	public static int makeDirectionAndSignalCode(int directionCode, int signalCode) {
		directionCode &= SIGNAL_MASK;
		signalCode &= SIGNAL_MASK;
		return (directionCode << 4) | signalCode;
	}

	/**
	 * Extracts the signal from the given data.
	 *
	 * @param data The input data containing both direction code and signal.
	 * @return The extracted signal.
	 */
	public static int getSignalCode(int data) {
		return data & SIGNAL_MASK;
	}

	/**
	 * Extracts the direction code from the given data.
	 *
	 * @param data The input data containing both direction code and signal.
	 * @return The extracted direction code.
	 */
	public static int getDirectionCode(int data) {
		return (data & DIRECTION_MASK) >> 4;
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

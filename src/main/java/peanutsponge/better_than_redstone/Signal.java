package peanutsponge.better_than_redstone;

import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.blockSignalConductor;

public class Signal {
	/**
	 * Gets the current a block receives from a neighbor
	 */
	public static int getCurrent(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);
		int data = world.getBlockMetadata(x, y, z);
		LOGGER.info("getCurrentStrength: (" + x +","+ y+"," + z+") " + "[" + blockId +";"+data+"]");
		if (blockId == blockSignalConductor.id) {
			LOGGER.info("TRIGGERED blockRedstoneConductor");
            return data%16;
		}
        return 0;
    }
	/**
	 * Calculates the highest current a block receives
	 */
	public static int getMaxCurrent(World world, int x, int y, int z){
		int maxCurrent = 0;
		if (world.isBlockGettingPowered(x,y,z)){
			return 15;
		} else {
			maxCurrent = Math.max(getCurrent(world, x + 1, y, z), maxCurrent);
			maxCurrent = Math.max(getCurrent(world, x - 1, y, z), maxCurrent);
			maxCurrent = Math.max(getCurrent(world, x, y + 1, z), maxCurrent);
			maxCurrent = Math.max(getCurrent(world, x, y - 1, z), maxCurrent);
			maxCurrent = Math.max(getCurrent(world, x, y, z + 1), maxCurrent);
			maxCurrent = Math.max(getCurrent(world, x, y, z - 1), maxCurrent);
			return maxCurrent;
		}
	}
	/**
	 * Calculates if a block receives current
	 */
	public static boolean hasCurrent(World world, int x, int y, int z) {
		return getMaxCurrent(world, x, y, z) > 0;
	}
}

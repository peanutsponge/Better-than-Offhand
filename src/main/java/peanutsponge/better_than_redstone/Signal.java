package peanutsponge.better_than_redstone;

import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.world.World;

import java.util.Random;

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
}

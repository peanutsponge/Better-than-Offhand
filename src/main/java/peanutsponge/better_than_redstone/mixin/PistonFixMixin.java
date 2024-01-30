package peanutsponge.better_than_redstone.mixin;

import net.minecraft.core.block.piston.BlockPistonBase;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static peanutsponge.better_than_redstone.Signal.hasCurrent;

@Mixin(value = BlockPistonBase.class, remap = false)
public class PistonFixMixin {

	/**
	 * @author peanutsponge
	 * @reason fix due to indirect power disable mixin, and new redstone components
	 */
	@Overwrite
	private boolean getNeighborSignal(World world, int x, int y, int z, int direction) {
		return hasCurrent(world, x, y, z);
	}
}

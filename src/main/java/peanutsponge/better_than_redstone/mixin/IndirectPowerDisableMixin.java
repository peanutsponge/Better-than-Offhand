package peanutsponge.better_than_redstone.mixin;

import net.minecraft.core.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(value = World.class, remap = false)
public class IndirectPowerDisableMixin {
	World thisAs = (World) (Object) this;
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public boolean isBlockIndirectlyProvidingPowerTo(int i, int j, int k, int l) {
		return false;
	}
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public boolean isBlockIndirectlyGettingPowered(int i, int j, int k) {
		return false;
	}
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public boolean isBlockProvidingPowerTo(int i, int j, int k, int l) {
		int i1 = thisAs.getBlockId(i, j, k);
		return i1 == 0 ? false : Block.blocksList[i1].isPoweringTo(thisAs, i, j, k, l);
	}
}

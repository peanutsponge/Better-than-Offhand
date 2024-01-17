package turniplabs.examplemod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = World.class, remap = false)
public class IndirectPowerDisableMixin {
	/**
	 * @author peanutsponge
	 * @reason to allow a more intuitive system
	 */
	@Overwrite
	public boolean isBlockIndirectlyProvidingPowerTo(int i, int j, int k, int l) {
		return false;
	}

	/**
	 * @author peanutsponge
	 * @reason to allow a more intuitive system
	 */
	@Overwrite
	public boolean isBlockIndirectlyGettingPowered(int i, int j, int k) {
		return false;
	}
}

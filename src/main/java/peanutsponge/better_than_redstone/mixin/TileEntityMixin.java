package peanutsponge.better_than_redstone.mixin;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.blockPipe;

@Mixin(value = TileEntity.class, remap = false)
public class TileEntityMixin {
	@Shadow
	public World worldObj;
	@Shadow public int x;
	@Shadow public int y;
	@Shadow public int z;
	@Inject(method = "onInventoryChanged", at = @At("TAIL"))
	public void onInventoryChanged(CallbackInfo ci){
		this.worldObj.scheduleBlockUpdate(this.x, this.y, this.z, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x+1, this.y, this.z, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x-1, this.y, this.z, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x, this.y+1, this.z, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x, this.y-1, this.z, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x, this.y, this.z+1, blockPipe.id, 1);
		this.worldObj.scheduleBlockUpdate(this.x, this.y, this.z-1, blockPipe.id, 1);
	}

}

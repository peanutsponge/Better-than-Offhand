package peanutsponge.better_than_redstone.mixin;

import net.minecraft.client.render.RenderBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.core.util.helper.Direction.*;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.BlockDirectional.*;

@Mixin(value = RenderBlocks.class, remap = false)
public class DirectionalModelMixin{
	@Shadow private World world;
	@Shadow private WorldSource blockAccess;
	@Shadow private int uvRotateEast;
	@Shadow private int uvRotateWest;
	@Shadow private int uvRotateNorth;
	@Shadow private int uvRotateSouth;
	@Shadow private int uvRotateTop;
	@Shadow private int uvRotateBottom;

    @Shadow
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        return false;
    }

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
	public void renderBlockByRenderType(Block block, int renderType, int x, int y, int z, CallbackInfoReturnable<Boolean> cir)
	{
		if (renderType == 36){
			block.setBlockBoundsBasedOnState(this.world, x, y, z);
			cir.setReturnValue(this.renderBlockDirectional(block, x, y, z));
		}
	}
	@Unique
	public boolean renderBlockDirectional(Block block, int x, int y, int z){
		int data = this.blockAccess.getBlockMetadata(x, y, z);
		int directionCode = getDirectionCode(data);
		Direction placementDirection = getPlacementDirection(directionCode);
		Direction horizontalDirection = getHorizontalDirection(directionCode);
		LOGGER.info("Placement direction: "+ placementDirection + ", "+ placementDirection.getHorizontalIndex());
		LOGGER.info("Horizontal direction: "+ horizontalDirection + ", "+ horizontalDirection.getHorizontalIndex());
		switch (placementDirection) {
			case NORTH://
				this.uvRotateSouth = 1;
				this.uvRotateNorth = 2;
				this.uvRotateTop = 0;
				this.uvRotateBottom = 0;
				break;
			case EAST://
				this.uvRotateEast = 2;
				this.uvRotateWest = 1;
				this.uvRotateTop = 1;
				this.uvRotateBottom = 2;
				break;
			case SOUTH://
				this.uvRotateSouth = 2;
				this.uvRotateNorth = 1;
				this.uvRotateTop = 3;
				this.uvRotateBottom = 3;
				break;
			case WEST://
				this.uvRotateEast = 1;
				this.uvRotateWest = 2;
				this.uvRotateTop = 2;
				this.uvRotateBottom = 1;
				break;
			case UP://
				switch (horizontalDirection) {
					case NORTH://
						this.uvRotateTop = 0;
						break;
					case EAST://
						this.uvRotateTop = 1;
						break;
					case SOUTH://
						this.uvRotateTop = 3;
						break;
					case WEST://
						this.uvRotateTop = 2;
						break;
				}
				break;
			case DOWN://
				switch (horizontalDirection) {
					case NORTH://
						this.uvRotateBottom = 0;
						break;
					case EAST://
						this.uvRotateBottom = 2;
						break;
					case SOUTH://
						this.uvRotateBottom = 3;
						break;
					case WEST://
						this.uvRotateBottom = 1;
						break;
				}
				this.uvRotateEast = 3;
				this.uvRotateWest = 3;
				this.uvRotateSouth = 3;
				this.uvRotateNorth = 3;
				break;
		}

//		if (placementDirection == UP | placementDirection == DOWN){
//			int rotation;
//			switch (horizontalDirection){
//				case NORTH:
//					rotation = 0;
//					break;
//				case EAST:
//					rotation = 1;
//					break;
//				case SOUTH:
//					rotation = 3;
//					break;
//				default:
//					rotation = 2;
//			}
//			this.uvRotateTop = rotation;
//			this.uvRotateBottom = (rotation + 2) % 4;
//			if (placementDirection == DOWN){
//				this.uvRotateEast = 3;
//				this.uvRotateWest = 3;
//				this.uvRotateSouth = 3;
//				this.uvRotateNorth = 3;
//			}
//		}
//		else {
//			int rotation = placementDirection.getHorizontalIndex();
//			this.uvRotateEast = rotation;
//			this.uvRotateWest = rotation;
//			this.uvRotateSouth = rotation;
//			this.uvRotateNorth = rotation;
//			this.uvRotateTop = rotation;
//			this.uvRotateBottom = rotation;
//		}

		this.renderStandardBlock(block, x, y, z);
		this.uvRotateEast = 0;
		this.uvRotateWest = 0;
		this.uvRotateSouth = 0;
		this.uvRotateNorth = 0;
		this.uvRotateTop = 0;
		this.uvRotateBottom = 0;
		return true;
	}
}

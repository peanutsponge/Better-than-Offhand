package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.logic.PistonDirections;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static peanutsponge.better_than_redstone.Signal.hasCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalExtender extends Block {
	private final boolean isOn;
	public int[] atlasIndices = new int[4];

	public BlockSignalExtender(String key, int id, boolean isOn_) {
		super(key, id, Material.metal);
        this.isOn = isOn_;
		this.atlasIndices[0] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_input.png");
		this.atlasIndices[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_side.png");
		this.atlasIndices[2] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_off.png");
		this.atlasIndices[3] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_on.png");
		}
	public static int getDirection(int data) {
		return data & 7;
	}

	public int getFaceTexture() {
		return !this.isOn ? this.atlasIndices[2] : this.atlasIndices[3];
	}

	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		int direction = getDirection(data);
		if (direction > 5) {
			return texCoordToIndex(13, 6);
		} else if (side.getId() == direction) { // face texture
			return getFaceTexture();
		} else {
			return side.getId() != PistonDirections.directionMap[direction] ? this.atlasIndices[1] : this.atlasIndices[0];
		}
	}

	public boolean isSolidRender() {
		return false;
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		int l = world.getBlockMetadata(x, y, z);
		if (this.isOn && !hasCurrent(world, x, y, z)) {
			world.setBlockAndMetadataWithNotify(x, y, z, blockSignalExtenderOff.id, l);
		} else if (!this.isOn) {
			world.setBlockAndMetadataWithNotify(x, y, z, blockSignalExtenderOn.id, l);
			if (!hasCurrent(world, x, y, z)) {
				world.scheduleBlockUpdate(x, y, z, blockSignalExtenderOn.id, 1);
			}
		}
	}


	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		world.setBlockMetadataWithNotify(x, y, z, placementDirection.getId());
		boolean hasCurrent = hasCurrent(world, x, y, z);
		if (hasCurrent) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		int i1 = world.getBlockMetadata(x, y, z);
		boolean flag = hasCurrent(world, x, y, z);
		if (this.isOn && !flag) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		} else if (!this.isOn && flag) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}
	public boolean canProvidePower() {
		return false;
	}
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		return this.isOn;
	}

}

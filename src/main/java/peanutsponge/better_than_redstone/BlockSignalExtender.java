package peanutsponge.better_than_redstone;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.*;
import static peanutsponge.better_than_redstone.Signal.hasCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalExtender extends BlockDirectional {
	public int[] atlasIndicesOutput = new int[2];

	public BlockSignalExtender(String key, int id) {
		super(key, id, Material.metal);
		this.atlasIndicesOutput[0] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_off.png");
		this.atlasIndicesOutput[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_on.png");
		}

	public int getFaceTexture(int data) {
		return this.isOn(data) ? this.atlasIndicesOutput[1] : this.atlasIndicesOutput[0];
	}


	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		if (this.isOn(data) && !hasCurrent(world, x, y, z)) {
			setOn(world, x, y, z, 0);
		} else if (!this.isOn(data)) {
			setOn(world, x, y, z, 1);
			if (!hasCurrent(world, x, y, z)) {
				world.scheduleBlockUpdate(x, y, z, blockSignalExtender.id, 1);
			}
		}
	}


	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x,  y,  z, side, entity, sideHeight);
		boolean hasCurrent = hasCurrent(world, x, y, z);
		if (hasCurrent) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		int data = world.getBlockMetadata(x, y, z);
		boolean flag = hasCurrent(world, x, y, z);
		if (this.isOn(data) && !flag) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		} else if (!this.isOn(data) && flag) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}
	public boolean canProvidePower() {
		return true;
	}
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		int data = blockAccess.getBlockMetadata(x, y, z);
		LOGGER.info("isPoweringTo" + data + this.isOn(data));
		return this.isOn(data);
	}
public void setOn(World world, int x, int y, int z, int on) {
	int data = world.getBlockMetadata(x, y, z);
	int direction = getDirectionCode(data);
	world.setBlockMetadataWithNotify(x, y, z, direction + (on * 16));
}
	public boolean isOn(int data) {
		int on = data >> 4;
		return (on == 1);
	}
}

package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.sound.SoundType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.hasInputCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalPulse extends Block {
	public int[] atlasIndicesOutput = new int[2];

	public BlockSignalPulse(String key, int id) {
		super(key, id, Material.metal);
		this.atlasIndicesOutput[0] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_off.png");
		this.atlasIndicesOutput[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_extender_front_on.png");
		}
	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		return this.isOn(data) ? this.atlasIndicesOutput[1] : this.atlasIndicesOutput[0];
	}

 	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		if (this.isOn(data) && !hasInputCurrent(world, x, y, z)) {
			setOn(world, x, y, z, 0);
		} else if (!this.isOn(data)) {
			setOn(world, x, y, z, 1);
			if (!hasInputCurrent(world, x, y, z)) {
				world.scheduleBlockUpdate(x, y, z, this.id, 1);
			}
		}
	}

 	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		int data = world.getBlockMetadata(x, y, z);
		boolean hasInput = hasInputCurrent(world, x, y, z);
		if (this.isOn(data) && !hasInput) {
			world.scheduleBlockUpdate(x, y, z, this.id, 0);
		} else if (!this.isOn(data) && hasInput) {
			world.scheduleBlockUpdate(x, y, z, this.id, 1);
		}
	}
	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		int data = blockAccess.getBlockMetadata(x, y, z);
		return this.isOn(data);
	}
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		this.blockActivated(world,x,y,z,player);
	}
	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		world.playSoundEffect(SoundType.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3F, 0.6F);
		return true;
	}

public void setOn(World world, int x, int y, int z, int on) {
	world.setBlockMetadataWithNotify(x, y, z, on);
	}

	public boolean isOn(int data) {
		return (data == 1);
	}
}

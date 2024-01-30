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
import static peanutsponge.better_than_redstone.Signal.getMaxCurrent;
import static peanutsponge.better_than_redstone.Signal.hasCurrent;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalPulse extends Block {
	public int[] atlasIndices = new int[2];

	public BlockSignalPulse(String key, int id) {
		super(key, id, Material.metal);
		this.atlasIndices[0] = getOrCreateBlockTextureIndex(MOD_ID, "signal_pulse_off.png");
		this.atlasIndices[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_pulse_on.png");
		}
	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		return this.isOn(data) ? this.atlasIndices[1] : this.atlasIndices[0];
	}

 	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		setOn(world, x, y, z, 0);
	}

 	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		int data = world.getBlockMetadata(x, y, z);
		if (!this.isPowered(data) && hasCurrent(world, x, y, z)) {//toggle on high
			setOn(world, x, y, z, 1);
			world.scheduleBlockUpdate(x, y, z, this.id, getMaxCurrent(world, x, y, z));
		}
		if (this.isPowered(data) != hasCurrent(world, x, y, z)) {//update the received power metadata, when mismatch
			int power = hasCurrent(world, x, y, z)? 1 : 0;
			setPowered(world, x, y, z, power);

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
		setOn(world, x, y, z, 1);
		world.scheduleBlockUpdate(x, y, z, this.id, 15);
		return true;
	}

	/**
	 * Returns the on state from the metadata
	 */
	public boolean isOn(int data) {
		return ((data%2) == 1);
	}
	/**
	 * Returns the received power state from the metadata
	 */
	public boolean isPowered(int data) {
		return ((data>>1) == 1);
	}
	/**
	 * Sets the on state to the metadata, with 0 = off and 1 = on
	 */
	public void setOn(World world, int x, int y, int z, int on) {
		world.playSoundEffect(SoundType.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3F, 0.6F);
		int data = world.getBlockMetadata(x, y, z);
		int newData = isPowered(data)? on + 2 : on ; //on/off is on the right most bit
		world.setBlockMetadataWithNotify(x, y, z, newData);

	}
	/**
	 * Sets the received power state to the metadata, with 0 = not receiving, and 1 = receiving
	 */
	public void setPowered(World world, int x, int y, int z, int powered) {
		int data = world.getBlockMetadata(x, y, z);
		int newData = isOn(data)? powered * 2 + 1 : powered * 2; //received power is on the second to right most bit
		world.setBlockMetadataWithNotify(x, y, z, newData);
	}
}

package peanutsponge.better_than_redstone.signal_components;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import peanutsponge.better_than_redstone.BlockDirectional;
import peanutsponge.better_than_redstone.Directions;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.*;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalRelay extends BlockDirectional {
	public int[] atlasIndicesOutput = new int[16];
	public BlockSignalRelay(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 16; ++i) {
			this.atlasIndicesOutput[i] = getOrCreateBlockTextureIndex(MOD_ID, "signal_conductor" + " ("+ i +").png");
		}
		this.atlasIndices[1] = getOrCreateBlockTextureIndex(MOD_ID, "signal_relay_side.png");
	}
	@Override
	public int getOutputTexture(int data) {
		return this.atlasIndicesOutput[getSignalCode(data)];
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		int signalCode = getSignalCode(data);
		int directionCode = Directions.getDirectionCode(data);
		int signalGot = getOutputCurrent(world, x, y, z);
		if (signalCode != signalGot) {
			world.setBlockMetadataWithNotify(x, y, z, makeMetaData(directionCode, signalGot));
			world.scheduleBlockUpdate(x, y, z, this.id, 1); //probably unnecessary
		}
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		super.onBlockPlaced(world, x,  y,  z, side, entity, sideHeight);
		world.scheduleBlockUpdate(x, y, z, this.id, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		world.scheduleBlockUpdate(x, y, z, this.id, 1);
	}
	@Override
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		int data = blockAccess.getBlockMetadata(x, y, z);
		Direction direction = Directions.getPlacementDirection(Directions.getDirectionCode(data));
		return getSignalCode(data) == 15 & side == direction.getOpposite().getId();
	}

	public static int getOutputCurrent(World world, int x, int y, int z){
		return Math.max(0, getInputCurrent(world, x, y, z) - getSumSideCurrent(world, x, y, z));
	}
}

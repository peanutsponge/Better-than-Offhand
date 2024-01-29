package peanutsponge.better_than_redstone;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.sound.SoundType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.logging.Logger;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static peanutsponge.better_than_redstone.Signal.*;
import static peanutsponge.better_than_redstone.Signal.makeMetaData;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;


public class BlockSignalAnalogInput extends BlockDirectional {
	public int[] atlasIndicesInput = new int[16];

	public BlockSignalAnalogInput(String key, int id) {
		super(key, id, Material.metal);
		for (int i = 0; i < 16; ++i) {
			this.atlasIndicesInput[i] = getOrCreateBlockTextureIndex(MOD_ID, "signal_display" + " (" + i + ").png");
		}
	}

	@Override
	public int getInputTexture(int data) {
		return this.atlasIndicesInput[getSignalCode(data)];
	}

	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side);
		Direction horizontalDirection = Direction.getHorizontalDirection(entity.yRot);
//		LOGGER.info("On block placed: " + horizontalDirection +" , "+ horizontalDirection.getHorizontalIndex());
		world.setBlockMetadataWithNotify(x, y, z, makeDirectionCode(placementDirection, horizontalDirection));
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		this.blockActivated(world,x,y,z,player);
	}
	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		int data = world.getBlockMetadata(x, y, z);
		int oldCurrent = getSignalCode(data);
		int newCurrent = (oldCurrent + 1) % 16;
		world.playSoundEffect(SoundType.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3F, 0.6F);
		world.setBlockMetadataWithNotify(x, y, z, makeMetaData(getDirectionCode(data), newCurrent));
		return true;
	}
	@Override
	public boolean isPoweringTo(WorldSource blockAccess, int x, int y, int z, int side) {
		int data = blockAccess.getBlockMetadata(x, y, z);
		Direction direction = getPlacementDirection(getDirectionCode(data));
		return getSignalCode(data) == 15 & side == direction.getOpposite().getId();
	}
}

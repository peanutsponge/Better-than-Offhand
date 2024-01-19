package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalInverter extends Block {

	public static int[] atlasIndices = new int[2];

	public BlockSignalInverter(String key, int id) {
		super(key, id, Material.metal);
		for(int i = 0; i < 2; ++i) {
			atlasIndices[i] = getOrCreateBlockTextureIndex(MOD_ID, key + " ("+ i +").png");
		}
	}
	@Override
	public int getBlockTextureFromSideAndMetadata(Side side, int data) {
		return atlasIndices[data % 16];
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}
}

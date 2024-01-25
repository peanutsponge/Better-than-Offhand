package peanutsponge.better_than_redstone;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.MOD_ID;
import static turniplabs.halplibe.helper.TextureHelper.getOrCreateBlockTextureIndex;

public class BlockSignalInverter extends BlockDirectional {

	public BlockSignalInverter(String key, int id) {
		super(key, id, Material.metal);
	}


	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}
}

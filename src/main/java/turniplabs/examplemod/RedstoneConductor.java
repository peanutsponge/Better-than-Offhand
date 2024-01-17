package turniplabs.examplemod;
import java.util.Random;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class RedstoneConductor extends Block {


	public RedstoneConductor(String key, int id, Material material) {
		super(key, id, material);
		this.setTicking(true);
	}

	public int tickRate() {
		return 2;
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
	}


//	public void updateTick(World world, int x, int y, int z, Random rand) {
//		boolean isPoweredByBlock = world.isBlockGettingPowered(x, y, z);
//		if (this.isActive) {
//			if (!isPoweredByBlock) {
//				world.setBlockAndMetadataWithNotify(x, y, z, Block.lampIdle.id, world.getBlockMetadata(x, y, z));
//			}
//		} else if (isPoweredByBlock) {
//			world.setBlockAndMetadataWithNotify(x, y, z, Block.lampActive.id, world.getBlockMetadata(x, y, z));
//		}
//	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
	}
}



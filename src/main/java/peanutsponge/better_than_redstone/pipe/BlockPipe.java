package peanutsponge.better_than_redstone.pipe;

import net.minecraft.client.Minecraft;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import peanutsponge.better_than_redstone.BlockTileEntityDirectional;

import java.util.Random;

import static peanutsponge.better_than_redstone.BlockDirectional.getDirectionCode;
import static peanutsponge.better_than_redstone.BlockDirectional.getPlacementDirection;

public class BlockPipe extends BlockTileEntityDirectional {
	private Random random = new Random();

	public BlockPipe(String key, int id) {
		super(key, id, Material.metal);
	}

	public int tickRate() {
		return 1;
	}

	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isClientSide) { //TODO Multiplayer support
			return true;
		} else {
			TileEntityPipe tileentitypipe = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
//			player.displayGUIDispenser(tileentitypipe); //Look into this maybe
			Minecraft.getMinecraft(Minecraft.class).displayGuiScreen(new GuiPipe(player.inventory, tileentitypipe));
			return true;
		}
	}

	private void dispenseItem(World world, int x, int y, int z, Direction spitDirection, Random random) {
		//TODO Fix location & direction
		int x_spit = 0;
		int y_spit = 0;
		int z_spit = 0;


		TileEntityPipe tileentitypipe = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
		ItemStack itemstack = tileentitypipe.getRandomStackFromInventory();
		double d = (double)x + (double)x_spit * 0.6 + 0.5;
		double d1 = (double)y + (double)y_spit * 0.6 + 0.5;
		double d2 = (double)z + (double)z_spit * 0.6 + 0.5;
		if (itemstack == null) {
			world.playSoundEffect(1001, x, y, z, 0);
		}  else {
				EntityItem entityitem = new EntityItem(world, d, d1 - 0.3, d2, itemstack);
				double d3 = random.nextDouble() * 0.1 + 0.2;
				entityitem.xd = (double)x_spit * d3;
				entityitem.yd = (double)y_spit * d3+ 0.20000000298023224;
				entityitem.zd = (double)z_spit * d3;
				entityitem.xd += random.nextGaussian() * 0.007499999832361937 * 6.0;
				entityitem.yd += random.nextGaussian() * 0.007499999832361937 * 6.0;
				entityitem.zd += random.nextGaussian() * 0.007499999832361937 * 6.0;
				world.entityJoinedWorld(entityitem);
				world.playSoundEffect(1000, x, y, z, 0);
		}
	}
	private void moveSides(World world, int x, int y, int z, Direction spitDirection) {

	}
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int data = world.getBlockMetadata(x, y, z);
		Direction spitDirection = getPlacementDirection(getDirectionCode(data));
		this.moveSides(world, x, y, z, spitDirection);
		this.dispenseItem(world, x, y, z, spitDirection, rand);
	}
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
	}



	protected TileEntity getNewBlockEntity() {
		return new TileEntityPipe();
	}

	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		if (world.getBlockTileEntity(x, y, z) != null) {
			TileEntityPipe tileentitypipe = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
			ItemStack itemstack = tileentitypipe.getStackInSlot(0);
			if (itemstack != null) {
				float f = this.random.nextFloat() * 0.8F + 0.1F;
				float f1 = this.random.nextFloat() * 0.8F + 0.1F;
				float f2 = this.random.nextFloat() * 0.8F + 0.1F;

				while(itemstack.stackSize > 0) {
					int i1 = this.random.nextInt(21) + 10;
					if (i1 > itemstack.stackSize) {
						i1 = itemstack.stackSize;
					}

					itemstack.stackSize -= i1;
					EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
					float f3 = 0.05F;
					entityitem.xd = (double)((float)this.random.nextGaussian() * f3);
					entityitem.yd = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
					entityitem.zd = (double)((float)this.random.nextGaussian() * f3);
					world.entityJoinedWorld(entityitem);
				}
			}
		}
		super.onBlockRemoved(world, x, y, z, data);
	}
}

package peanutsponge.better_than_redstone.pipe;

import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import peanutsponge.better_than_redstone.BlockTileEntityDirectional;

import java.util.Random;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.LOGGER;
import static peanutsponge.better_than_redstone.Directions.*;
import static peanutsponge.better_than_redstone.Signal.getMaxSideCurrent;

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

	private void suckItem(World world, int x, int y, int z, Direction spitDirection) { //TODO SUCKING
		int x_suck = directionToX(spitDirection.getOpposite());
		int y_suck = directionToY(spitDirection.getOpposite());
		int z_suck = directionToZ(spitDirection.getOpposite());
	}
	private boolean spitItem(World world, int x, int y, int z, int x_spit, int y_spit, int z_spit, ItemStack pipeStack, Random random) {
		int blockId = world.getBlockId(x+x_spit, y+y_spit, z+z_spit);
		if (Block.solid[blockId]){
			return false;
		}

		double d = (double)x + (double)x_spit * 0.6 + 0.5;
		double d1 = (double)y + (double)y_spit * 0.6 + 0.5;
		double d2 = (double)z + (double)z_spit * 0.6 + 0.5;
		double d3 = random.nextDouble() * 0.1 + 0.2;

		EntityItem entityitem = new EntityItem(world, d, d1 - 0.3, d2, pipeStack);
		entityitem.xd = (double)x_spit * d3;
		entityitem.yd = (double)y_spit * d3+ 0.20000000298023224;
		entityitem.zd = (double)z_spit * d3;
		entityitem.xd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		entityitem.yd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		entityitem.zd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		world.entityJoinedWorld(entityitem);
		return true;
	}

	private boolean spitToBlock(World world, int x, int y, int z, ItemStack pipeStack) {
		if (world.isBlockGettingPowered(x, y, z)){
			return false;
		}
		int blockId = world.getBlockId(x, y, z);
		if (!Block.isEntityTile[blockId]){
			return false;
		}
		TileEntity targetTileEntity = world.getBlockTileEntity(x,y,z);
		if (!(targetTileEntity instanceof IInventory)){
			return false;
		}
		IInventory targetInventory = (IInventory) targetTileEntity;
		int targetInventorySize = targetInventory.getSizeInventory();



		for (int i=0;i<targetInventorySize;i++){
			ItemStack targetStack = targetInventory.getStackInSlot(i);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(i, pipeStack);
				break;
			}
			if(pipeStack.canStackWith(targetStack)){
				targetStack.stackSize++;
				break;
			}
		}

		return true;
	}

	private boolean spitToBlocks(World world, int x, int y, int z, int x_spit, int y_spit, int z_spit, ItemStack pipeStack) {

		if (x_spit == 0) {
			if (this.spitToBlock(world, x + 1, y, z, pipeStack))
				return true;
			if (this.spitToBlock(world, x - 1, y, z, pipeStack))
				return true;
		}
		if (y_spit == 0) {
			if (this.spitToBlock(world, x , y+1, z, pipeStack))
				return true;
			if (this.spitToBlock(world, x , y-1, z, pipeStack))
				return true;
		}
		if (z_spit == 0) {
			if (this.spitToBlock(world, x , y, z+ 1, pipeStack))
				return true;
			if (this.spitToBlock(world, z, y, z - 1, pipeStack))
				return true;
		}
		return this.spitToBlock(world, x + x_spit, y + y_spit, z + z_spit, pipeStack); // The block on the spit side
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		TileEntityPipe tileentitypipe = (TileEntityPipe) world.getBlockTileEntity(x,y,z);
		ItemStack pipeStack = tileentitypipe.decrStackSize(0,1);
		if (pipeStack == null) {
			world.playSoundEffect(1001, x, y, z, 0);
			return;
		}

		int data = world.getBlockMetadata(x, y, z);
		Direction spitDirection = getPlacementDirection(getDirectionCode(data));
		int x_spit = directionToX(spitDirection);
		int y_spit = directionToY(spitDirection);
		int z_spit = directionToZ(spitDirection);

		boolean doneSpitting = this.spitToBlocks(world, x, y, z, x_spit,y_spit,z_spit, pipeStack);
		if (!doneSpitting)
			doneSpitting = this.spitItem(world, x, y, z, x_spit,y_spit,z_spit, pipeStack, rand);
		if (doneSpitting){
			world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
			world.playSoundEffect(1000, x, y, z, 0);
		} else{
			tileentitypipe.addToStackInSlot(pipeStack);
		}

		//TODO Sucking, maybe not here but on a collision trigger.
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

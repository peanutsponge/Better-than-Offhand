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

import static net.minecraft.core.util.helper.Direction.*;
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

	private boolean suckBlock(World world, int x, int y, int z, TileEntityPipe tileentitypipe) {
		if (world.isBlockGettingPowered(x, y, z)){
			return false;
		}
		int blockId = world.getBlockId(x, y, z);
		if (!Block.isEntityTile[blockId] | blockId == this.id){
			return false;
		}
		TileEntity targetTileEntity = world.getBlockTileEntity(x,y,z);
		if (!(targetTileEntity instanceof IInventory)){
			return false;
		}

		IInventory containerInventory = (IInventory) targetTileEntity;
		int targetInventorySize = containerInventory.getSizeInventory();
		ItemStack pipeStack = tileentitypipe.getStackInSlot(0);

		for (int i=0;i<targetInventorySize;i++){
			ItemStack containerStack = containerInventory.getStackInSlot(i);
			if (containerStack == null) {
				continue;
			}
			if (pipeStack == null) {
				tileentitypipe.setInventorySlotContents(0, containerInventory.decrStackSize(i, 1));
				world.playSoundEffect(1000, x, y, z, 0);
				return true;
			}
			if(containerStack.canStackWith(pipeStack)){
				if (pipeStack.stackSize == tileentitypipe.getInventoryStackLimit()){
					continue;
				}
				pipeStack.stackSize += 1;
				tileentitypipe.onInventoryChanged();
				containerInventory.decrStackSize(i,1);
				world.playSoundEffect(1000, x, y, z, 0);
				return true;
			}
		}
		world.playSoundEffect(1001, x, y, z, 0);
		return false;
	}
	private boolean spitItem(World world, int x, int y, int z, int x_spit, int y_spit, int z_spit, TileEntityPipe tileentitypipe, int amount, Random random) {
		int blockId = world.getBlockId(x+x_spit, y+y_spit, z+z_spit);
		if (Block.solid[blockId]){
			return false;
		}

		double d = (double)x + (double)x_spit * 0.6 + 0.5;
		double d1 = (double)y + (double)y_spit * 0.6 + 0.5;
		double d2 = (double)z + (double)z_spit * 0.6 + 0.5;
		double d3 = random.nextDouble() * 0.1 + 0.2;

		EntityItem entityitem = new EntityItem(world, d, d1 - 0.3, d2, tileentitypipe.decrStackSize(0, amount));
		entityitem.xd = (double)x_spit * d3;
		entityitem.yd = (double)y_spit * d3+ 0.20000000298023224;
		entityitem.zd = (double)z_spit * d3;
		entityitem.xd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		entityitem.yd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		entityitem.zd += random.nextGaussian() * 0.007499999832361937 * 6.0;
		world.entityJoinedWorld(entityitem);
		world.playSoundEffect(1000, x, y, z, 0);
		return true;
	}
	/**
	 * First attempts to spit to the sides, then spits to the spit side.
	 */
	private boolean spitToBlocks(World world, int x, int y, int z, int x_spit, int y_spit, int z_spit, TileEntityPipe tileentitypipe, int amount) {
		if (x_spit == 0) {
			if (this.spitToPipe(world, x + 1, y, z, EAST, tileentitypipe, amount))
				return true;
			if (this.spitToPipe(world, x - 1, y, z, WEST ,tileentitypipe, amount))
				return true;
		}
		if (y_spit == 0) {
			if (this.spitToPipe(world, x , y+1, z, UP, tileentitypipe, amount))
				return true;
			if (this.spitToPipe(world, x , y-1, z, DOWN, tileentitypipe, amount))
				return true;
		}
		if (z_spit == 0) {
			if (this.spitToPipe(world, x , y, z+ 1, SOUTH, tileentitypipe, amount))
				return true;
			if (this.spitToPipe(world, z, y, z - 1, NORTH, tileentitypipe, amount))
				return true;
		}
		return this.spitToBlock(world, x + x_spit, y + y_spit, z + z_spit, tileentitypipe, amount); // The block on the spit side
	}
	/**
	 * Ensures that side spitting is only to the suck-side of pipes
	 */
	private boolean spitToPipe(World world, int x, int y, int z, Direction direction, TileEntityPipe tileentitypipe, int amount) {
		if (world.getBlockId(x, y, z) != this.id)
			return false;
		Direction pipeDirection = getPlacementDirection(world, x, y, z);
		if (direction == pipeDirection)
			return this.spitToBlock(world, x, y, z, tileentitypipe, amount);
		return false;
	}
	/**
	 * Attempts to spit to a block located at x, y, z.
	 */
	private boolean spitToBlock(World world, int x, int y, int z, TileEntityPipe tileentitypipe, int amount) {
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
		ItemStack pipeStack = tileentitypipe.getStackInSlot(0);

		for (int i=0;i<targetInventorySize;i++){
			ItemStack targetStack = targetInventory.getStackInSlot(i);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(i, tileentitypipe.decrStackSize(0, amount));
				world.playSoundEffect(1000, x, y, z, 0);
				return true;
			}
			if(pipeStack.canStackWith(targetStack)){
				if ((amount + targetStack.stackSize) > targetInventory.getInventoryStackLimit()){
					continue;
				}
				targetStack.stackSize += amount;
				targetInventory.onInventoryChanged();
				tileentitypipe.decrStackSize(0, amount);
				world.playSoundEffect(1000, x, y, z, 0);
				return true;
			}
		}
		world.playSoundEffect(1001, x, y, z, 0);
		return false;
	}



	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isBlockGettingPowered(x,y,z))
			return;
		TileEntityPipe tileentitypipe = (TileEntityPipe) world.getBlockTileEntity(x,y,z);
		ItemStack pipeStack = tileentitypipe.getStackInSlot(0);
		if (pipeStack == null) {
			world.playSoundEffect(1001, x, y, z, 0);
			return;
		}

		int data = world.getBlockMetadata(x, y, z);
		Direction spitDirection = getPlacementDirection(getDirectionCode(data));
		int x_spit = directionToX(spitDirection);
		int y_spit = directionToY(spitDirection);
		int z_spit = directionToZ(spitDirection);

		int current = getMaxSideCurrent(world, x, y, z);
		if (pipeStack.stackSize > current){
			boolean doneSpitting = this.spitToBlocks(world, x, y, z, x_spit,y_spit,z_spit, tileentitypipe,  1);
			if (!doneSpitting)
				doneSpitting = this.spitItem(world, x, y, z, x_spit,y_spit,z_spit, tileentitypipe, 1, rand);
		}

		int x_suck = directionToX(spitDirection.getOpposite());
		int y_suck = directionToY(spitDirection.getOpposite());
		int z_suck = directionToZ(spitDirection.getOpposite());
		this.suckBlock(world, x + x_suck, y + y_suck, z + z_suck, tileentitypipe);
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

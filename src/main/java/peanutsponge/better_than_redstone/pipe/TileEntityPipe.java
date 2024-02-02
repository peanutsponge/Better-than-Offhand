package peanutsponge.better_than_redstone.pipe;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.blockPipe;
import static peanutsponge.better_than_redstone.Directions.*;
import static peanutsponge.better_than_redstone.Directions.directionToZ;

public class TileEntityPipe extends TileEntity implements IInventory {
	private ItemStack pipeContents;
	public boolean isTicking = true;
	public int getSizeInventory() {
		return 1;
	}

	public ItemStack getStackInSlot(int i) {
		return this.pipeContents;
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.pipeContents != null) {
			ItemStack itemstack1;
			if (this.pipeContents.stackSize <= j) {
				itemstack1 = this.pipeContents;
				this.pipeContents = null;
				this.onInventoryChanged();
				return itemstack1;
			} else {
				itemstack1 = this.pipeContents.splitStack(j);
				if (this.pipeContents.stackSize <= 0) {
					this.pipeContents = null;
				}

				this.onInventoryChanged();
				return itemstack1;
			}
		} else {
			return null;
		}
	}


	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.pipeContents = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return "Pipe";
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j < 1) {
				this.pipeContents = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

	}

	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();
		if (this.pipeContents != null) {
			CompoundTag nbttagcompound1 = new CompoundTag();
			nbttagcompound1.putByte("Slot", (byte)0);
			this.pipeContents.writeToNBT(nbttagcompound1);
			nbttaglist.addTag(nbttagcompound1);

		}

		nbttagcompound.put("Items", nbttaglist);
	}

	public int getInventoryStackLimit() {
		return 16;
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (this.worldObj.getBlockTileEntity(this.x, this.y, this.z) != this) {
			return false;
		} else {
			return entityplayer.distanceToSqr((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
		}
	}

	public void sortInventory() {
	}
	public void onInventoryChanged(){
		this.worldObj.scheduleBlockUpdate(this.x, this.y, this.z, blockPipe.id, 1);
	}
	public boolean addItem(ItemStack itemstack){
		if (this.pipeContents == null){
			this.pipeContents = itemstack;
			return true;
		} else if (itemstack != null && itemstack.canStackWith(this.pipeContents) && this.pipeContents.stackSize < this.getInventoryStackLimit()) {
			this.pipeContents.stackSize++;
			itemstack.stackSize--;
			return true;
		}
		return false;
	}
	public void tick() {
		if (!isTicking)
			return;
		if (this.worldObj != null && !this.worldObj.isClientSide && !this.worldObj.isBlockGettingPowered(this.x, this.y, this.z)) {
			Direction spitDirection = getPlacementDirection(this.worldObj, this.x, this.y, this.z);
			int x_suck =this.x + directionToX(spitDirection.getOpposite());
			int y_suck = this.y +directionToY(spitDirection.getOpposite());
			int z_suck = this.z +directionToZ(spitDirection.getOpposite());
			if (Block.solid[this.worldObj.getBlockId(x_suck, y_suck, z_suck)]){
				return;
			}
			AABB aabb = AABB.getBoundingBoxFromPool((double) x_suck, (double) y_suck, (double) z_suck, (double)(1 + x_suck), (double)(1 + y_suck), (double)(1+ z_suck));
			List<Entity> entities = this.worldObj.getEntitiesWithinAABB(EntityItem.class, aabb);
			if (entities.size() > 0) {
				Iterator var4 = entities.iterator();

				while(var4.hasNext()) {
					Entity e = (Entity)var4.next();
					EntityItem entity = (EntityItem)e;
					if (entity.item != null && entity.item.stackSize > 0 && entity.basketPickupDelay == 0) {
						if (this.addItem(entity.item)){
							onInventoryChanged();
							e.outOfWorld();
							break;
						}
					}
				}
			}
		}
	}
}

package peanutsponge.better_than_redstone.pipe;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import java.util.Random;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;

import static peanutsponge.better_than_redstone.BetterThanRedstoneMod.blockPipe;

public class TileEntityPipe extends TileEntity implements IInventory {
	private ItemStack pipeContents;

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

//	public void addToStackInSlot(ItemStack itemstack){
//		if (this.pipeContents == null){
//			this.pipeContents = itemstack;
//		} else if (itemstack != null && itemstack.canStackWith(this.pipeContents)) {
//			this.pipeContents.stackSize += itemstack.stackSize;
//		}
//		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
//			itemstack.stackSize = this.getInventoryStackLimit();
//		}
//	}
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


}

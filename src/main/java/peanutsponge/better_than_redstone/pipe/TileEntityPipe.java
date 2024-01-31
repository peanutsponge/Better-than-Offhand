package peanutsponge.better_than_redstone.pipe;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import java.util.Random;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;

public class TileEntityPipe extends TileEntity implements IInventory {
	private ItemStack[] pipeContents = new ItemStack[1];
	private Random pipeRandom = new Random();

	public int getSizeInventory() {
		return 1;
	}

	public ItemStack getStackInSlot(int i) {
		return this.pipeContents[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.pipeContents[i] != null) {
			ItemStack itemstack1;
			if (this.pipeContents[i].stackSize <= j) {
				itemstack1 = this.pipeContents[i];
				this.pipeContents[i] = null;
				this.onInventoryChanged();
				return itemstack1;
			} else {
				itemstack1 = this.pipeContents[i].splitStack(j);
				if (this.pipeContents[i].stackSize <= 0) {
					this.pipeContents[i] = null;
				}

				this.onInventoryChanged();
				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public ItemStack getRandomStackFromInventory() {
		int i = -1;
		int j = 1;

		for(int k = 0; k < this.pipeContents.length; ++k) {
			if (this.pipeContents[k] != null && this.pipeRandom.nextInt(j++) == 0) {
				i = k;
			}
		}

		if (i >= 0) {
			return this.decrStackSize(i, 1);
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.pipeContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return "Trap";
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.pipeContents = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j < this.pipeContents.length) {
				this.pipeContents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

	}

	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();

		for(int i = 0; i < this.pipeContents.length; ++i) {
			if (this.pipeContents[i] != null) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)i);
				this.pipeContents[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
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
}

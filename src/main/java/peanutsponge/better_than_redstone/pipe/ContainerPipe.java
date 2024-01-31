package peanutsponge.better_than_redstone.pipe;

import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.entity.TileEntityDispenser;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.slot.Slot;

public class ContainerPipe extends Container {
	public TileEntityPipe tileEntity;
	private int pipeSlotsStart;
	private int inventorySlotsStart;
	private int hotbarSlotsStart;

	public ContainerPipe(IInventory iinventory, TileEntityPipe tileentitypipe) {
		this.tileEntity = tileentitypipe;

		int k;
		int i1;

		this.addSlot(new Slot(tileentitypipe, 0, 62 + 18, 17 + 18));

		for(k = 0; k < 3; ++k) {
			for(i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(iinventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for(k = 0; k < 9; ++k) {
			this.addSlot(new Slot(iinventory, k, 8 + k * 18, 142));
		}

		this.pipeSlotsStart = 0;
		this.inventorySlotsStart = 9 - 8;
		this.hotbarSlotsStart = 36 - 8;
	}

	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return this.tileEntity.canInteractWith(entityplayer);
	}

	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
		if (slot.id >= this.pipeSlotsStart && slot.id < this.inventorySlotsStart) {
			return this.getSlots(this.pipeSlotsStart, 1, false);
		} else {
			if (action == InventoryAction.MOVE_ALL) {
				if (slot.id >= this.inventorySlotsStart && slot.id < this.hotbarSlotsStart) {
					return this.getSlots(this.inventorySlotsStart, 27, false);
				}

				if (slot.id >= this.hotbarSlotsStart) {
					return this.getSlots(this.hotbarSlotsStart, 9, false);
				}
			}

			return action == InventoryAction.MOVE_SIMILAR && slot.id >= this.inventorySlotsStart ? this.getSlots(this.inventorySlotsStart, 36, false) : null;
		}
	}

	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
		return slot.id >= this.pipeSlotsStart && slot.id < this.inventorySlotsStart ? this.getSlots(this.inventorySlotsStart, 36, false) : this.getSlots(this.pipeSlotsStart, 1, false);
	}
}

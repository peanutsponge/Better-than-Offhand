package peanutsponge.better_than_redstone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.ChunkPosition;

public class RedstoneConductor extends Block {
	private Set power_level = new HashSet();

	public RedstoneConductor(String key, int id, Material material) {
		super(key, id, material);
		this.setTicking(true);
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.updateAndPropagateCurrentStrength(world, x, y, z);
		world.notifyBlocksOfNeighborChange(x+ 1, y , z, this.id);
		world.notifyBlocksOfNeighborChange(x- 1, y , z, this.id);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id);
		world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, this.id);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, this.id);

	}


	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.updateAndPropagateCurrentStrength(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}


	private void updateAndPropagateCurrentStrength(World world, int x, int y, int z) {
		this.something(world, x, y, z, x, y, z);
		ArrayList arraylist = new ArrayList(this.power_level);
		this.power_level.clear();
		for (int l = 0; l < arraylist.size(); ++l) {
			ChunkPosition chunkposition = (ChunkPosition) arraylist.get(l);
			world.notifyBlocksOfNeighborChange(chunkposition.x, chunkposition.y, chunkposition.z, this.id);
		}
	}
	private int getMaxCurrentStrength(World world, int x, int y, int z, int l) {
		if (world.getBlockId(x, y, z) != this.id) {
			return l;
		} else {
			int l2 = world.getBlockMetadata(x, y, z);
			return l2 > l ? l2 : l;
		}
	}
	private void something (World world,int x, int y, int z, int x2, int y2, int z2) {
		int k1 = world.getBlockMetadata(x, y, z);
		int l1 = 0;
		int j2;
		int l2;
		int j3;

		for (j2 = 0; j2 < 4; ++j2) {
			l2 = x;
			j3 = z;
			if (j2 == 0) {
				l2 = x - 1;
			}

			if (j2 == 1) {
				++l2;
			}

			if (j2 == 2) {
				j3 = z - 1;
			}

			if (j2 == 3) {
				++j3;
			}

			if (l2 != x2 || y != y2 || j3 != z2) {
				l1 = this.getMaxCurrentStrength(world, l2, y, j3, l1);
			}

			if (world.isBlockNormalCube(l2, y, j3) && !world.isBlockNormalCube(x, y + 1, z)) {
				if (l2 != x2 || y + 1 != y2 || j3 != z2) {
					l1 = this.getMaxCurrentStrength(world, l2, y + 1, j3, l1);
				}
			} else if (!world.isBlockNormalCube(l2, y, j3) && (l2 != x2 || y - 1 != y2 || j3 != z2)) {
				l1 = this.getMaxCurrentStrength(world, l2, y - 1, j3, l1);
			}
		}

		if (l1 > 0) {
			--l1;
		} else {
			l1 = 0;
		}

		if (k1 != l1) {
			world.editingBlocks = true;
			world.setBlockMetadataWithNotify(x, y, z, l1);
			world.markBlocksDirty(x, y, z, x, y, z);
			world.editingBlocks = false;

			for(j2 = 0; j2 < 4; ++j2) {
				l2 = x;
				j3 = z;
				int k3 = y - 1;
				if (j2 == 0) {
					l2 = x - 1;
				}

				if (j2 == 1) {
					++l2;
				}

				if (j2 == 2) {
					j3 = z - 1;
				}

				if (j2 == 3) {
					++j3;
				}

				if (world.isBlockNormalCube(l2, y, j3)) {
					k3 += 2;
				}
				int l3 = this.getMaxCurrentStrength(world, l2, y, j3, -1);
				l1 = world.getBlockMetadata(x, y, z);
				if (l1 > 0) {
					--l1;
				}

				if (l3 >= 0 && l3 != l1) {
					this.something(world, l2, y, j3, x, y, z);
				}

				l3 = this.getMaxCurrentStrength(world, l2, k3, j3, -1);
				l1 = world.getBlockMetadata(x, y, z);
				if (l1 > 0) {
					--l1;
				}

				if (l3 >= 0 && l3 != l1) {
					this.something(world, l2, k3, j3, x, y, z);
				}
			}

			if (k1 == 0 || l1 == 0) {
				this.power_level.add(new ChunkPosition(x, y, z));
				this.power_level.add(new ChunkPosition(x - 1, y, z));
				this.power_level.add(new ChunkPosition(x + 1, y, z));
				this.power_level.add(new ChunkPosition(x, y - 1, z));
				this.power_level.add(new ChunkPosition(x, y + 1, z));
				this.power_level.add(new ChunkPosition(x, y, z - 1));
				this.power_level.add(new ChunkPosition(x, y, z + 1));
			}
		}
	}
}

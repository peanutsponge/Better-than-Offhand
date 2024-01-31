package peanutsponge.better_than_redstone.pipe;

import net.minecraft.client.gui.GuiContainer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiPipe extends GuiContainer {
	public GuiPipe(InventoryPlayer inventoryplayer, TileEntityPipe tileentitypipe) {
		super(new ContainerPipe(inventoryplayer, tileentitypipe));
	}

	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString(I18n.getInstance().translateKey("gui.pipe.label.pipe"), 60, 6, 4210752);
		this.fontRenderer.drawString(I18n.getInstance().translateKey("gui.pipe.label.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f) {
		int i = this.mc.renderEngine.getTexture("/gui/trap.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
	}
}

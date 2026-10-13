package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiDispenser extends GuiContainer {
   private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");
   private final InventoryPlayer playerInventory;
   public IInventory dispenserInventory;
   private static final long bb = 48796425439072L;

   public GuiDispenser(InventoryPlayer var1, IInventory var2) {
      super(new ContainerDispenser(var1, var2));
      this.playerInventory = var1;
      this.dispenserInventory = var2;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      String var5 = this.dispenserInventory.getDisplayName().getUnformattedText();
      this.fontRendererObj.D(var5, this.xSize / 2 - this.fontRendererObj.getStringWidth(var5) / 2, 6.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
      int var4 = (this.width - this.xSize) / 2;
      int var5 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
   }
}

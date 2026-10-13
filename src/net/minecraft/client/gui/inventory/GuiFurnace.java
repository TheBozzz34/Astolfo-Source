package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer {
   private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
   private final InventoryPlayer playerInventory;
   private IInventory tileFurnace;
   private static final long bb = 43414933809850L;

   public GuiFurnace(InventoryPlayer var1, IInventory var2) {
      super(new ContainerFurnace(var1, var2));
      this.playerInventory = var1;
      this.tileFurnace = var2;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      String var5 = this.tileFurnace.getDisplayName().getUnformattedText();
      this.fontRendererObj.D(var5, this.xSize / 2 - this.fontRendererObj.getStringWidth(var5) / 2, 6.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      if (TileEntityFurnace.isBurning(this.tileFurnace)) {
         int var9 = this.getBurnLeftScaled(13);
         this.drawTexturedModalRect(var7 + 56, var8 + 36 + 12 - var9, 176, 12 - var9, 14, var9 + 1);
      }

      int var10 = this.getCookProgressScaled(24);
      this.drawTexturedModalRect(var7 + 79, var8 + 34, 176, 14, var10 + 1, 16);
   }

   private int getCookProgressScaled(int var1) {
      int var5 = this.tileFurnace.getField(2);
      int var6 = this.tileFurnace.getField(3);
      return var6 != 0 && var5 != 0 ? var5 * var1 / var6 : 0;
   }

   private int getBurnLeftScaled(int var1) {
      int var5 = this.tileFurnace.getField(1);
      if (var5 == 0) {
         var5 = 200;
      }

      return this.tileFurnace.getField(0) * var1 / var5;
   }
}

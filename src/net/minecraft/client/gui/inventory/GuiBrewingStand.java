package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBrewingStand extends GuiContainer {
   private static final ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private final InventoryPlayer playerInventory;
   private IInventory tileBrewingStand;
   private static final long bb = 71851714743262L;

   public GuiBrewingStand(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBrewingStand(var1, var2));
      this.playerInventory = var1;
      this.tileBrewingStand = var2;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      String var5 = this.tileBrewingStand.getDisplayName().getUnformattedText();
      this.fontRendererObj.D(var5, this.xSize / 2 - this.fontRendererObj.getStringWidth(var5) / 2, 6.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      int var9 = this.tileBrewingStand.getField(0);
      if (var9 > 0) {
         int var10 = (int)(28.0F * (1.0F - var9 / 400.0F));
         if (var10 > 0) {
            this.drawTexturedModalRect(var7 + 97, var8 + 16, 176, 0, 9, var10);
         }

         int var11 = var9 / 2 % 7;
         switch (var11) {
            case 0:
               var10 = 29;
               break;
            case 1:
               var10 = 24;
               break;
            case 2:
               var10 = 20;
               break;
            case 3:
               var10 = 16;
               break;
            case 4:
               var10 = 11;
               break;
            case 5:
               var10 = 6;
               break;
            case 6:
               var10 = 0;
         }

         if (var10 > 0) {
            this.drawTexturedModalRect(var7 + 65, var8 + 14 + 29 - var10, 185, 29 - var10, 12, var10);
         }
      }
   }
}

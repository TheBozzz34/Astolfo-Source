package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer {
   private static final ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
   private IInventory playerInventory;
   private IInventory horseInventory;
   private EntityHorse horseEntity;
   private float mousePosx;
   private float mousePosY;
   private static final long bb = 129646921174906L;

   public GuiScreenHorseInventory(IInventory var1, IInventory var2, EntityHorse var3) {
      super(new ContainerHorseInventory(var1, var2, var3, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = var1;
      this.horseInventory = var2;
      this.horseEntity = var3;
      this.allowUserInput = false;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      this.fontRendererObj.D(this.horseInventory.getDisplayName().getUnformattedText(), 8.0F, 6.0F, 4210752);
      this.fontRendererObj.D(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(horseGuiTextures);
      int var7 = (this.width - this.xSize) / 2;
      int var8 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity.isChested()) {
         this.drawTexturedModalRect(var7 + 79, var8 + 17, 0, this.ySize, 90, 54);
      }

      if (this.horseEntity.canWearArmor()) {
         this.drawTexturedModalRect(var7 + 7, var8 + 35, 0, this.ySize + 54, 18, 18);
      }

      GuiInventory.drawEntityOnScreen(var7 + 51, var8 + 60, 17, var7 + 51 - this.mousePosx, var8 + 75 - 50 - this.mousePosY, this.horseEntity);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.mousePosx = var1;
      this.mousePosY = var2;
      super.drawScreen(var1, var2, var3);
   }
}

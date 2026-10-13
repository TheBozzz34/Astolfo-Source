package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiInventory extends InventoryEffectRenderer {
   private float oldMouseX;
   private float oldMouseY;
   private static final long bb = 8927156825382L;

   public GuiInventory(EntityPlayer var1) {
      super(var1.inventoryContainer);
      this.allowUserInput = true;
   }

   @Override
   public void updateScreen() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      } else {
         super.initGui();
      }
   }

   @Override
   public void drawGuiContainerForegroundLayer(int var1, int var2) {
      this.fontRendererObj.D(I18n.format("container.crafting"), 86.0F, 16.0F, 4210752);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
      this.oldMouseX = var1;
      this.oldMouseY = var2;
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(inventoryBackground);
      int var6 = this.guiLeft;
      int var7 = this.guiTop;
      this.drawTexturedModalRect(var6, var7, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(var6 + 51, var7 + 75, 30, var6 + 51 - this.oldMouseX, var7 + 75 - 50 - this.oldMouseY, this.mc.thePlayer);
   }

   public static void drawEntityOnScreen(int var0, int var1, int var2, float var3, float var4, EntityLivingBase var5) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(var0, var1, 50.0F);
      GlStateManager.scale(-var2, var2, var2);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float var10 = var5.renderYawOffset;
      float var11 = var5.rotationYaw;
      float var12 = var5.rotationPitch;
      float var13 = var5.prevRotationYawHead;
      float var14 = var5.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(var4 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      var5.renderYawOffset = (float)Math.atan(var3 / 40.0F) * 20.0F;
      var5.rotationYaw = (float)Math.atan(var3 / 40.0F) * 40.0F;
      var5.rotationPitch = -((float)Math.atan(var4 / 40.0F)) * 20.0F;
      var5.rotationYawHead = var5.rotationYaw;
      var5.prevRotationYawHead = var5.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager var15 = Minecraft.getMinecraft().getRenderManager();
      var15.setPlayerViewY(180.0F);
      var15.setRenderShadow(false);
      var15.renderEntityWithPosYaw(var5, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      var15.setRenderShadow(true);
      var5.renderYawOffset = var10;
      var5.rotationYaw = var11;
      var5.rotationPitch = var12;
      var5.prevRotationYawHead = var13;
      var5.rotationYawHead = var14;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public static void g(int var0, int var1, float var2, float var3, int var4, EntityLivingBase var5) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(var0, var1, 50.0F);
      GlStateManager.scale(-var4, var4, var4);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float var9 = var5.renderYawOffset;
      float var10 = var5.rotationYaw;
      float var11 = var5.rotationPitch;
      float var12 = var5.prevRotationYawHead;
      float var13 = var5.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(0.025F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      var5.renderYawOffset = (float)Math.atan(0.025F) * 20.0F;
      var5.rotationYaw = var2;
      var5.rotationPitch = var3;
      var5.rotationYawHead = var5.rotationYaw;
      var5.prevRotationYawHead = var5.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager var14 = Minecraft.getMinecraft().getRenderManager();
      var14.setPlayerViewY(180.0F);
      var14.setRenderShadow(false);
      var14.renderEntityWithPosYaw(var5, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      var14.setRenderShadow(true);
      var5.renderYawOffset = var9;
      var5.rotationYaw = var10;
      var5.rotationPitch = var11;
      var5.prevRotationYawHead = var12;
      var5.rotationYawHead = var13;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (var1.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }
}

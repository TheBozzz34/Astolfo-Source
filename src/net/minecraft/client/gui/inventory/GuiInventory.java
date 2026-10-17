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

   public GuiInventory(EntityPlayer entityPlayer) {
      super(entityPlayer.inventoryContainer);
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
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.drawStringNoShadow(I18n.format("container.crafting"), 86.0F, 16.0F, 4210752);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.oldMouseX = mouseX;
      this.oldMouseY = mouseY;
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(inventoryBackground);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - this.oldMouseX, j + 75 - 50 - this.oldMouseY, this.mc.thePlayer);
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(posX, posY, 50.0F);
      GlStateManager.scale(-scale, scale, scale);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float f = ent.renderYawOffset;
      float g = ent.rotationYaw;
      float h = ent.rotationPitch;
      float f2 = ent.prevRotationYawHead;
      float f3 = ent.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      ent.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
      ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
      ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
      ent.rotationYawHead = ent.rotationYaw;
      ent.prevRotationYawHead = ent.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      renderManager.setRenderShadow(true);
      ent.renderYawOffset = f;
      ent.rotationYaw = g;
      ent.rotationPitch = h;
      ent.prevRotationYawHead = f2;
      ent.rotationYawHead = f3;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public static void drawEntityWithRotation(int i, int j, float f, float g, int k, EntityLivingBase entityLivingBase) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(i, j, 50.0F);
      GlStateManager.scale(-k, k, k);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float h = entityLivingBase.renderYawOffset;
      float f2 = entityLivingBase.rotationYaw;
      float f3 = entityLivingBase.rotationPitch;
      float f4 = entityLivingBase.prevRotationYawHead;
      float f5 = entityLivingBase.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(0.025F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      entityLivingBase.renderYawOffset = (float)Math.atan(0.025F) * 20.0F;
      entityLivingBase.rotationYaw = f;
      entityLivingBase.rotationPitch = g;
      entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
      entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      renderManager.setRenderShadow(true);
      entityLivingBase.renderYawOffset = h;
      entityLivingBase.rotationYaw = f2;
      entityLivingBase.rotationPitch = f3;
      entityLivingBase.prevRotationYawHead = f4;
      entityLivingBase.rotationYawHead = f5;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }
}

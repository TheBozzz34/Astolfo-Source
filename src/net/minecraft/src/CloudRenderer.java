package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
   private Minecraft mc;
   private boolean updated = false;
   private boolean renderFancy = false;
   public int cloudTickCounter;
   public float partialTicks;
   private int updateCloudTickCounter = -1;
   private int glListClouds = 0;
   private double updatePlayerX = 0.0;
   private double updatePlayerY = 0.0;
   private double updatePlayerZ = 0.0;

   public CloudRenderer(Minecraft mc) {
      this.mc = mc;
      this.updateCloudTickCounter = GLAllocation.generateDisplayLists(1);
   }

   public void u(boolean bl, int i, float f) {
      if (this.renderFancy != bl) {
         this.updated = false;
      }

      this.renderFancy = bl;
      this.cloudTickCounter = i;
      this.partialTicks = f;
   }

   public boolean shouldUpdateGlList() {
      if (!this.updated) {
         return true;
      }

      if (this.cloudTickCounter >= this.glListClouds + 20) {
         return true;
      }

      Entity entity = this.mc.getRenderViewEntity();
      boolean bl = this.updatePlayerY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      boolean bl2 = entity.prevPosY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      return bl2 != bl;
   }

   public void startUpdateGlList() {
      GL11.glNewList(this.updateCloudTickCounter, 4864);
   }

   public void endUpdateGlList() {
      GL11.glEndList();
      this.glListClouds = this.cloudTickCounter;
      this.updatePlayerX = this.mc.getRenderViewEntity().prevPosX;
      this.updatePlayerY = this.mc.getRenderViewEntity().prevPosY;
      this.updatePlayerZ = this.mc.getRenderViewEntity().prevPosZ;
      this.updated = true;
      GlStateManager.resetColor();
   }

   public void renderGlList() {
      Entity entity = this.mc.getRenderViewEntity();
      double d = entity.prevPosX + (entity.posX - entity.prevPosX) * this.partialTicks;
      double e = entity.prevPosY + (entity.posY - entity.prevPosY) * this.partialTicks;
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * this.partialTicks;
      double d3 = this.cloudTickCounter - this.glListClouds + this.partialTicks;
      float f = (float)(d - this.updatePlayerX + d3 * 0.03);
      float g = (float)(e - this.updatePlayerY);
      float h = (float)(d2 - this.updatePlayerZ);
      GlStateManager.pushMatrix();
      if (this.renderFancy) {
         GlStateManager.translate(-f / 12.0F, -g, -h / 12.0F);
      } else {
         GlStateManager.translate(-f, -g, -h);
      }

      GlStateManager.callList(this.updateCloudTickCounter);
      GlStateManager.popMatrix();
      GlStateManager.resetColor();
   }

   public void reset() {
      this.updated = false;
   }
}

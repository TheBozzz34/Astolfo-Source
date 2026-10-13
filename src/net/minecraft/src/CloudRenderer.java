package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
   private Minecraft mc;
   private boolean updated = false;
   private boolean K = false;
   public int cloudTickCounter;
   public float partialTicks;
   private int updateCloudTickCounter = -1;
   private int glListClouds = 0;
   private double updatePlayerX = 0.0;
   private double updatePlayerY = 0.0;
   private double updatePlayerZ = 0.0;
   private static final long a = 133384793235791L;

   public CloudRenderer(Minecraft var1) {
      this.mc = var1;
      this.updateCloudTickCounter = GLAllocation.generateDisplayLists(1);
   }

   public void u(boolean var1, int var2, float var3) {
      if (this.K != var1) {
         this.updated = false;
      }

      this.K = var1;
      this.cloudTickCounter = var2;
      this.partialTicks = var3;
   }

   public boolean shouldUpdateGlList() {
      if (!this.updated) {
         return true;
      }

      if (this.cloudTickCounter >= this.glListClouds + 20) {
         return true;
      }

      Entity var4 = this.mc.getRenderViewEntity();
      boolean var5 = this.updatePlayerY + var4.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      boolean var6 = var4.prevPosY + var4.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      return var6 != var5;
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
      Entity var4 = this.mc.getRenderViewEntity();
      double var5 = var4.prevPosX + (var4.posX - var4.prevPosX) * this.partialTicks;
      double var7 = var4.prevPosY + (var4.posY - var4.prevPosY) * this.partialTicks;
      double var9 = var4.prevPosZ + (var4.posZ - var4.prevPosZ) * this.partialTicks;
      double var11 = this.cloudTickCounter - this.glListClouds + this.partialTicks;
      float var13 = (float)(var5 - this.updatePlayerX + var11 * 0.03);
      float var14 = (float)(var7 - this.updatePlayerY);
      float var15 = (float)(var9 - this.updatePlayerZ);
      GlStateManager.pushMatrix();
      if (this.K) {
         GlStateManager.translate(-var13 / 12.0F, -var14, -var15 / 12.0F);
      } else {
         GlStateManager.translate(-var13, -var14, -var15);
      }

      GlStateManager.callList(this.updateCloudTickCounter);
      GlStateManager.popMatrix();
      GlStateManager.resetColor();
   }

   public void reset() {
      this.updated = false;
   }
}

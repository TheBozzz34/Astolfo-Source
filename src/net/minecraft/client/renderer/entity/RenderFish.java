package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook> {
   private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");
   private static final long a = 20729444810482L;

   public RenderFish(RenderManager var1) {
      super(var1);
   }

   public void doRender(EntityFishHook var1, double var2, double var4, double var6, float var8, float var9) {
      long var10 = a ^ 2612742436680L;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.bindEntityTexture(var1);
      Tessellator var13 = Tessellator.getInstance();
      WorldRenderer var14 = var13.getWorldRenderer();
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      var14.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      var14.pos(-0.5, -0.5, 0.0).tex(0.0625, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(0.5, -0.5, 0.0).tex(0.125, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(0.5, 0.5, 0.0).tex(0.125, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      var14.pos(-0.5, 0.5, 0.0).tex(0.0625, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      var13.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      if (var1.angler != null) {
         float var24 = var1.angler.getSwingProgress(var9);
         float var25 = MathHelper.sin(MathHelper.sqrt_float(var24) * (float) Math.PI);
         Vec3 var26 = new Vec3(-0.36, 0.03, 0.35);
         var26 = var26.rotatePitch(
            -(var1.angler.prevRotationPitch + (var1.angler.rotationPitch - var1.angler.prevRotationPitch) * var9) * (float) Math.PI / 180.0F
         );
         var26 = var26.rotateYaw(-(var1.angler.prevRotationYaw + (var1.angler.rotationYaw - var1.angler.prevRotationYaw) * var9) * (float) Math.PI / 180.0F);
         var26 = var26.rotateYaw(var25 * 0.5F);
         var26 = var26.rotatePitch(-var25 * 0.7F);
         double var27 = var1.angler.prevPosX + (var1.angler.posX - var1.angler.prevPosX) * var9 + var26.xCoord;
         double var29 = var1.angler.prevPosY + (var1.angler.posY - var1.angler.prevPosY) * var9 + var26.yCoord;
         double var31 = var1.angler.prevPosZ + (var1.angler.posZ - var1.angler.prevPosZ) * var9 + var26.zCoord;
         double var33 = var1.angler.getEyeHeight();
         if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || var1.angler != Minecraft.getMinecraft().thePlayer) {
            float var35 = (var1.angler.prevRenderYawOffset + (var1.angler.renderYawOffset - var1.angler.prevRenderYawOffset) * var9) * (float) Math.PI / 180.0F;
            double var36 = MathHelper.sin(var35);
            double var38 = MathHelper.cos(var35);
            var27 = var1.angler.prevPosX + (var1.angler.posX - var1.angler.prevPosX) * var9 - var38 * 0.35 - var36 * 0.8;
            var29 = var1.angler.prevPosY + var33 + (var1.angler.posY - var1.angler.prevPosY) * var9 - 0.45;
            var31 = var1.angler.prevPosZ + (var1.angler.posZ - var1.angler.prevPosZ) * var9 - var36 * 0.35 + var38 * 0.8;
            var33 = var1.angler.isSneaking() ? -0.1875 : 0.0;
         }

         double var54 = var1.prevPosX + (var1.posX - var1.prevPosX) * var9;
         double var37 = var1.prevPosY + (var1.posY - var1.prevPosY) * var9 + 0.25;
         long var10000 = 67459851010375763L ^ var10;
         double var39 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * var9;
         double var41 = (float)(var27 - var54);
         double var43 = (float)(var29 - var37) + var33;
         double var45 = (float)(var31 - var39);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         var14.begin(3, DefaultVertexFormats.POSITION_COLOR);

         for (int var48 = 0; var48 <= 16; var48++) {
            float var49 = var48 / 16.0F;
            var14.pos(var2 + var41 * var49, var4 + var43 * (var49 * var49 + var49) * 0.5 + 0.25, var6 + var45 * var49).color(0, 0, 0, 255).endVertex();
         }

         var13.draw();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         super.doRender(var1, var2, var4, var6, var8, var9);
      }
   }

   public ResourceLocation getEntityTexture(EntityFishHook var1) {
      return FISH_PARTICLES;
   }
}

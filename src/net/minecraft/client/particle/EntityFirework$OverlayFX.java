package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework$OverlayFX extends EntityFX {
   private static final long ab = 113651520968428L;

   public EntityFirework$OverlayFX(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.particleMaxAge = 4;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var15 = 7.1F * MathHelper.sin((this.particleAge + var3 - 1.0F) * 0.25F * (float) Math.PI);
      this.particleAlpha = 0.6F - (this.particleAge + var3 - 1.0F) * 0.25F * 0.5F;
      float var16 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var17 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
      float var18 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
      int var19 = this.getBrightnessForRender(var3);
      int var20 = var19 >> 16 & 65535;
      int var21 = var19 & 65535;
      var1.pos(var16 - var4 * var15 - var7 * var15, var17 - var5 * var15, var18 - var6 * var15 - var8 * var15)
         .tex(0.5, 0.375)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 - var4 * var15 + var7 * var15, var17 + var5 * var15, var18 - var6 * var15 + var8 * var15)
         .tex(0.5, 0.125)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 + var4 * var15 + var7 * var15, var17 + var5 * var15, var18 + var6 * var15 + var8 * var15)
         .tex(0.25, 0.125)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 + var4 * var15 - var7 * var15, var17 - var5 * var15, var18 + var6 * var15 - var8 * var15)
         .tex(0.25, 0.375)
         .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
         .lightmap(var20, var21)
         .endVertex();
   }
}

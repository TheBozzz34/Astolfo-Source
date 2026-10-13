package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
   private static final long ab = 73828584685559L;

   public EntityBreakingFX(World var1, double var2, double var4, double var6, Item var8) {
      this(var1, var2, var4, var6, var8, 0);
   }

   public EntityBreakingFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, Item var14, int var15) {
      this(var1, var2, var4, var6, var14, var15);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += var8;
      this.motionY += var10;
      this.motionZ += var12;
   }

   public EntityBreakingFX(World var1, double var2, double var4, double var6, Item var8, int var9) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(var8, var9));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleGravity = Blocks.snow.blockParticleGravity;
      this.particleScale /= 2.0F;
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var12 = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float var13 = var12 + 0.015609375F;
      float var14 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float var15 = var14 + 0.015609375F;
      float var16 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         var12 = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         var13 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         var14 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         var15 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float var17 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var18 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
      float var19 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
      int var20 = this.getBrightnessForRender(var3);
      int var21 = var20 >> 16 & 65535;
      int var22 = var20 & 65535;
      var1.pos(var17 - var4 * var16 - var7 * var16, var18 - var5 * var16, var19 - var6 * var16 - var8 * var16)
         .tex(var12, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 - var4 * var16 + var7 * var16, var18 + var5 * var16, var19 - var6 * var16 + var8 * var16)
         .tex(var12, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 + var4 * var16 + var7 * var16, var18 + var5 * var16, var19 + var6 * var16 + var8 * var16)
         .tex(var13, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var21, var22)
         .endVertex();
      var1.pos(var17 + var4 * var16 - var7 * var16, var18 - var5 * var16, var19 + var6 * var16 - var8 * var16)
         .tex(var13, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var21, var22)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      private static final long a = 8362814653556L;

      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         int var19 = var15.length > 1 ? var15[1] : 0;
         return new EntityBreakingFX(var2, var3, var5, var7, var9, var11, var13, Item.getItemById(var15[0]), var19);
      }
   }

   public static class SlimeFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityBreakingFX(var2, var3, var5, var7, Items.slime_ball);
      }
   }

   public static class SnowballFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityBreakingFX(var2, var3, var5, var7, Items.snowball);
      }
   }
}

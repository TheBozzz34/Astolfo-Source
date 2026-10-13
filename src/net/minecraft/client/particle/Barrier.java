package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {
   private static final long ab = 54475275198557L;

   public Barrier(World var1, double var2, double var4, double var6, Item var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(var8));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 80;
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var11 = this.particleIcon.getMinU();
      float var12 = this.particleIcon.getMaxU();
      float var13 = this.particleIcon.getMinV();
      float var14 = this.particleIcon.getMaxV();
      float var16 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var17 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
      float var18 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
      int var19 = this.getBrightnessForRender(var3);
      int var20 = var19 >> 16 & 65535;
      int var21 = var19 & 65535;
      var1.pos(var16 - var4 * 0.5F - var7 * 0.5F, var17 - var5 * 0.5F, var18 - var6 * 0.5F - var8 * 0.5F)
         .tex(var12, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 - var4 * 0.5F + var7 * 0.5F, var17 + var5 * 0.5F, var18 - var6 * 0.5F + var8 * 0.5F)
         .tex(var12, var13)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 + var4 * 0.5F + var7 * 0.5F, var17 + var5 * 0.5F, var18 + var6 * 0.5F + var8 * 0.5F)
         .tex(var11, var13)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var20, var21)
         .endVertex();
      var1.pos(var16 + var4 * 0.5F - var7 * 0.5F, var17 - var5 * 0.5F, var18 + var6 * 0.5F - var8 * 0.5F)
         .tex(var11, var14)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var20, var21)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new Barrier(var2, var3, var5, var7, Item.getItemFromBlock(Blocks.barrier));
      }
   }
}

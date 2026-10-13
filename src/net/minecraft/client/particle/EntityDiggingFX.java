package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
   private IBlockState sourceState;
   private BlockPos sourcePos;
   private static final long ab = 135369271629960L;

   public EntityDiggingFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, IBlockState var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.sourceState = var14;
      this.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(var14));
      this.particleGravity = var14.getBlock().blockParticleGravity;
      this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
      this.particleScale /= 2.0F;
   }

   public EntityDiggingFX setBlockPos(BlockPos var1) {
      this.sourcePos = var1;
      if (this.sourceState.getBlock() == Blocks.grass) {
         return this;
      }

      int var5 = this.sourceState.getBlock().colorMultiplier(this.worldObj, var1);
      this.particleRed *= (var5 >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (var5 >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (var5 & 0xFF) / 255.0F;
      return this;
   }

   public EntityDiggingFX func_174845_l() {
      this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
      Block var4 = this.sourceState.getBlock();
      if (var4 == Blocks.grass) {
         return this;
      }

      int var5 = var4.getRenderColor(this.sourceState);
      this.particleRed *= (var5 >> 16 & 0xFF) / 255.0F;
      this.particleGreen *= (var5 >> 8 & 0xFF) / 255.0F;
      this.particleBlue *= (var5 & 0xFF) / 255.0F;
      return this;
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var13 = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float var14 = var13 + 0.015609375F;
      float var15 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float var16 = var15 + 0.015609375F;
      float var17 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         var13 = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         var14 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         var15 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         var16 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float var18 = (float)(this.prevPosX + (this.posX - this.prevPosX) * var3 - interpPosX);
      float var19 = (float)(this.prevPosY + (this.posY - this.prevPosY) * var3 - interpPosY);
      float var20 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * var3 - interpPosZ);
      int var21 = this.getBrightnessForRender(var3);
      int var22 = var21 >> 16 & 65535;
      int var23 = var21 & 65535;
      var1.pos(var18 - var4 * var17 - var7 * var17, var19 - var5 * var17, var20 - var6 * var17 - var8 * var17)
         .tex(var13, var16)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var22, var23)
         .endVertex();
      var1.pos(var18 - var4 * var17 + var7 * var17, var19 + var5 * var17, var20 - var6 * var17 + var8 * var17)
         .tex(var13, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var22, var23)
         .endVertex();
      var1.pos(var18 + var4 * var17 + var7 * var17, var19 + var5 * var17, var20 + var6 * var17 + var8 * var17)
         .tex(var14, var15)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var22, var23)
         .endVertex();
      var1.pos(var18 + var4 * var17 - var7 * var17, var19 - var5 * var17, var20 + var6 * var17 - var8 * var17)
         .tex(var14, var16)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(var22, var23)
         .endVertex();
   }

   @Override
   public int getBrightnessForRender(float var1) {
      int var5 = super.getBrightnessForRender(var1);
      int var6 = 0;
      if (this.worldObj.isBlockLoaded(this.sourcePos)) {
         var6 = this.worldObj.getCombinedLight(this.sourcePos, 0);
      }

      return var5 == 0 ? var6 : var5;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityDiggingFX(var2, var3, var5, var7, var9, var11, var13, Block.getStateById(var15[0])).func_174845_l();
      }
   }
}

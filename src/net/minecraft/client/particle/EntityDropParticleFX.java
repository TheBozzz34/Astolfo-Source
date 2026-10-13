package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDropParticleFX extends EntityFX {
   private Material materialType;
   private int bobTimer;
   private static final long ab = 43345816824559L;

   public EntityDropParticleFX(World var1, double var2, double var4, double var6, Material var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX = this.motionY = this.motionZ = 0.0;
      if (var8 == Material.water) {
         this.particleRed = 0.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 0.0F;
         this.particleBlue = 0.0F;
      }

      this.setParticleTextureIndex(113);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.materialType = var8;
      this.bobTimer = 40;
      this.particleMaxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
      this.motionX = this.motionY = this.motionZ = 0.0;
   }

   @Override
   public int getBrightnessForRender(float var1) {
      return this.materialType == Material.water ? super.getBrightnessForRender(var1) : 257;
   }

   @Override
   public float getBrightness(float var1) {
      return this.materialType == Material.water ? super.getBrightness(var1) : 1.0F;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.materialType == Material.water) {
         this.particleRed = 0.2F;
         this.particleGreen = 0.3F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (40 - this.bobTimer + 16);
         this.particleBlue = 4.0F / (40 - this.bobTimer + 8);
      }

      this.motionY = this.motionY - this.particleGravity;
      if (this.bobTimer-- > 0) {
         this.motionX *= 0.02;
         this.motionY *= 0.02;
         this.motionZ *= 0.02;
         this.setParticleTextureIndex(113);
      } else {
         this.setParticleTextureIndex(112);
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

      if (this.onGround) {
         if (this.materialType == Material.water) {
            this.setDead();
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
         } else {
            this.setParticleTextureIndex(114);
         }

         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }

      BlockPos var4 = new BlockPos(this);
      IBlockState var5 = this.worldObj.getBlockState(var4);
      Material var6 = var5.getBlock().getMaterial();
      if (var6.isLiquid() || var6.isSolid()) {
         double var7 = 0.0;
         if (var5.getBlock() instanceof BlockLiquid) {
            var7 = BlockLiquid.getLiquidHeightPercent(var5.getValue(BlockLiquid.LEVEL));
         }

         double var9 = MathHelper.floor_double(this.posY) + 1 - var7;
         if (this.posY < var9) {
            this.setDead();
         }
      }
   }

   public static class LavaFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityDropParticleFX(var2, var3, var5, var7, Material.lava);
      }
   }

   public static class WaterFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityDropParticleFX(var2, var3, var5, var7, Material.water);
      }
   }
}

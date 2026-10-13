package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityRainFX extends EntityFX {
   private static final long ab = 86618652444762L;
   private static final long hb = 7183974934530490387L;

   public EntityRainFX(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX *= 0.3F;
      this.motionY = Math.random() * 0.2F + 0.1F;
      this.motionZ *= 0.3F;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex((int)hb + this.rand.nextInt(4));
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY = this.motionY - this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

      if (this.onGround) {
         if (Math.random() < 0.5) {
            this.setDead();
         }

         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }

      BlockPos var4 = new BlockPos(this);
      IBlockState var5 = this.worldObj.getBlockState(var4);
      Block var6 = var5.getBlock();
      var6.setBlockBoundsBasedOnState(this.worldObj, var4);
      Material var7 = var5.getBlock().getMaterial();
      if (var7.isLiquid() || var7.isSolid()) {
         double var8 = 0.0;
         if (var5.getBlock() instanceof BlockLiquid) {
            var8 = 1.0F - BlockLiquid.getLiquidHeightPercent(var5.getValue(BlockLiquid.LEVEL));
         } else {
            var8 = var6.getBlockBoundsMaxY();
         }

         double var10 = MathHelper.floor_double(this.posY) + var8;
         if (this.posY < var10) {
            this.setDead();
         }
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityRainFX(var2, var3, var5, var7);
      }
   }
}

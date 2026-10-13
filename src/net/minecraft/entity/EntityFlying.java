package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   private static final long ab = 118800346035487L;

   public EntityFlying(World var1) {
      super(var1);
   }

   @Override
   public void fall(float var1, float var2) {
   }

   @Override
   public void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      if (this.isInWater()) {
         this.moveFlying(var1, var2, 0.02F);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.8F;
         this.motionY *= 0.8F;
         this.motionZ *= 0.8F;
      } else if (this.isInLava()) {
         this.moveFlying(var1, var2, 0.02F);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
      } else {
         float var6 = 0.91F;
         if (this.onGround) {
            var6 = this.worldObj
                  .getBlockState(
                     new BlockPos(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                     )
                  )
                  .getBlock()
                  .slipperiness
               * 0.91F;
         }

         float var7 = 0.16277136F / (var6 * var6 * var6);
         this.moveFlying(var1, var2, this.onGround ? 0.1F * var7 : 0.02F);
         var6 = 0.91F;
         if (this.onGround) {
            var6 = this.worldObj
                  .getBlockState(
                     new BlockPos(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                     )
                  )
                  .getBlock()
                  .slipperiness
               * 0.91F;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= var6;
         this.motionY *= var6;
         this.motionZ *= var6;
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var12 = this.posX - this.prevPosX;
      double var8 = this.posZ - this.prevPosZ;
      float var10 = MathHelper.sqrt_double(var12 * var12 + var8 * var8) * 4.0F;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (var10 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   @Override
   public boolean isOnLadder() {
      return false;
   }
}

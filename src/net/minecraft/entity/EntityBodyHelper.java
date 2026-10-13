package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public class EntityBodyHelper {
   private EntityLivingBase theLiving;
   private int rotationTickCounter;
   private float prevRenderYawHead;
   private static final long a = 39642410144149L;

   public EntityBodyHelper(EntityLivingBase var1) {
      this.theLiving = var1;
   }

   public void updateRenderAngles() {
      double var4 = this.theLiving.posX - this.theLiving.prevPosX;
      double var6 = this.theLiving.posZ - this.theLiving.prevPosZ;
      if (var4 * var4 + var6 * var6 > 2.5000003E-7F) {
         this.theLiving.renderYawOffset = this.theLiving.rotationYaw;
         this.theLiving.rotationYawHead = this.computeAngleWithBound(this.theLiving.renderYawOffset, this.theLiving.rotationYawHead, 75.0F);
         this.prevRenderYawHead = this.theLiving.rotationYawHead;
         this.rotationTickCounter = 0;
      } else {
         float var8 = 75.0F;
         if (Math.abs(this.theLiving.rotationYawHead - this.prevRenderYawHead) > 15.0F) {
            this.rotationTickCounter = 0;
            this.prevRenderYawHead = this.theLiving.rotationYawHead;
         } else {
            this.rotationTickCounter++;
            if (this.rotationTickCounter > 10) {
               var8 = Math.max(1.0F - (this.rotationTickCounter - 10) / 10.0F, 0.0F) * 75.0F;
            }
         }

         this.theLiving.renderYawOffset = this.computeAngleWithBound(this.theLiving.rotationYawHead, this.theLiving.renderYawOffset, var8);
      }
   }

   private float computeAngleWithBound(float var1, float var2, float var3) {
      float var7 = MathHelper.wrapAngleTo180_float(var1 - var2);
      if (var7 < -var3) {
         var7 = -var3;
      }

      if (var7 >= var3) {
         var7 = var3;
      }

      return var1 - var7;
   }
}

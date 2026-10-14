package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public class EntityBodyHelper {
   private EntityLivingBase theLiving;
   private int rotationTickCounter;
   private float prevRenderYawHead;

   public EntityBodyHelper(EntityLivingBase entityLivingBase) {
      this.theLiving = entityLivingBase;
   }

   public void updateRenderAngles() {
      double d = this.theLiving.posX - this.theLiving.prevPosX;
      double e = this.theLiving.posZ - this.theLiving.prevPosZ;
      if (d * d + e * e > 2.5000003E-7F) {
         this.theLiving.renderYawOffset = this.theLiving.rotationYaw;
         this.theLiving.rotationYawHead = this.computeAngleWithBound(this.theLiving.renderYawOffset, this.theLiving.rotationYawHead, 75.0F);
         this.prevRenderYawHead = this.theLiving.rotationYawHead;
         this.rotationTickCounter = 0;
      } else {
         float f = 75.0F;
         if (Math.abs(this.theLiving.rotationYawHead - this.prevRenderYawHead) > 15.0F) {
            this.rotationTickCounter = 0;
            this.prevRenderYawHead = this.theLiving.rotationYawHead;
         } else {
            this.rotationTickCounter++;
            if (this.rotationTickCounter > 10) {
               f = Math.max(1.0F - (this.rotationTickCounter - 10) / 10.0F, 0.0F) * 75.0F;
            }
         }

         this.theLiving.renderYawOffset = this.computeAngleWithBound(this.theLiving.rotationYawHead, this.theLiving.renderYawOffset, f);
      }
   }

   private float computeAngleWithBound(float f, float g, float h) {
      float f2 = MathHelper.wrapAngleTo180_float(f - g);
      if (f2 < -h) {
         f2 = -h;
      }

      if (f2 >= h) {
         f2 = h;
      }

      return f - f2;
   }
}

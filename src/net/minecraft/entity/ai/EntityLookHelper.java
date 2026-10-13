package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityLookHelper {
   private EntityLiving entity;
   private float deltaLookYaw;
   private float deltaLookPitch;
   private boolean isLooking;
   private double posX;
   private double posY;
   private double posZ;
   private static final long a = 95548415090289L;

   public EntityLookHelper(EntityLiving var1) {
      this.entity = var1;
   }

   public void setLookPositionWithEntity(Entity var1, float var2, float var3) {
      this.posX = var1.posX;
      if (var1 instanceof EntityLivingBase) {
         this.posY = var1.posY + var1.getEyeHeight();
      } else {
         this.posY = (var1.getEntityBoundingBox().minY + var1.getEntityBoundingBox().maxY) / 2.0;
      }

      this.posZ = var1.posZ;
      this.deltaLookYaw = var2;
      this.deltaLookPitch = var3;
      this.isLooking = true;
   }

   public void setLookPosition(double var1, double var3, double var5, float var7, float var8) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      this.deltaLookYaw = var7;
      this.deltaLookPitch = var8;
      this.isLooking = true;
   }

   public void onUpdateLook() {
      this.entity.rotationPitch = 0.0F;
      if (this.isLooking) {
         this.isLooking = false;
         double var4 = this.posX - this.entity.posX;
         double var6 = this.posY - (this.entity.posY + this.entity.getEyeHeight());
         double var8 = this.posZ - this.entity.posZ;
         double var10 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
         float var12 = (float)(MathHelper.atan2(var8, var4) * 180.0 / Math.PI) - 90.0F;
         float var13 = (float)(-(MathHelper.atan2(var6, var10) * 180.0 / Math.PI));
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, var13, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, var12, this.deltaLookYaw);
      } else {
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
      }

      float var14 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (!this.entity.getNavigator().noPath()) {
         if (var14 < -75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
         }

         if (var14 > 75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
         }
      }
   }

   private float updateRotation(float var1, float var2, float var3) {
      float var7 = MathHelper.wrapAngleTo180_float(var2 - var1);
      if (var7 > var3) {
         var7 = var3;
      }

      if (var7 < -var3) {
         var7 = -var3;
      }

      return var1 + var7;
   }

   public boolean getIsLooking() {
      return this.isLooking;
   }

   public double s() {
      return this.posX;
   }

   public double t() {
      return this.posY;
   }

   public double i() {
      return this.posZ;
   }
}

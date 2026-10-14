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

   public EntityLookHelper(EntityLiving entitylivingIn) {
      this.entity = entitylivingIn;
   }

   public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
      this.posX = entityIn.posX;
      if (entityIn instanceof EntityLivingBase) {
         this.posY = entityIn.posY + entityIn.getEyeHeight();
      } else {
         this.posY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0;
      }

      this.posZ = entityIn.posZ;
      this.deltaLookYaw = deltaYaw;
      this.deltaLookPitch = deltaPitch;
      this.isLooking = true;
   }

   public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.deltaLookYaw = deltaYaw;
      this.deltaLookPitch = deltaPitch;
      this.isLooking = true;
   }

   public void onUpdateLook() {
      this.entity.rotationPitch = 0.0F;
      if (this.isLooking) {
         this.isLooking = false;
         double d = this.posX - this.entity.posX;
         double e = this.posY - (this.entity.posY + this.entity.getEyeHeight());
         double d2 = this.posZ - this.entity.posZ;
         double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
         float f = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
         float g = (float)(-(MathHelper.atan2(e, d3) * 180.0 / Math.PI));
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, g, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
      } else {
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
      }

      float h = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (!this.entity.getNavigator().noPath()) {
         if (h < -75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
         }

         if (h > 75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
         }
      }
   }

   private float updateRotation(float f, float g, float h) {
      float f2 = MathHelper.wrapAngleTo180_float(g - f);
      if (f2 > h) {
         f2 = h;
      }

      if (f2 < -h) {
         f2 = -h;
      }

      return f + f2;
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

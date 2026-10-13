package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {
   public EntityLiving entity;
   public double posX;
   public double posY;
   public double posZ;
   public double speed;
   public boolean update;
   private static final long a = 54006948099728L;

   public EntityMoveHelper(EntityLiving var1) {
      this.entity = var1;
      this.posX = var1.posX;
      this.posY = var1.posY;
      this.posZ = var1.posZ;
   }

   public boolean isUpdating() {
      return this.update;
   }

   public double getSpeed() {
      return this.speed;
   }

   public void setMoveTo(double var1, double var3, double var5, double var7) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      this.speed = var7;
      this.update = true;
   }

   public void onUpdateMoveHelper() {
      this.entity.setMoveForward(0.0F);
      if (this.update) {
         this.update = false;
         int var4 = MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5);
         double var5 = this.posX - this.entity.posX;
         double var7 = this.posZ - this.entity.posZ;
         double var9 = this.posY - var4;
         double var11 = var5 * var5 + var9 * var9 + var7 * var7;
         if (var11 >= 2.5000003E-7F) {
            float var13 = (float)(MathHelper.atan2(var7, var5) * 180.0 / Math.PI) - 90.0F;
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, var13, 30.0F);
            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
            if (var9 > 0.0 && var5 * var5 + var7 * var7 < 1.0) {
               this.entity.getJumpHelper().setJumping();
            }
         }
      }
   }

   public float limitAngle(float var1, float var2, float var3) {
      float var7 = MathHelper.wrapAngleTo180_float(var2 - var1);
      if (var7 > var3) {
         var7 = var3;
      }

      if (var7 < -var3) {
         var7 = -var3;
      }

      float var8 = var1 + var7;
      if (var8 < 0.0F) {
         var8 += 360.0F;
      } else if (var8 > 360.0F) {
         var8 -= 360.0F;
      }

      return var8;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }
}

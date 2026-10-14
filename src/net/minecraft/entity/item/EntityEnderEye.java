package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
   private double targetX;
   private double targetY;
   private double targetZ;
   private int despawnTimer;
   private boolean shatterOrDrop;
   private static final long ab = 105959308491390L;

   public EntityEnderEye(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   public EntityEnderEye(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.despawnTimer = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(x, y, z);
   }

   public void moveTowards(BlockPos blockPos) {
      double d = blockPos.getX();
      int i = blockPos.getY();
      double e = blockPos.getZ();
      double d2 = d - this.posX;
      double d3 = e - this.posZ;
      float f = MathHelper.sqrt_double(d2 * d2 + d3 * d3);
      if (f > 12.0F) {
         this.targetX = this.posX + d2 / f * 12.0;
         this.targetZ = this.posZ + d3 / f * 12.0;
         this.targetY = this.posY + 8.0;
      } else {
         this.targetX = d;
         this.targetY = i;
         this.targetZ = e;
      }

      this.despawnTimer = 0;
      this.shatterOrDrop = this.rand.nextInt(5) > 0;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt_double(x * x + z * z);
         this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, f) * 180.0 / Math.PI);
      }
   }

   @Override
   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.onUpdate();
      this.posX = this.posX + this.motionX;
      this.posY = this.posY + this.motionY;
      this.posZ = this.posZ + this.motionZ;
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
      this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);

      while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      if (!this.worldObj.isRemote) {
         double d = this.targetX - this.posX;
         double e = this.targetZ - this.posZ;
         float g = (float)Math.sqrt(d * d + e * e);
         float h = (float)MathHelper.atan2(e, d);
         double d2 = f + (g - f) * 0.0025;
         if (g < 1.0F) {
            d2 *= 0.8;
            this.motionY *= 0.8;
         }

         this.motionX = Math.cos(h) * d2;
         this.motionZ = Math.sin(h) * d2;
         if (this.posY < this.targetY) {
            this.motionY = this.motionY + (1.0 - this.motionY) * 0.015F;
         } else {
            this.motionY = this.motionY + (-1.0 - this.motionY) * 0.015F;
         }
      }

      float f2 = 0.25F;
      if (this.isInWater()) {
         for (int i = 0; i < 4; i++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.WATER_BUBBLE,
                  this.posX - this.motionX * f2,
                  this.posY - this.motionY * f2,
                  this.posZ - this.motionZ * f2,
                  this.motionX,
                  this.motionY,
                  this.motionZ
               );
         }
      } else {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.PORTAL,
               this.posX - this.motionX * f2 + this.rand.nextDouble() * 0.6 - 0.3,
               this.posY - this.motionY * f2 - 0.5,
               this.posZ - this.motionZ * f2 + this.rand.nextDouble() * 0.6 - 0.3,
               this.motionX,
               this.motionY,
               this.motionZ
            );
      }

      if (!this.worldObj.isRemote) {
         this.setPosition(this.posX, this.posY, this.posZ);
         this.despawnTimer++;
         if (this.despawnTimer > 80 && !this.worldObj.isRemote) {
            this.setDead();
            if (this.shatterOrDrop) {
               this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Items.ender_eye)));
            } else {
               this.worldObj.playAuxSFX(2003, new BlockPos(this), 0);
            }
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public float getBrightness(float partialTicks) {
      return 1.0F;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }
}

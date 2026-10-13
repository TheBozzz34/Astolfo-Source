package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   public int fuse;
   private EntityLivingBase tntPlacedBy;
   private static final long ab = 82581249015974L;
   private static final long fb = -3476068937056124848L;

   public EntityTNTPrimed(World var1) {
      super(var1);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
   }

   public EntityTNTPrimed(World var1, double var2, double var4, double var6, EntityLivingBase var8) {
      this(var1);
      this.setPosition(var2, var4, var6);
      float var11 = (float)(Math.random() * Math.PI * 2.0);
      this.motionX = -((float)Math.sin(var11)) * 0.02F;
      this.motionY = 0.2F;
      this.motionZ = -((float)Math.cos(var11)) * 0.02F;
      this.fuse = (int)fb;
      this.prevPosX = var2;
      this.prevPosY = var4;
      this.prevPosZ = var6;
      this.tntPlacedBy = var8;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.04F;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
         this.motionY *= -0.5;
      }

      if (this.fuse-- <= 0) {
         this.setDead();
         if (!this.worldObj.isRemote) {
            this.explode();
         }
      } else {
         this.handleWaterMovement();
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
      }
   }

   private void explode() {
      float var1 = 4.0F;
      this.worldObj.createExplosion(this, this.posX, this.posY + this.height / 16.0F, this.posZ, var1, true);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setByte("Fuse", (byte)this.fuse);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.fuse = var1.getByte("Fuse");
   }

   public EntityLivingBase getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }
}

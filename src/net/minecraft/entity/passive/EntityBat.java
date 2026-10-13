package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
   private BlockPos spawnPosition;
   private static final long ab = 24803502417455L;

   public EntityBat(World var1) {
      super(var1);
      this.setSize(0.5F, 0.9F);
      this.setIsBatHanging(true);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public float getSoundVolume() {
      return 0.1F;
   }

   @Override
   public float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Override
   public String getLivingSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.bat.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.bat.death";
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void collideWithEntity(Entity var1) {
   }

   @Override
   public void collideWithNearbyEntities() {
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0);
   }

   public boolean getIsBatHanging() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setIsBatHanging(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -2));
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getIsBatHanging()) {
         this.motionX = this.motionY = this.motionZ = 0.0;
         this.posY = MathHelper.floor_double(this.posY) + 1.0 - this.height;
      } else {
         this.motionY *= 0.6F;
      }
   }

   @Override
   public void updateAITasks() {
      super.updateAITasks();
      BlockPos var4 = new BlockPos(this);
      BlockPos var5 = var4.up();
      if (this.getIsBatHanging()) {
         if (!this.worldObj.getBlockState(var5).getBlock().isNormalCube()) {
            this.setIsBatHanging(false);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, var4, 0);
         } else {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = this.rand.nextInt(360);
            }

            if (this.worldObj.getClosestPlayerToEntity(this, 4.0) != null) {
               this.setIsBatHanging(false);
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, var4, 0);
            }
         }
      } else {
         if (this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
            this.spawnPosition = null;
         }

         if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.distanceSq((int)this.posX, (int)this.posY, (int)this.posZ) < 4.0) {
            this.spawnPosition = new BlockPos(
               (int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
               (int)this.posY + this.rand.nextInt(6) - 2,
               (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7)
            );
         }

         double var6 = this.spawnPosition.getX() + 0.5 - this.posX;
         double var8 = this.spawnPosition.getY() + 0.1 - this.posY;
         double var10 = this.spawnPosition.getZ() + 0.5 - this.posZ;
         this.motionX = this.motionX + (Math.signum(var6) * 0.5 - this.motionX) * 0.1F;
         this.motionY = this.motionY + (Math.signum(var8) * 0.7F - this.motionY) * 0.1F;
         this.motionZ = this.motionZ + (Math.signum(var10) * 0.5 - this.motionZ) * 0.1F;
         float var12 = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0F;
         float var13 = MathHelper.wrapAngleTo180_float(var12 - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += var13;
         if (this.rand.nextInt(100) == 0 && this.worldObj.getBlockState(var5).getBlock().isNormalCube()) {
            this.setIsBatHanging(true);
         }
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void fall(float var1, float var2) {
   }

   @Override
   public void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
   }

   @Override
   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (!this.worldObj.isRemote && this.getIsBatHanging()) {
         this.setIsBatHanging(false);
      }

      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.dataWatcher.updateObject(16, var1.getByte("BatFlags"));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setByte("BatFlags", this.dataWatcher.getWatchableObjectByte(16));
   }

   @Override
   public boolean getCanSpawnHere() {
      BlockPos var4 = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (var4.getY() >= this.worldObj.getSeaLevel()) {
         return false;
      }

      int var5 = this.worldObj.getLightFromNeighbors(var4);
      byte var6 = 4;
      if (this.isDateAroundHalloween(this.worldObj.getCurrentDate())) {
         var6 = 7;
      } else if (this.rand.nextBoolean()) {
         return false;
      }

      return var5 > this.rand.nextInt(var6) ? false : super.getCanSpawnHere();
   }

   private boolean isDateAroundHalloween(Calendar var1) {
      return var1.get(2) + 1 == 10 && var1.get(5) >= 20 || var1.get(2) + 1 == 11 && var1.get(5) <= 3;
   }

   @Override
   public float getEyeHeight() {
      return this.height / 2.0F;
   }
}

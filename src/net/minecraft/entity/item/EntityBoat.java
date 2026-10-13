package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private boolean isBoatEmpty = true;
   private double speedMultiplier = 0.07;
   private int boatPosRotationIncrements;
   private double boatX;
   private double boatY;
   private double boatZ;
   private double boatYaw;
   private double boatPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private static final long ab = 38520532670222L;

   public EntityBoat(World var1) {
      super(var1);
      this.preventEntitySpawning = true;
      this.setSize(1.5F, 0.6F);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity var1) {
      return var1.getEntityBoundingBox();
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getEntityBoundingBox();
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityBoat(World var1, double var2, double var4, double var6) {
      this(var1);
      this.setPosition(var2, var4, var6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = var2;
      this.prevPosY = var4;
      this.prevPosZ = var6;
   }

   @Override
   public double getMountedYOffset() {
      return -0.3;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (!this.worldObj.isRemote && !this.isDead) {
         if (this.riddenByEntity != null && this.riddenByEntity == var1.getEntity() && var1 instanceof EntityDamageSourceIndirect) {
            return false;
         }

         this.setForwardDirection(-this.getForwardDirection());
         this.setTimeSinceHit(10);
         this.setDamageTaken(this.getDamageTaken() + var2 * 10.0F);
         this.setBeenAttacked();
         boolean var6 = var1.getEntity() instanceof EntityPlayer && ((EntityPlayer)var1.getEntity()).capabilities.isCreativeMode;
         if (var6 || this.getDamageTaken() > 40.0F) {
            if (this.riddenByEntity != null) {
               this.riddenByEntity.mountEntity(this);
            }

            if (!var6 && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
               this.dropItemWithOffset(Items.boat, 1, 0.0F);
            }

            this.setDead();
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      if (var10 && this.riddenByEntity != null) {
         this.prevPosX = this.posX = var1;
         this.prevPosY = this.posY = var3;
         this.prevPosZ = this.posZ = var5;
         this.rotationYaw = var7;
         this.rotationPitch = var8;
         this.boatPosRotationIncrements = 0;
         this.setPosition(var1, var3, var5);
         this.motionX = this.velocityX = 0.0;
         this.motionY = this.velocityY = 0.0;
         this.motionZ = this.velocityZ = 0.0;
      } else {
         if (this.isBoatEmpty) {
            this.boatPosRotationIncrements = var9 + 5;
         } else {
            double var14 = var1 - this.posX;
            double var16 = var3 - this.posY;
            double var18 = var5 - this.posZ;
            double var20 = var14 * var14 + var16 * var16 + var18 * var18;
            if (var20 <= 1.0) {
               return;
            }

            this.boatPosRotationIncrements = 3;
         }

         this.boatX = var1;
         this.boatY = var3;
         this.boatZ = var5;
         this.boatYaw = var7;
         this.boatPitch = var8;
         this.motionX = this.velocityX;
         this.motionY = this.velocityY;
         this.motionZ = this.velocityZ;
      }
   }

   @Override
   public void setVelocity(double var1, double var3, double var5) {
      this.velocityX = this.motionX = var1;
      this.velocityY = this.motionY = var3;
      this.velocityZ = this.motionZ = var5;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      byte var4 = 5;
      double var5 = 0.0;

      for (int var7 = 0; var7 < var4; var7++) {
         double var8 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (var7 + 0) / var4 - 0.125;
         double var10 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (var7 + 1) / var4 - 0.125;
         AxisAlignedBB var12 = new AxisAlignedBB(
            this.getEntityBoundingBox().minX, var8, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, var10, this.getEntityBoundingBox().maxZ
         );
         if (this.worldObj.isAABBInMaterial(var12, Material.water)) {
            var5 += 1.0 / var4;
         }
      }

      double var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (var22 > 0.2975) {
         double var9 = Math.cos(this.rotationYaw * Math.PI / 180.0);
         double var11 = Math.sin(this.rotationYaw * Math.PI / 180.0);

         for (int var13 = 0; var13 < 1.0 + var22 * 60.0; var13++) {
            double var14 = this.rand.nextFloat() * 2.0F - 1.0F;
            double var16 = (this.rand.nextInt(2) * 2 - 1) * 0.7;
            if (this.rand.nextBoolean()) {
               double var18 = this.posX - var9 * var14 * 0.8 + var11 * var16;
               double var20 = this.posZ - var11 * var14 * 0.8 - var9 * var16;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, var18, this.posY - 0.125, var20, this.motionX, this.motionY, this.motionZ);
            } else {
               double var46 = this.posX + var9 + var11 * var14 * 0.7;
               double var47 = this.posZ + var11 - var9 * var14 * 0.7;
               this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, var46, this.posY - 0.125, var47, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

      if (this.worldObj.isRemote && this.isBoatEmpty) {
         if (this.boatPosRotationIncrements > 0) {
            double var26 = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
            double var34 = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
            double var39 = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;
            double var43 = MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + var43 / this.boatPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements);
            this.boatPosRotationIncrements--;
            this.setPosition(var26, var34, var39);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            double var27 = this.posX + this.motionX;
            double var35 = this.posY + this.motionY;
            double var40 = this.posZ + this.motionZ;
            this.setPosition(var27, var35, var40);
            if (this.onGround) {
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
            }

            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }
      } else {
         if (var5 < 1.0) {
            double var23 = var5 * 2.0 - 1.0;
            this.motionY += 0.04F * var23;
         } else {
            if (this.motionY < 0.0) {
               this.motionY /= 2.0;
            }

            this.motionY += 0.007F;
         }

         if (this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase var24 = (EntityLivingBase)this.riddenByEntity;
            float var28 = this.riddenByEntity.rotationYaw + -var24.moveStrafing * 90.0F;
            this.motionX = this.motionX + -Math.sin(var28 * (float) Math.PI / 180.0F) * this.speedMultiplier * var24.moveForward * 0.05F;
            this.motionZ = this.motionZ + Math.cos(var28 * (float) Math.PI / 180.0F) * this.speedMultiplier * var24.moveForward * 0.05F;
         }

         double var25 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var25 > 0.35) {
            double var29 = 0.35 / var25;
            this.motionX *= var29;
            this.motionZ *= var29;
            var25 = 0.35;
         }

         if (var25 > var22 && this.speedMultiplier < 0.35) {
            this.speedMultiplier = this.speedMultiplier + (0.35 - this.speedMultiplier) / 35.0;
            if (this.speedMultiplier > 0.35) {
               this.speedMultiplier = 0.35;
            }
         } else {
            this.speedMultiplier = this.speedMultiplier - (this.speedMultiplier - 0.07) / 35.0;
            if (this.speedMultiplier < 0.07) {
               this.speedMultiplier = 0.07;
            }
         }

         for (int var30 = 0; var30 < 4; var30++) {
            int var36 = MathHelper.floor_double(this.posX + (var30 % 2 - 0.5) * 0.8);
            int var37 = MathHelper.floor_double(this.posZ + (var30 / 2 - 0.5) * 0.8);

            for (int var41 = 0; var41 < 2; var41++) {
               int var15 = MathHelper.floor_double(this.posY) + var41;
               BlockPos var44 = new BlockPos(var36, var15, var37);
               Block var17 = this.worldObj.getBlockState(var44).getBlock();
               if (var17 == Blocks.snow_layer) {
                  this.worldObj.setBlockToAir(var44);
                  this.isCollidedHorizontally = false;
               } else if (var17 == Blocks.waterlily) {
                  this.worldObj.destroyBlock(var44, true);
                  this.isCollidedHorizontally = false;
               }
            }
         }

         if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (this.isCollidedHorizontally && var22 > 0.2975) {
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int var31 = 0; var31 < 3; var31++) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for (int var32 = 0; var32 < 2; var32++) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }
         } else {
            this.motionX *= 0.99F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.99F;
         }

         this.rotationPitch = 0.0F;
         double var33 = this.rotationYaw;
         double var38 = this.prevPosX - this.posX;
         double var42 = this.prevPosZ - this.posZ;
         if (var38 * var38 + var42 * var42 > 0.001) {
            var33 = (float)(MathHelper.atan2(var42, var38) * 180.0 / Math.PI);
         }

         double var45 = MathHelper.wrapAngleTo180_double(var33 - this.rotationYaw);
         if (var45 > 20.0) {
            var45 = 20.0;
         }

         if (var45 < -20.0) {
            var45 = -20.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + var45);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (!this.worldObj.isRemote) {
            List var19 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F));
            if (var19 != null && !var19.isEmpty()) {
               for (int var48 = 0; var48 < var19.size(); var48++) {
                  Entity var21 = (Entity)var19.get(var48);
                  if (var21 != this.riddenByEntity && var21.canBePushed() && var21 instanceof EntityBoat) {
                     var21.applyEntityCollision(this);
                  }
               }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
               this.riddenByEntity = null;
            }
         }
      }
   }

   @Override
   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         double var4 = Math.cos(this.rotationYaw * Math.PI / 180.0) * 0.4;
         double var6 = Math.sin(this.rotationYaw * Math.PI / 180.0) * 0.4;
         this.riddenByEntity.setPosition(this.posX + var4, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + var6);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != var1) {
         return true;
      }

      if (!this.worldObj.isRemote) {
         var1.mountEntity(this);
      }

      return true;
   }

   @Override
   public void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
      if (var3) {
         if (this.fallDistance > 3.0F) {
            this.fall(this.fallDistance, 1.0F);
            if (!this.worldObj.isRemote && !this.isDead) {
               this.setDead();
               if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  for (int var9 = 0; var9 < 3; var9++) {
                     this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                  }

                  for (int var10 = 0; var10 < 2; var10++) {
                     this.dropItemWithOffset(Items.stick, 1, 0.0F);
                  }
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.worldObj.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.water && var1 < 0.0) {
         this.fallDistance = (float)(this.fallDistance - var1);
      }
   }

   public void setDamageTaken(float var1) {
      this.dataWatcher.updateObject(19, var1);
   }

   public float getDamageTaken() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setTimeSinceHit(int var1) {
      this.dataWatcher.updateObject(17, var1);
   }

   public int getTimeSinceHit() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setForwardDirection(int var1) {
      this.dataWatcher.updateObject(18, var1);
   }

   public int getForwardDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setIsBoatEmpty(boolean var1) {
      this.isBoatEmpty = var1;
   }
}

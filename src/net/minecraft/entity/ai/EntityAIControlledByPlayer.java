package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class EntityAIControlledByPlayer extends EntityAIBase {
   private final EntityLiving thisEntity;
   private final float maxSpeed;
   private float currentSpeed;
   private boolean speedBoosted;
   private int speedBoostTime;
   private int maxSpeedBoostTime;
   private static final long b = 139447762787264L;

   public EntityAIControlledByPlayer(EntityLiving var1, float var2) {
      this.thisEntity = var1;
      this.maxSpeed = var2;
      this.setMutexBits(7);
   }

   @Override
   public void startExecuting() {
      this.currentSpeed = 0.0F;
   }

   @Override
   public void resetTask() {
      this.speedBoosted = false;
      this.currentSpeed = 0.0F;
   }

   @Override
   public boolean shouldExecute() {
      return this.thisEntity.isEntityAlive()
         && this.thisEntity.riddenByEntity != null
         && this.thisEntity.riddenByEntity instanceof EntityPlayer
         && (this.speedBoosted || this.thisEntity.canBeSteered());
   }

   @Override
   public void updateTask() {
      EntityPlayer var5 = (EntityPlayer)this.thisEntity.riddenByEntity;
      EntityCreature var6 = (EntityCreature)this.thisEntity;
      float var7 = MathHelper.wrapAngleTo180_float(var5.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
      if (var7 > 5.0F) {
         var7 = 5.0F;
      }

      if (var7 < -5.0F) {
         var7 = -5.0F;
      }

      this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + var7);
      if (this.currentSpeed < this.maxSpeed) {
         this.currentSpeed = this.currentSpeed + (this.maxSpeed - this.currentSpeed) * 0.01F;
      }

      if (this.currentSpeed > this.maxSpeed) {
         this.currentSpeed = this.maxSpeed;
      }

      int var8 = MathHelper.floor_double(this.thisEntity.posX);
      int var9 = MathHelper.floor_double(this.thisEntity.posY);
      int var10 = MathHelper.floor_double(this.thisEntity.posZ);
      float var11 = this.currentSpeed;
      if (this.speedBoosted) {
         if (this.speedBoostTime++ > this.maxSpeedBoostTime) {
            this.speedBoosted = false;
         }

         var11 += var11 * 1.15F * MathHelper.sin((float)this.speedBoostTime / this.maxSpeedBoostTime * (float) Math.PI);
      }

      float var12 = 0.91F;
      if (this.thisEntity.onGround) {
         var12 = this.thisEntity
               .worldObj
               .getBlockState(new BlockPos(MathHelper.floor_float(var8), MathHelper.floor_float(var9) - 1, MathHelper.floor_float(var10)))
               .getBlock()
               .slipperiness
            * 0.91F;
      }

      float var13 = 0.16277136F / (var12 * var12 * var12);
      float var14 = MathHelper.sin(var6.rotationYaw * (float) Math.PI / 180.0F);
      float var15 = MathHelper.cos(var6.rotationYaw * (float) Math.PI / 180.0F);
      float var16 = var6.getAIMoveSpeed() * var13;
      float var17 = Math.max(var11, 1.0F);
      var17 = var16 / var17;
      float var18 = var11 * var17;
      float var19 = -(var18 * var14);
      float var20 = var18 * var15;
      if (MathHelper.abs(var19) > MathHelper.abs(var20)) {
         if (var19 < 0.0F) {
            var19 -= this.thisEntity.width / 2.0F;
         }

         if (var19 > 0.0F) {
            var19 += this.thisEntity.width / 2.0F;
         }

         var20 = 0.0F;
      } else {
         var19 = 0.0F;
         if (var20 < 0.0F) {
            var20 -= this.thisEntity.width / 2.0F;
         }

         if (var20 > 0.0F) {
            var20 += this.thisEntity.width / 2.0F;
         }
      }

      int var21 = MathHelper.floor_double(this.thisEntity.posX + var19);
      int var22 = MathHelper.floor_double(this.thisEntity.posZ + var20);
      int var23 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      int var24 = MathHelper.floor_float(this.thisEntity.height + var5.height + 1.0F);
      int var25 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      if (var8 != var21 || var10 != var22) {
         Block var26 = this.thisEntity.worldObj.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
         boolean var27 = !this.isStairOrSlab(var26)
            && (
               var26.getMaterial() != Material.air
                  || !this.isStairOrSlab(this.thisEntity.worldObj.getBlockState(new BlockPos(var8, var9 - 1, var10)).getBlock())
            );
         if (var27
            && 0 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, var21, var9, var22, var23, var24, var25, false, false, true)
            && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, var8, var9 + 1, var10, var23, var24, var25, false, false, true)
            && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, var21, var9 + 1, var22, var23, var24, var25, false, false, true)
            )
          {
            var6.getJumpHelper().setJumping();
         }
      }

      if (!var5.capabilities.isCreativeMode && this.currentSpeed >= this.maxSpeed * 0.5F && this.thisEntity.getRNG().nextFloat() < 0.006F && !this.speedBoosted
         )
       {
         ItemStack var29 = var5.getHeldItem();
         if (var29 != null && var29.getItem() == Items.carrot_on_a_stick) {
            var29.damageItem(1, var5);
            if (var29.stackSize == 0) {
               ItemStack var30 = new ItemStack(Items.fishing_rod);
               var30.setTagCompound(var29.getTagCompound());
               var5.inventory.mainInventory[var5.inventory.currentItem] = var30;
            }
         }
      }

      this.thisEntity.moveEntityWithHeading(0.0F, var11);
   }

   private boolean isStairOrSlab(Block var1) {
      return var1 instanceof BlockStairs || var1 instanceof BlockSlab;
   }

   public boolean isSpeedBoosted() {
      return this.speedBoosted;
   }

   public void boostSpeed() {
      this.speedBoosted = true;
      this.speedBoostTime = 0;
      this.maxSpeedBoostTime = this.thisEntity.getRNG().nextInt(841) + 140;
   }

   public boolean isControlledByPlayer() {
      return !this.isSpeedBoosted() && this.currentSpeed > this.maxSpeed * 0.3F;
   }
}

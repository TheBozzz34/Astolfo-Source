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

   public EntityAIControlledByPlayer(EntityLiving entitylivingIn, float maxspeed) {
      this.thisEntity = entitylivingIn;
      this.maxSpeed = maxspeed;
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
      EntityPlayer entityPlayer = (EntityPlayer)this.thisEntity.riddenByEntity;
      EntityCreature entityCreature = (EntityCreature)this.thisEntity;
      float f = MathHelper.wrapAngleTo180_float(entityPlayer.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
      if (f > 5.0F) {
         f = 5.0F;
      }

      if (f < -5.0F) {
         f = -5.0F;
      }

      this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + f);
      if (this.currentSpeed < this.maxSpeed) {
         this.currentSpeed = this.currentSpeed + (this.maxSpeed - this.currentSpeed) * 0.01F;
      }

      if (this.currentSpeed > this.maxSpeed) {
         this.currentSpeed = this.maxSpeed;
      }

      int i = MathHelper.floor_double(this.thisEntity.posX);
      int j = MathHelper.floor_double(this.thisEntity.posY);
      int k = MathHelper.floor_double(this.thisEntity.posZ);
      float g = this.currentSpeed;
      if (this.speedBoosted) {
         if (this.speedBoostTime++ > this.maxSpeedBoostTime) {
            this.speedBoosted = false;
         }

         g += g * 1.15F * MathHelper.sin((float)this.speedBoostTime / this.maxSpeedBoostTime * (float) Math.PI);
      }

      float h = 0.91F;
      if (this.thisEntity.onGround) {
         h = this.thisEntity
               .worldObj
               .getBlockState(new BlockPos(MathHelper.floor_float(i), MathHelper.floor_float(j) - 1, MathHelper.floor_float(k)))
               .getBlock()
               .slipperiness
            * 0.91F;
      }

      float f2 = 0.16277136F / (h * h * h);
      float f3 = MathHelper.sin(entityCreature.rotationYaw * (float) Math.PI / 180.0F);
      float f4 = MathHelper.cos(entityCreature.rotationYaw * (float) Math.PI / 180.0F);
      float f5 = entityCreature.getAIMoveSpeed() * f2;
      float f6 = Math.max(g, 1.0F);
      f6 = f5 / f6;
      float f7 = g * f6;
      float f8 = -(f7 * f3);
      float f9 = f7 * f4;
      if (MathHelper.abs(f8) > MathHelper.abs(f9)) {
         if (f8 < 0.0F) {
            f8 -= this.thisEntity.width / 2.0F;
         }

         if (f8 > 0.0F) {
            f8 += this.thisEntity.width / 2.0F;
         }

         f9 = 0.0F;
      } else {
         f8 = 0.0F;
         if (f9 < 0.0F) {
            f9 -= this.thisEntity.width / 2.0F;
         }

         if (f9 > 0.0F) {
            f9 += this.thisEntity.width / 2.0F;
         }
      }

      int l = MathHelper.floor_double(this.thisEntity.posX + f8);
      int m = MathHelper.floor_double(this.thisEntity.posZ + f9);
      int n = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      int i2 = MathHelper.floor_float(this.thisEntity.height + entityPlayer.height + 1.0F);
      int i3 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      if (i != l || k != m) {
         Block block = this.thisEntity.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
         boolean bl = !this.isStairOrSlab(block)
            && (
               block.getMaterial() != Material.air
                  || !this.isStairOrSlab(this.thisEntity.worldObj.getBlockState(new BlockPos(i, j - 1, k)).getBlock())
            );
         if (bl
            && 0 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, l, j, m, n, i2, i3, false, false, true)
            && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, i, j + 1, k, n, i2, i3, false, false, true)
            && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, l, j + 1, m, n, i2, i3, false, false, true)
            )
          {
            entityCreature.getJumpHelper().setJumping();
         }
      }

      if (!entityPlayer.capabilities.isCreativeMode && this.currentSpeed >= this.maxSpeed * 0.5F && this.thisEntity.getRNG().nextFloat() < 0.006F && !this.speedBoosted
         )
       {
         ItemStack itemStack = entityPlayer.getHeldItem();
         if (itemStack != null && itemStack.getItem() == Items.carrot_on_a_stick) {
            itemStack.damageItem(1, entityPlayer);
            if (itemStack.stackSize == 0) {
               ItemStack itemStack2 = new ItemStack(Items.fishing_rod);
               itemStack2.setTagCompound(itemStack.getTagCompound());
               entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = itemStack2;
            }
         }
      }

      this.thisEntity.moveEntityWithHeading(0.0F, g);
   }

   private boolean isStairOrSlab(Block blockIn) {
      return blockIn instanceof BlockStairs || blockIn instanceof BlockSlab;
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

package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   public Block spawnableBlock = Blocks.grass;
   private int inLove;
   private EntityPlayer playerInLove;
   private static final long eb = 120772434163378L;

   public EntityAnimal(World var1) {
      super(var1);
   }

   @Override
   public void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         this.inLove--;
         if (this.inLove % 10 == 0) {
            double var4 = this.rand.nextGaussian() * 0.02;
            double var6 = this.rand.nextGaussian() * 0.02;
            double var8 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  var4,
                  var6,
                  var8
               );
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.inLove = 0;
      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public float getBlockPathWeight(BlockPos var1) {
      return this.worldObj.getBlockState(var1.down()).getBlock() == Blocks.grass ? 10.0F : this.worldObj.getLightBrightness(var1) - 0.5F;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("InLove", this.inLove);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.inLove = var1.getInteger("InLove");
   }

   @Override
   public boolean getCanSpawnHere() {
      int var4 = MathHelper.floor_double(this.posX);
      int var5 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int var6 = MathHelper.floor_double(this.posZ);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      return this.worldObj.getBlockState(var7.down()).getBlock() == this.spawnableBlock && this.worldObj.getLight(var7) > 8 && super.getCanSpawnHere();
   }

   @Override
   public int getTalkInterval() {
      return 120;
   }

   @Override
   public boolean canDespawn() {
      return false;
   }

   @Override
   public int getExperiencePoints(EntityPlayer var1) {
      return 1 + this.worldObj.rand.nextInt(3);
   }

   public boolean isBreedingItem(ItemStack var1) {
      return var1 == null ? false : var1.getItem() == Items.wheat;
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null) {
         if (this.isBreedingItem(var5) && this.getGrowingAge() == 0 && this.inLove <= 0) {
            this.consumeItemFromStack(var1, var5);
            this.setInLove(var1);
            return true;
         }

         if (this.isChild() && this.isBreedingItem(var5)) {
            this.consumeItemFromStack(var1, var5);
            this.func_175501_a((int)(-this.getGrowingAge() / 20 * 0.1F), true);
            return true;
         }
      }

      return super.interact(var1);
   }

   public void consumeItemFromStack(EntityPlayer var1, ItemStack var2) {
      if (!var1.capabilities.isCreativeMode) {
         var2.stackSize--;
         if (var2.stackSize <= 0) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
         }
      }
   }

   public void setInLove(EntityPlayer var1) {
      this.inLove = 600;
      this.playerInLove = var1;
      this.worldObj.setEntityState(this, (byte)18);
   }

   public EntityPlayer getPlayerInLove() {
      return this.playerInLove;
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   public boolean canMateWith(EntityAnimal var1) {
      return var1 == this ? false : (var1.getClass() != this.getClass() ? false : this.isInLove() && var1.isInLove());
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 18) {
         for (int var5 = 0; var5 < 7; var5++) {
            double var6 = this.rand.nextGaussian() * 0.02;
            double var8 = this.rand.nextGaussian() * 0.02;
            double var10 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  var6,
                  var8,
                  var10
               );
         }
      } else {
         super.handleStatusUpdate(var1);
      }
   }
}

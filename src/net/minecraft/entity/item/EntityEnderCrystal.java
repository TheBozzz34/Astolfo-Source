package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class EntityEnderCrystal extends Entity {
   public int innerRotation;
   public int health;
   private static final long ab = 98582297868862L;

   public EntityEnderCrystal(World var1) {
      super(var1);
      this.preventEntitySpawning = true;
      this.setSize(2.0F, 2.0F);
      this.health = 5;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EntityEnderCrystal(World var1, double var2, double var4, double var6) {
      this(var1);
      this.setPosition(var2, var4, var6);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(8, this.health);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.innerRotation++;
      this.dataWatcher.updateObject(8, this.health);
      int var4 = MathHelper.floor_double(this.posX);
      int var5 = MathHelper.floor_double(this.posY);
      int var6 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.provider instanceof WorldProviderEnd && this.worldObj.getBlockState(new BlockPos(var4, var5, var6)).getBlock() != Blocks.fire) {
         this.worldObj.setBlockState(new BlockPos(var4, var5, var6), Blocks.fire.getDefaultState());
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.health = 0;
         if (this.health <= 0) {
            this.setDead();
            if (!this.worldObj.isRemote) {
               this.worldObj.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F, true);
            }
         }
      }

      return true;
   }
}

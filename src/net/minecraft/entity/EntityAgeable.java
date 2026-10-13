package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   public int growingAge;
   public int field_175502_b;
   public int field_175503_c;
   private float ageWidth = -1.0F;
   private float ageHeight;
   private static final long ab = 140034969748022L;

   public EntityAgeable(World var1) {
      super(var1);
   }

   public abstract EntityAgeable createChild(EntityAgeable var1);

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.spawn_egg) {
         if (!this.worldObj.isRemote) {
            Class var6 = EntityList.getClassFromID(var5.getMetadata());
            if (var6 != null && this.getClass() == var6) {
               EntityAgeable var7 = this.createChild(this);
               if (var7 != null) {
                  var7.setGrowingAge(-24000);
                  var7.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                  this.worldObj.spawnEntityInWorld(var7);
                  if (var5.hasDisplayName()) {
                     var7.setCustomNameTag(var5.getDisplayName());
                  }

                  if (!var1.capabilities.isCreativeMode) {
                     var5.stackSize--;
                     if (var5.stackSize <= 0) {
                        var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(12, (byte)0);
   }

   public int getGrowingAge() {
      return this.worldObj.isRemote ? this.dataWatcher.getWatchableObjectByte(12) : this.growingAge;
   }

   public void func_175501_a(int var1, boolean var2) {
      int var6 = this.getGrowingAge();
      int var7 = var6;
      var6 += var1 * 20;
      if (var6 > 0) {
         var6 = 0;
         if (var7 < 0) {
            this.onGrowingAdult();
         }
      }

      int var8 = var6 - var7;
      this.setGrowingAge(var6);
      if (var2) {
         this.field_175502_b += var8;
         if (this.field_175503_c == 0) {
            this.field_175503_c = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.field_175502_b);
      }
   }

   public void addGrowth(int var1) {
      this.func_175501_a(var1, false);
   }

   public void setGrowingAge(int var1) {
      this.dataWatcher.updateObject(12, (byte)MathHelper.clamp_int(var1, -1, 1));
      this.growingAge = var1;
      this.setScaleForAge(this.isChild());
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("Age", this.getGrowingAge());
      var1.setInteger("ForcedAge", this.field_175502_b);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setGrowingAge(var1.getInteger("Age"));
      this.field_175502_b = var1.getInteger("ForcedAge");
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.worldObj.isRemote) {
         if (this.field_175503_c > 0) {
            if (this.field_175503_c % 4 == 0) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.VILLAGER_HAPPY,
                     this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     this.posY + 0.5 + this.rand.nextFloat() * this.height,
                     this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            this.field_175503_c--;
         }

         this.setScaleForAge(this.isChild());
      } else {
         int var4 = this.getGrowingAge();
         if (var4 < 0) {
            this.setGrowingAge(++var4);
            if (var4 == 0) {
               this.onGrowingAdult();
            }
         } else if (var4 > 0) {
            this.setGrowingAge(--var4);
         }
      }
   }

   public void onGrowingAdult() {
   }

   @Override
   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public void setScaleForAge(boolean var1) {
      this.setScale(var1 ? 0.5F : 1.0F);
   }

   @Override
   public final void setSize(float var1, float var2) {
      boolean var6 = this.ageWidth > 0.0F;
      this.ageWidth = var1;
      this.ageHeight = var2;
      if (!var6) {
         this.setScale(1.0F);
      }
   }

   public final void setScale(float var1) {
      super.setSize(this.ageWidth * var1, this.ageHeight * var1);
   }
}

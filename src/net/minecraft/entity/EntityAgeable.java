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

   public EntityAgeable(World worldIn) {
      super(worldIn);
   }

   public abstract EntityAgeable createChild(EntityAgeable ageable);

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.spawn_egg) {
         if (!this.worldObj.isRemote) {
            Class class2 = EntityList.getClassFromID(itemStack.getMetadata());
            if (class2 != null && this.getClass() == class2) {
               EntityAgeable entityAgeable = this.createChild(this);
               if (entityAgeable != null) {
                  entityAgeable.setGrowingAge(-24000);
                  entityAgeable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                  this.worldObj.spawnEntityInWorld(entityAgeable);
                  if (itemStack.hasDisplayName()) {
                     entityAgeable.setCustomNameTag(itemStack.getDisplayName());
                  }

                  if (!player.capabilities.isCreativeMode) {
                     itemStack.stackSize--;
                     if (itemStack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
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

   public void func_175501_a(int i, boolean bl) {
      int j = this.getGrowingAge();
      int k = j;
      j += i * 20;
      if (j > 0) {
         j = 0;
         if (k < 0) {
            this.onGrowingAdult();
         }
      }

      int l = j - k;
      this.setGrowingAge(j);
      if (bl) {
         this.field_175502_b += l;
         if (this.field_175503_c == 0) {
            this.field_175503_c = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.field_175502_b);
      }
   }

   public void addGrowth(int growth) {
      this.func_175501_a(growth, false);
   }

   public void setGrowingAge(int age) {
      this.dataWatcher.updateObject(12, (byte)MathHelper.clamp_int(age, -1, 1));
      this.growingAge = age;
      this.setScaleForAge(this.isChild());
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Age", this.getGrowingAge());
      tagCompound.setInteger("ForcedAge", this.field_175502_b);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setGrowingAge(tagCompund.getInteger("Age"));
      this.field_175502_b = tagCompund.getInteger("ForcedAge");
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
         int i = this.getGrowingAge();
         if (i < 0) {
            this.setGrowingAge(++i);
            if (i == 0) {
               this.onGrowingAdult();
            }
         } else if (i > 0) {
            this.setGrowingAge(--i);
         }
      }
   }

   public void onGrowingAdult() {
   }

   @Override
   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public void setScaleForAge(boolean bl) {
      this.setScale(bl ? 0.5F : 1.0F);
   }

   @Override
   public final void setSize(float width, float height) {
      boolean bl = this.ageWidth > 0.0F;
      this.ageWidth = width;
      this.ageHeight = height;
      if (!bl) {
         this.setScale(1.0F);
      }
   }

   public final void setScale(float scale) {
      super.setSize(this.ageWidth * scale, this.ageHeight * scale);
   }
}

package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable {
   private ItemStack potionDamage;

   public EntityPotion(World worldIn) {
      super(worldIn);
   }

   public EntityPotion(World worldIn, EntityLivingBase throwerIn, int meta) {
      this(worldIn, throwerIn, new ItemStack(Items.potionitem, 1, meta));
   }

   public EntityPotion(World worldIn, EntityLivingBase throwerIn, ItemStack potionDamageIn) {
      super(worldIn, throwerIn);
      this.potionDamage = potionDamageIn;
   }

   public EntityPotion(World worldIn, double x, double y, double z, int i) {
      this(worldIn, x, y, z, new ItemStack(Items.potionitem, 1, i));
   }

   public EntityPotion(World worldIn, double x, double y, double z, ItemStack potionDamageIn) {
      super(worldIn, x, y, z);
      this.potionDamage = potionDamageIn;
   }

   @Override
   public float getGravityVelocity() {
      return 0.05F;
   }

   @Override
   public float getVelocity() {
      return 0.5F;
   }

   @Override
   public float getInaccuracy() {
      return -20.0F;
   }

   public void setPotionDamage(int potionId) {
      if (this.potionDamage == null) {
         this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
      }

      this.potionDamage.setItemDamage(potionId);
   }

   public int getPotionDamage() {
      if (this.potionDamage == null) {
         this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
      }

      return this.potionDamage.getMetadata();
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      if (!this.worldObj.isRemote) {
         List list = Items.potionitem.getEffects(this.potionDamage);
         if (list != null && !list.isEmpty()) {
            AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().expand(4.0, 2.0, 4.0);
            List list2 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);
            if (!list2.isEmpty()) {
               for (Object object : list2) {
                  EntityLivingBase entityLivingBase = (EntityLivingBase)object;
                  double d = this.getDistanceSqToEntity(entityLivingBase);
                  if (d < 16.0) {
                     double e = 1.0 - Math.sqrt(d) / 4.0;
                     if (entityLivingBase == movingObjectPosition.entityHit) {
                        e = 1.0;
                     }

                     for (Object object2 : list) {
                        PotionEffect potionEffect = (PotionEffect)object2;
                        int i = potionEffect.getPotionID();
                        if (Potion.potionTypes[i].isInstant()) {
                           Potion.potionTypes[i].affectEntity(this, this.getThrower(), entityLivingBase, potionEffect.getAmplifier(), e);
                        } else {
                           int j = (int)(e * potionEffect.getDuration() + 0.5);
                           if (j > 20) {
                              entityLivingBase.addPotionEffect(new PotionEffect(i, j, potionEffect.getAmplifier()));
                           }
                        }
                     }
                  }
               }
            }
         }

         this.worldObj.playAuxSFX(2002, new BlockPos(this), this.getPotionDamage());
         this.setDead();
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("Potion", 10)) {
         this.potionDamage = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("Potion"));
      } else {
         this.setPotionDamage(tagCompund.getInteger("potionValue"));
      }

      if (this.potionDamage == null) {
         this.setDead();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      if (this.potionDamage != null) {
         tagCompound.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
      }
   }
}

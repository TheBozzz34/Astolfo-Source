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
   private static final long ab = 27472290109006L;

   public EntityPotion(World var1) {
      super(var1);
   }

   public EntityPotion(World var1, EntityLivingBase var2, int var3) {
      this(var1, var2, new ItemStack(Items.potionitem, 1, var3));
   }

   public EntityPotion(World var1, EntityLivingBase var2, ItemStack var3) {
      super(var1, var2);
      this.potionDamage = var3;
   }

   public EntityPotion(World var1, double var2, double var4, double var6, int var8) {
      this(var1, var2, var4, var6, new ItemStack(Items.potionitem, 1, var8));
   }

   public EntityPotion(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1, var2, var4, var6);
      this.potionDamage = var8;
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

   public void setPotionDamage(int var1) {
      if (this.potionDamage == null) {
         this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
      }

      this.potionDamage.setItemDamage(var1);
   }

   public int getPotionDamage() {
      if (this.potionDamage == null) {
         this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
      }

      return this.potionDamage.getMetadata();
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (!this.worldObj.isRemote) {
         List var5 = Items.potionitem.getEffects(this.potionDamage);
         if (var5 != null && !var5.isEmpty()) {
            AxisAlignedBB var6 = this.getEntityBoundingBox().expand(4.0, 2.0, 4.0);
            List var7 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, var6);
            if (!var7.isEmpty()) {
               for (Object var9$raw : var7) {
                  EntityLivingBase var9 = (EntityLivingBase)var9$raw;
                  double var10 = this.getDistanceSqToEntity(var9);
                  if (var10 < 16.0) {
                     double var12 = 1.0 - Math.sqrt(var10) / 4.0;
                     if (var9 == var1.entityHit) {
                        var12 = 1.0;
                     }

                     for (Object var15$raw : var5) {
                        PotionEffect var15 = (PotionEffect)var15$raw;
                        int var16 = var15.getPotionID();
                        if (Potion.potionTypes[var16].isInstant()) {
                           Potion.potionTypes[var16].affectEntity(this, this.getThrower(), var9, var15.getAmplifier(), var12);
                        } else {
                           int var17 = (int)(var12 * var15.getDuration() + 0.5);
                           if (var17 > 20) {
                              var9.addPotionEffect(new PotionEffect(var16, var17, var15.getAmplifier()));
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
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("Potion", 10)) {
         this.potionDamage = ItemStack.loadItemStackFromNBT(var1.getCompoundTag("Potion"));
      } else {
         this.setPotionDamage(var1.getInteger("potionValue"));
      }

      if (this.potionDamage == null) {
         this.setDead();
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      if (this.potionDamage != null) {
         var1.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
      }
   }
}

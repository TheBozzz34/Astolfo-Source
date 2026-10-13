package net.minecraft.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class EntitySelectors {
   public static final Predicate<Entity> selectAnything = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1.isEntityAlive();
      }
   };
   public static final Predicate<Entity> IS_STANDALONE = new Predicate<Entity>() {
      private static final long a = 69461571890257L;

      public boolean apply(Entity var1) {
         return var1.isEntityAlive() && var1.riddenByEntity == null && var1.ridingEntity == null;
      }
   };
   public static final Predicate<Entity> selectInventories = new Predicate<Entity>() {
      private static final long a = 8202236515401L;

      public boolean apply(Entity var1) {
         return var1 instanceof IInventory && var1.isEntityAlive();
      }
   };
   public static final Predicate<Entity> NOT_SPECTATING = new Predicate<Entity>() {
      private static final long a = 80711639279533L;

      public boolean apply(Entity var1) {
         return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).isSpectator();
      }
   };

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack armor;
      private static final long a = 33427564747212L;

      public ArmoredMob(ItemStack var1) {
         this.armor = var1;
      }

      public boolean apply(Entity var1) {
         if (!var1.isEntityAlive()) {
            return false;
         }

         if (!(var1 instanceof EntityLivingBase)) {
            return false;
         }

         EntityLivingBase var5 = (EntityLivingBase)var1;
         return var5.getEquipmentInSlot(EntityLiving.getArmorPosition(this.armor)) != null
            ? false
            : (var5 instanceof EntityLiving ? ((EntityLiving)var5).canPickUpLoot() : (var5 instanceof EntityArmorStand ? true : var5 instanceof EntityPlayer));
      }
   }
}

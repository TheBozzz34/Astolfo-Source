package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {
   private static final Random enchantmentRand = new Random();
   private static final EnchantmentHelper.ModifierDamage enchantmentModifierDamage = new EnchantmentHelper.ModifierDamage();
   private static final EnchantmentHelper.ModifierLiving enchantmentModifierLiving = new EnchantmentHelper.ModifierLiving();
   private static final EnchantmentHelper.HurtIterator ENCHANTMENT_ITERATOR_HURT = new EnchantmentHelper.HurtIterator();
   private static final EnchantmentHelper.DamageIterator ENCHANTMENT_ITERATOR_DAMAGE = new EnchantmentHelper.DamageIterator();
   private static final long a = 125056557435934L;

   public static int getEnchantmentLevel(int var0, ItemStack var1) {
      if (var1 == null) {
         return 0;
      }

      NBTTagList var5 = var1.getEnchantmentTagList();
      if (var5 == null) {
         return 0;
      }

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         short var7 = var5.getCompoundTagAt(var6).getShort("id");
         short var8 = var5.getCompoundTagAt(var6).getShort("lvl");
         if (var7 == var0) {
            return var8;
         }
      }

      return 0;
   }

   public static Map<Integer, Integer> getEnchantments(ItemStack var0) {
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      NBTTagList var5 = var0.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(var0) : var0.getEnchantmentTagList();
      if (var5 != null) {
         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            short var7 = var5.getCompoundTagAt(var6).getShort("id");
            short var8 = var5.getCompoundTagAt(var6).getShort("lvl");
            var4.put(Integer.valueOf(var7), Integer.valueOf(var8));
         }
      }

      return var4;
   }

   public static void setEnchantments(Map<Integer, Integer> var0, ItemStack var1) {
      NBTTagList var5 = new NBTTagList();

      for (int var7 : var0.keySet()) {
         Enchantment var8 = Enchantment.getEnchantmentById(var7);
         if (var8 != null) {
            NBTTagCompound var9 = new NBTTagCompound();
            var9.setShort("id", (short)var7);
            var9.setShort("lvl", (short)((Integer)var0.get(var7)).intValue());
            var5.appendTag(var9);
            if (var1.getItem() == Items.enchanted_book) {
               Items.enchanted_book.addEnchantment(var1, new EnchantmentData(var8, (Integer)var0.get(var7)));
            }
         }
      }

      if (var5.tagCount() > 0) {
         if (var1.getItem() != Items.enchanted_book) {
            var1.setTagInfo("ench", var5);
         }
      } else if (var1.hasTagCompound()) {
         var1.getTagCompound().removeTag("ench");
      }
   }

   public static int getMaxEnchantmentLevel(int var0, ItemStack[] var1) {
      if (var1 == null) {
         return 0;
      }

      int var5 = 0;

      for (ItemStack var9 : var1) {
         int var10 = getEnchantmentLevel(var0, var9);
         if (var10 > var5) {
            var5 = var10;
         }
      }

      return var5;
   }

   private static void applyEnchantmentModifier(EnchantmentHelper.IModifier var0, ItemStack var1) {
      if (var1 != null) {
         NBTTagList var5 = var1.getEnchantmentTagList();
         if (var5 != null) {
            for (int var6 = 0; var6 < var5.tagCount(); var6++) {
               short var7 = var5.getCompoundTagAt(var6).getShort("id");
               short var8 = var5.getCompoundTagAt(var6).getShort("lvl");
               if (Enchantment.getEnchantmentById(var7) != null) {
                  var0.calculateModifier(Enchantment.getEnchantmentById(var7), var8);
               }
            }
         }
      }
   }

   private static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier var0, ItemStack[] var1) {
      for (ItemStack var8 : var1) {
         applyEnchantmentModifier(var0, var8);
      }
   }

   public static int getEnchantmentModifierDamage(ItemStack[] var0, DamageSource var1) {
      enchantmentModifierDamage.damageModifier = 0;
      enchantmentModifierDamage.source = var1;
      applyEnchantmentModifierArray(enchantmentModifierDamage, var0);
      if (enchantmentModifierDamage.damageModifier > 25) {
         enchantmentModifierDamage.damageModifier = 25;
      } else if (enchantmentModifierDamage.damageModifier < 0) {
         enchantmentModifierDamage.damageModifier = 0;
      }

      return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
   }

   public static float getModifierForCreature(ItemStack var0, EnumCreatureAttribute var1) {
      enchantmentModifierLiving.livingModifier = 0.0F;
      enchantmentModifierLiving.entityLiving = var1;
      applyEnchantmentModifier(enchantmentModifierLiving, var0);
      return enchantmentModifierLiving.livingModifier;
   }

   public static void applyThornEnchantments(EntityLivingBase var0, Entity var1) {
      ENCHANTMENT_ITERATOR_HURT.attacker = var1;
      ENCHANTMENT_ITERATOR_HURT.user = var0;
      if (var0 != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_HURT, var0.getInventory());
      }

      if (var1 instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_HURT, var0.getHeldItem());
      }
   }

   public static void applyArthropodEnchantments(EntityLivingBase var0, Entity var1) {
      ENCHANTMENT_ITERATOR_DAMAGE.user = var0;
      ENCHANTMENT_ITERATOR_DAMAGE.target = var1;
      if (var0 != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_DAMAGE, var0.getInventory());
      }

      if (var0 instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_DAMAGE, var0.getHeldItem());
      }
   }

   public static int getKnockbackModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.knockback.effectId, var0.getHeldItem());
   }

   public static int getFireAspectModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.fireAspect.effectId, var0.getHeldItem());
   }

   public static int getRespiration(Entity var0) {
      return getMaxEnchantmentLevel(Enchantment.respiration.effectId, var0.getInventory());
   }

   public static int getDepthStriderModifier(Entity var0) {
      return getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, var0.getInventory());
   }

   public static int getEfficiencyModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.efficiency.effectId, var0.getHeldItem());
   }

   public static boolean getSilkTouchModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.silkTouch.effectId, var0.getHeldItem()) > 0;
   }

   public static int getFortuneModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.fortune.effectId, var0.getHeldItem());
   }

   public static int getLuckOfSeaModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, var0.getHeldItem());
   }

   public static int getLureModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.lure.effectId, var0.getHeldItem());
   }

   public static int getLootingModifier(EntityLivingBase var0) {
      return getEnchantmentLevel(Enchantment.looting.effectId, var0.getHeldItem());
   }

   public static boolean getAquaAffinityModifier(EntityLivingBase var0) {
      return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, var0.getInventory()) > 0;
   }

   public static ItemStack getEnchantedItem(Enchantment var0, EntityLivingBase var1) {
      for (ItemStack var8 : var1.getInventory()) {
         if (var8 != null && getEnchantmentLevel(var0.effectId, var8) > 0) {
            return var8;
         }
      }

      return null;
   }

   public static int calcItemStackEnchantability(Random var0, int var1, int var2, ItemStack var3) {
      Item var7 = var3.getItem();
      int var8 = var7.getItemEnchantability();
      if (var8 <= 0) {
         return 0;
      }

      if (var2 > 15) {
         var2 = 15;
      }

      int var9 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
      return var1 == 0 ? Math.max(var9 / 3, 1) : (var1 == 1 ? var9 * 2 / 3 + 1 : Math.max(var9, var2 * 2));
   }

   public static ItemStack addRandomEnchantment(Random var0, ItemStack var1, int var2) {
      List var6 = buildEnchantmentList(var0, var1, var2);
      boolean var7 = var1.getItem() == Items.book;
      if (var7) {
         var1.setItem(Items.enchanted_book);
      }

      if (var6 != null) {
         for (Object var9$raw : var6) {
            EnchantmentData var9 = (EnchantmentData)var9$raw;
            if (var7) {
               Items.enchanted_book.addEnchantment(var1, var9);
            } else {
               var1.addEnchantment(var9.enchantmentobj, var9.enchantmentLevel);
            }
         }
      }

      return var1;
   }

   public static List<EnchantmentData> buildEnchantmentList(Random var0, ItemStack var1, int var2) {
      Item var6 = var1.getItem();
      int var7 = var6.getItemEnchantability();
      if (var7 <= 0) {
         return null;
      }

      var7 /= 2;
      var7 = 1 + var0.nextInt((var7 >> 1) + 1) + var0.nextInt((var7 >> 1) + 1);
      int var8 = var7 + var2;
      float var9 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
      int var10 = (int)(var8 * (1.0F + var9) + 0.5F);
      if (var10 < 1) {
         var10 = 1;
      }

      ArrayList var11 = null;
      Map var12 = mapEnchantmentData(var10, var1);
      if (var12 != null && !var12.isEmpty()) {
         EnchantmentData var13 = (net.minecraft.enchantment.EnchantmentData)(WeightedRandom.getRandomItem(var0, var12.values()));
         if (var13 != null) {
            var11 = Lists.newArrayList();
            var11.add(var13);

            for (int var14 = var10; var0.nextInt(50) <= var14; var14 >>= 1) {
               Iterator var15 = var12.keySet().iterator();

               while (var15.hasNext()) {
                  Integer var16 = (Integer)var15.next();
                  boolean var17 = true;

                  for (Object var19$raw : var11) {
                     EnchantmentData var19 = (EnchantmentData)var19$raw;
                     if (!var19.enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(var16))) {
                        var17 = false;
                        break;
                     }
                  }

                  if (!var17) {
                     var15.remove();
                  }
               }

               if (!var12.isEmpty()) {
                  EnchantmentData var22 = (net.minecraft.enchantment.EnchantmentData)(WeightedRandom.getRandomItem(var0, var12.values()));
                  var11.add(var22);
               }
            }
         }
      }

      return var11;
   }

   public static Map<Integer, EnchantmentData> mapEnchantmentData(int var0, ItemStack var1) {
      Item var5 = var1.getItem();
      HashMap var6 = null;
      boolean var7 = var1.getItem() == Items.book;

      for (Enchantment var11 : Enchantment.enchantmentsBookList) {
         if (var11 != null && (var11.type.canEnchantItem(var5) || var7)) {
            for (int var12 = var11.getMinLevel(); var12 <= var11.getMaxLevel(); var12++) {
               if (var0 >= var11.getMinEnchantability(var12) && var0 <= var11.getMaxEnchantability(var12)) {
                  if (var6 == null) {
                     var6 = Maps.newHashMap();
                  }

                  var6.put(var11.effectId, new EnchantmentData(var11, var12));
               }
            }
         }
      }

      return var6;
   }

   public static final class DamageIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity target;

      public DamageIterator() {
      }

      @Override
      public void calculateModifier(Enchantment var1, int var2) {
         var1.onEntityDamaged(this.user, this.target, var2);
      }
   }

   public static final class HurtIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity attacker;

      public HurtIterator() {
      }

      @Override
      public void calculateModifier(Enchantment var1, int var2) {
         var1.onUserHurt(this.user, this.attacker, var2);
      }
   }

   public interface IModifier {
      void calculateModifier(Enchantment var1, int var2);
   }

   public static final class ModifierDamage implements EnchantmentHelper.IModifier {
      public int damageModifier;
      public DamageSource source;

      public ModifierDamage() {
      }

      @Override
      public void calculateModifier(Enchantment var1, int var2) {
         this.damageModifier = this.damageModifier + var1.calcModifierDamage(var2, this.source);
      }
   }

   public static final class ModifierLiving implements EnchantmentHelper.IModifier {
      public float livingModifier;
      public EnumCreatureAttribute entityLiving;

      public ModifierLiving() {
      }

      @Override
      public void calculateModifier(Enchantment var1, int var2) {
         this.livingModifier = this.livingModifier + var1.calcDamageByCreature(var2, this.entityLiving);
      }
   }
}

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

   public static int getEnchantmentLevel(int enchID, ItemStack stack) {
      if (stack == null) {
         return 0;
      }

      NBTTagList nbtTagList = stack.getEnchantmentTagList();
      if (nbtTagList == null) {
         return 0;
      }

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         short s = nbtTagList.getCompoundTagAt(i).getShort("id");
         short s2 = nbtTagList.getCompoundTagAt(i).getShort("lvl");
         if (s == enchID) {
            return s2;
         }
      }

      return 0;
   }

   public static Map<Integer, Integer> getEnchantments(ItemStack stack) {
      LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
      NBTTagList nbtTagList = stack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(stack) : stack.getEnchantmentTagList();
      if (nbtTagList != null) {
         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            short s = nbtTagList.getCompoundTagAt(i).getShort("id");
            short s2 = nbtTagList.getCompoundTagAt(i).getShort("lvl");
            linkedHashMap.put(Integer.valueOf(s), Integer.valueOf(s2));
         }
      }

      return linkedHashMap;
   }

   public static void setEnchantments(Map<Integer, Integer> enchMap, ItemStack stack) {
      NBTTagList nbtTagList = new NBTTagList();

      for (int i : enchMap.keySet()) {
         Enchantment enchantment = Enchantment.getEnchantmentById(i);
         if (enchantment != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setShort("id", (short)i);
            nbtTagCompound.setShort("lvl", (short)((Integer)enchMap.get(i)).intValue());
            nbtTagList.appendTag(nbtTagCompound);
            if (stack.getItem() == Items.enchanted_book) {
               Items.enchanted_book.addEnchantment(stack, new EnchantmentData(enchantment, (Integer)enchMap.get(i)));
            }
         }
      }

      if (nbtTagList.tagCount() > 0) {
         if (stack.getItem() != Items.enchanted_book) {
            stack.setTagInfo("ench", nbtTagList);
         }
      } else if (stack.hasTagCompound()) {
         stack.getTagCompound().removeTag("ench");
      }
   }

   public static int getMaxEnchantmentLevel(int enchID, ItemStack[] stacks) {
      if (stacks == null) {
         return 0;
      }

      int i = 0;

      for (ItemStack itemStack : stacks) {
         int j = getEnchantmentLevel(enchID, itemStack);
         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   private static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack stack) {
      if (stack != null) {
         NBTTagList nbtTagList = stack.getEnchantmentTagList();
         if (nbtTagList != null) {
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               short s = nbtTagList.getCompoundTagAt(i).getShort("id");
               short s2 = nbtTagList.getCompoundTagAt(i).getShort("lvl");
               if (Enchantment.getEnchantmentById(s) != null) {
                  modifier.calculateModifier(Enchantment.getEnchantmentById(s), s2);
               }
            }
         }
      }
   }

   private static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier modifier, ItemStack[] stacks) {
      for (ItemStack itemStack : stacks) {
         applyEnchantmentModifier(modifier, itemStack);
      }
   }

   public static int getEnchantmentModifierDamage(ItemStack[] stacks, DamageSource source) {
      enchantmentModifierDamage.damageModifier = 0;
      enchantmentModifierDamage.source = source;
      applyEnchantmentModifierArray(enchantmentModifierDamage, stacks);
      if (enchantmentModifierDamage.damageModifier > 25) {
         enchantmentModifierDamage.damageModifier = 25;
      } else if (enchantmentModifierDamage.damageModifier < 0) {
         enchantmentModifierDamage.damageModifier = 0;
      }

      return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
   }

   public static float getModifierForCreature(ItemStack itemStack, EnumCreatureAttribute enumCreatureAttribute) {
      enchantmentModifierLiving.livingModifier = 0.0F;
      enchantmentModifierLiving.entityLiving = enumCreatureAttribute;
      applyEnchantmentModifier(enchantmentModifierLiving, itemStack);
      return enchantmentModifierLiving.livingModifier;
   }

   public static void applyThornEnchantments(EntityLivingBase entityLivingBase, Entity entity) {
      ENCHANTMENT_ITERATOR_HURT.attacker = entity;
      ENCHANTMENT_ITERATOR_HURT.user = entityLivingBase;
      if (entityLivingBase != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_HURT, entityLivingBase.getInventory());
      }

      if (entity instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_HURT, entityLivingBase.getHeldItem());
      }
   }

   public static void applyArthropodEnchantments(EntityLivingBase entityLivingBase, Entity entity) {
      ENCHANTMENT_ITERATOR_DAMAGE.user = entityLivingBase;
      ENCHANTMENT_ITERATOR_DAMAGE.target = entity;
      if (entityLivingBase != null) {
         applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_DAMAGE, entityLivingBase.getInventory());
      }

      if (entityLivingBase instanceof EntityPlayer) {
         applyEnchantmentModifier(ENCHANTMENT_ITERATOR_DAMAGE, entityLivingBase.getHeldItem());
      }
   }

   public static int getKnockbackModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.knockback.effectId, player.getHeldItem());
   }

   public static int getFireAspectModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.fireAspect.effectId, player.getHeldItem());
   }

   public static int getRespiration(Entity player) {
      return getMaxEnchantmentLevel(Enchantment.respiration.effectId, player.getInventory());
   }

   public static int getDepthStriderModifier(Entity player) {
      return getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, player.getInventory());
   }

   public static int getEfficiencyModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.efficiency.effectId, player.getHeldItem());
   }

   public static boolean getSilkTouchModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.silkTouch.effectId, player.getHeldItem()) > 0;
   }

   public static int getFortuneModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem());
   }

   public static int getLuckOfSeaModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, player.getHeldItem());
   }

   public static int getLureModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.lure.effectId, player.getHeldItem());
   }

   public static int getLootingModifier(EntityLivingBase player) {
      return getEnchantmentLevel(Enchantment.looting.effectId, player.getHeldItem());
   }

   public static boolean getAquaAffinityModifier(EntityLivingBase player) {
      return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, player.getInventory()) > 0;
   }

   public static ItemStack getEnchantedItem(Enchantment enchantment, EntityLivingBase entityLivingBase) {
      for (ItemStack itemStack : entityLivingBase.getInventory()) {
         if (itemStack != null && getEnchantmentLevel(enchantment.effectId, itemStack) > 0) {
            return itemStack;
         }
      }

      return null;
   }

   public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
      Item item = stack.getItem();
      int i = item.getItemEnchantability();
      if (i <= 0) {
         return 0;
      }

      if (power > 15) {
         power = 15;
      }

      int j = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
      return enchantNum == 0 ? Math.max(j / 3, 1) : (enchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, power * 2));
   }

   public static ItemStack addRandomEnchantment(Random random2, ItemStack itemStack, int i) {
      List list = buildEnchantmentList(random2, itemStack, i);
      boolean bl = itemStack.getItem() == Items.book;
      if (bl) {
         itemStack.setItem(Items.enchanted_book);
      }

      if (list != null) {
         for (Object object : list) {
            EnchantmentData enchantmentData = (EnchantmentData)object;
            if (bl) {
               Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
            } else {
               itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
            }
         }
      }

      return itemStack;
   }

   public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int i) {
      Item item = itemStackIn.getItem();
      int j = item.getItemEnchantability();
      if (j <= 0) {
         return null;
      }

      j /= 2;
      j = 1 + randomIn.nextInt((j >> 1) + 1) + randomIn.nextInt((j >> 1) + 1);
      int k = j + i;
      float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
      int l = (int)(k * (1.0F + f) + 0.5F);
      if (l < 1) {
         l = 1;
      }

      ArrayList arrayList = null;
      Map map = mapEnchantmentData(l, itemStackIn);
      if (map != null && !map.isEmpty()) {
         EnchantmentData enchantmentData = (net.minecraft.enchantment.EnchantmentData)(WeightedRandom.getRandomItem(randomIn, map.values()));
         if (enchantmentData != null) {
            arrayList = Lists.newArrayList();
            arrayList.add(enchantmentData);

            for (int m = l; randomIn.nextInt(50) <= m; m >>= 1) {
               Iterator iterator2 = map.keySet().iterator();

               while (iterator2.hasNext()) {
                  Integer integer = (Integer)iterator2.next();
                  boolean bl = true;

                  for (Object object : arrayList) {
                     EnchantmentData enchantmentData2 = (EnchantmentData)object;
                     if (!enchantmentData2.enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(integer))) {
                        bl = false;
                        break;
                     }
                  }

                  if (!bl) {
                     iterator2.remove();
                  }
               }

               if (!map.isEmpty()) {
                  EnchantmentData enchantmentData3 = (net.minecraft.enchantment.EnchantmentData)(WeightedRandom.getRandomItem(randomIn, map.values()));
                  arrayList.add(enchantmentData3);
               }
            }
         }
      }

      return arrayList;
   }

   public static Map<Integer, EnchantmentData> mapEnchantmentData(int i, ItemStack itemStack) {
      Item item = itemStack.getItem();
      HashMap hashMap = null;
      boolean bl = itemStack.getItem() == Items.book;

      for (Enchantment enchantment : Enchantment.enchantmentsBookList) {
         if (enchantment != null && (enchantment.type.canEnchantItem(item) || bl)) {
            for (int j = enchantment.getMinLevel(); j <= enchantment.getMaxLevel(); j++) {
               if (i >= enchantment.getMinEnchantability(j) && i <= enchantment.getMaxEnchantability(j)) {
                  if (hashMap == null) {
                     hashMap = Maps.newHashMap();
                  }

                  hashMap.put(enchantment.effectId, new EnchantmentData(enchantment, j));
               }
            }
         }
      }

      return hashMap;
   }

   public static final class DamageIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity target;

      public DamageIterator() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         enchantmentIn.onEntityDamaged(this.user, this.target, enchantmentLevel);
      }
   }

   public static final class HurtIterator implements EnchantmentHelper.IModifier {
      public EntityLivingBase user;
      public Entity attacker;

      public HurtIterator() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         enchantmentIn.onUserHurt(this.user, this.attacker, enchantmentLevel);
      }
   }

   public interface IModifier {
      void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel);
   }

   public static final class ModifierDamage implements EnchantmentHelper.IModifier {
      public int damageModifier;
      public DamageSource source;

      public ModifierDamage() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         this.damageModifier = this.damageModifier + enchantmentIn.calcModifierDamage(enchantmentLevel, this.source);
      }
   }

   public static final class ModifierLiving implements EnchantmentHelper.IModifier {
      public float livingModifier;
      public EnumCreatureAttribute entityLiving;

      public ModifierLiving() {
      }

      @Override
      public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
         this.livingModifier = this.livingModifier + enchantmentIn.calcDamageByCreature(enchantmentLevel, this.entityLiving);
      }
   }
}

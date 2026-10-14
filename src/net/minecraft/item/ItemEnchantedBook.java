package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {
   @Override
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @Override
   public boolean isItemTool(ItemStack stack) {
      return false;
   }

   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return this.getEnchantments(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
   }

   public NBTTagList getEnchantments(ItemStack stack) {
      NBTTagCompound nbtTagCompound = stack.getTagCompound();
      return nbtTagCompound != null && nbtTagCompound.hasKey("StoredEnchantments", 9) ? (NBTTagList)nbtTagCompound.getTag("StoredEnchantments") : new NBTTagList();
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      super.addInformation(stack, playerIn, tooltip, advanced);
      NBTTagList nbtTagList = this.getEnchantments(stack);
      if (nbtTagList != null) {
         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            short s = nbtTagList.getCompoundTagAt(i).getShort("id");
            short s2 = nbtTagList.getCompoundTagAt(i).getShort("lvl");
            if (Enchantment.getEnchantmentById(s) != null) {
               tooltip.add(Enchantment.getEnchantmentById(s).getTranslatedName(s2));
            }
         }
      }
   }

   public void addEnchantment(ItemStack stack, EnchantmentData enchantment) {
      NBTTagList nbtTagList = this.getEnchantments(stack);
      boolean bl = true;

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         if (nbtTagCompound.getShort("id") == enchantment.enchantmentobj.effectId) {
            if (nbtTagCompound.getShort("lvl") < enchantment.enchantmentLevel) {
               nbtTagCompound.setShort("lvl", (short)enchantment.enchantmentLevel);
            }

            bl = false;
            break;
         }
      }

      if (bl) {
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound2.setShort("id", (short)enchantment.enchantmentobj.effectId);
         nbtTagCompound2.setShort("lvl", (short)enchantment.enchantmentLevel);
         nbtTagList.appendTag(nbtTagCompound2);
      }

      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      stack.getTagCompound().setTag("StoredEnchantments", nbtTagList);
   }

   public ItemStack getEnchantedItemStack(EnchantmentData data) {
      ItemStack itemStack = new ItemStack(this);
      this.addEnchantment(itemStack, data);
      return itemStack;
   }

   public void getAll(Enchantment enchantment, List<ItemStack> list) {
      for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
         list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, i)));
      }
   }

   public WeightedRandomChestContent getRandom(Random rand) {
      return this.getRandom(rand, 1, 1, 1);
   }

   public WeightedRandomChestContent getRandom(Random rand, int minChance, int maxChance, int weight) {
      ItemStack itemStack = new ItemStack(Items.book, 1, 0);
      EnchantmentHelper.addRandomEnchantment(rand, itemStack, 30);
      return new WeightedRandomChestContent(itemStack, minChance, maxChance, weight);
   }
}

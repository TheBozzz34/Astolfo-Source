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
   private static final long a = 128689290526587L;

   @Override
   public boolean hasEffect(ItemStack var1) {
      return true;
   }

   @Override
   public boolean isItemTool(ItemStack var1) {
      return false;
   }

   @Override
   public EnumRarity getRarity(ItemStack var1) {
      return this.getEnchantments(var1).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(var1);
   }

   public NBTTagList getEnchantments(ItemStack var1) {
      NBTTagCompound var5 = var1.getTagCompound();
      return var5 != null && var5.hasKey("StoredEnchantments", 9) ? (NBTTagList)var5.getTag("StoredEnchantments") : new NBTTagList();
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      super.addInformation(var1, var2, var3, var4);
      NBTTagList var8 = this.getEnchantments(var1);
      if (var8 != null) {
         for (int var9 = 0; var9 < var8.tagCount(); var9++) {
            short var10 = var8.getCompoundTagAt(var9).getShort("id");
            short var11 = var8.getCompoundTagAt(var9).getShort("lvl");
            if (Enchantment.getEnchantmentById(var10) != null) {
               var3.add(Enchantment.getEnchantmentById(var10).getTranslatedName(var11));
            }
         }
      }
   }

   public void addEnchantment(ItemStack var1, EnchantmentData var2) {
      NBTTagList var6 = this.getEnchantments(var1);
      boolean var7 = true;

      for (int var8 = 0; var8 < var6.tagCount(); var8++) {
         NBTTagCompound var9 = var6.getCompoundTagAt(var8);
         if (var9.getShort("id") == var2.enchantmentobj.effectId) {
            if (var9.getShort("lvl") < var2.enchantmentLevel) {
               var9.setShort("lvl", (short)var2.enchantmentLevel);
            }

            var7 = false;
            break;
         }
      }

      if (var7) {
         NBTTagCompound var10 = new NBTTagCompound();
         var10.setShort("id", (short)var2.enchantmentobj.effectId);
         var10.setShort("lvl", (short)var2.enchantmentLevel);
         var6.appendTag(var10);
      }

      if (!var1.hasTagCompound()) {
         var1.setTagCompound(new NBTTagCompound());
      }

      var1.getTagCompound().setTag("StoredEnchantments", var6);
   }

   public ItemStack getEnchantedItemStack(EnchantmentData var1) {
      ItemStack var2 = new ItemStack(this);
      this.addEnchantment(var2, var1);
      return var2;
   }

   public void getAll(Enchantment var1, List<ItemStack> var2) {
      for (int var6 = var1.getMinLevel(); var6 <= var1.getMaxLevel(); var6++) {
         var2.add(this.getEnchantedItemStack(new EnchantmentData(var1, var6)));
      }
   }

   public WeightedRandomChestContent getRandom(Random var1) {
      return this.getRandom(var1, 1, 1, 1);
   }

   public WeightedRandomChestContent getRandom(Random var1, int var2, int var3, int var4) {
      ItemStack var7 = new ItemStack(Items.book, 1, 0);
      EnchantmentHelper.addRandomEnchantment(var1, var7, 30);
      return new WeightedRandomChestContent(var7, var2, var3, var4);
   }
}

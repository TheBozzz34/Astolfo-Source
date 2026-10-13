package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   private static final long a = 89404166093962L;

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      if (var2 != 1) {
         return super.getColorFromItemStack(var1, var2);
      }

      NBTBase var6 = getExplosionTag(var1, "Colors");
      if (!(var6 instanceof NBTTagIntArray)) {
         return 9079434;
      }

      NBTTagIntArray var7 = (NBTTagIntArray)var6;
      int[] var8 = var7.getIntArray();
      if (var8.length == 1) {
         return var8[0];
      }

      int var9 = 0;
      int var10 = 0;
      int var11 = 0;

      for (int var15 : var8) {
         var9 += (var15 & 0xFF0000) >> 16;
         var10 += (var15 & 0xFF00) >> 8;
         var11 += (var15 & 0xFF) >> 0;
      }

      var9 /= var8.length;
      var10 /= var8.length;
      var11 /= var8.length;
      return var9 << 16 | var10 << 8 | var11;
   }

   public static NBTBase getExplosionTag(ItemStack var0, String var1) {
      if (var0.hasTagCompound()) {
         NBTTagCompound var5 = var0.getTagCompound().getCompoundTag("Explosion");
         if (var5 != null) {
            return var5.getTag(var1);
         }
      }

      return null;
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.hasTagCompound()) {
         NBTTagCompound var8 = var1.getTagCompound().getCompoundTag("Explosion");
         if (var8 != null) {
            addExplosionInfo(var8, var3);
         }
      }
   }

   public static void addExplosionInfo(NBTTagCompound var0, List<String> var1) {
      byte var5 = var0.getByte("Type");
      if (var5 >= 0 && var5 <= 4) {
         var1.add(StatCollector.translateToLocal("item.fireworksCharge.type." + var5).trim());
      } else {
         var1.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
      }

      int[] var6 = var0.getIntArray("Colors");
      if (var6.length > 0) {
         boolean var7 = true;
         String var8 = "";

         for (int var12 : var6) {
            if (!var7) {
               var8 = var8 + ", ";
            }

            var7 = false;
            boolean var13 = false;

            for (int var14 = 0; var14 < ItemDye.dyeColors.length; var14++) {
               if (var12 == ItemDye.dyeColors[var14]) {
                  var13 = true;
                  var8 = var8 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(var14).getUnlocalizedName());
                  break;
               }
            }

            if (!var13) {
               var8 = var8 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         var1.add(var8);
      }

      int[] var16 = var0.getIntArray("FadeColors");
      if (var16.length > 0) {
         boolean var17 = true;
         String var19 = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

         for (int var24 : var16) {
            if (!var17) {
               var19 = var19 + ", ";
            }

            var17 = false;
            boolean var25 = false;

            for (int var15 = 0; var15 < 16; var15++) {
               if (var24 == ItemDye.dyeColors[var15]) {
                  var25 = true;
                  var19 = var19 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(var15).getUnlocalizedName());
                  break;
               }
            }

            if (!var25) {
               var19 = var19 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         var1.add(var19);
      }

      boolean var18 = var0.getBoolean("Trail");
      if (var18) {
         var1.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
      }

      boolean var20 = var0.getBoolean("Flicker");
      if (var20) {
         var1.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
      }
   }
}

package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass != 1) {
         return super.getColorFromItemStack(stack, renderPass);
      }

      NBTBase nbtBase = getExplosionTag(stack, "Colors");
      if (!(nbtBase instanceof NBTTagIntArray)) {
         return 9079434;
      }

      NBTTagIntArray nbtTagIntArray = (NBTTagIntArray)nbtBase;
      int[] ints = nbtTagIntArray.getIntArray();
      if (ints.length == 1) {
         return ints[0];
      }

      int i = 0;
      int j = 0;
      int k = 0;

      for (int l : ints) {
         i += (l & 0xFF0000) >> 16;
         j += (l & 0xFF00) >> 8;
         k += (l & 0xFF) >> 0;
      }

      i /= ints.length;
      j /= ints.length;
      k /= ints.length;
      return i << 16 | j << 8 | k;
   }

   public static NBTBase getExplosionTag(ItemStack stack, String key) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nbtTagCompound != null) {
            return nbtTagCompound.getTag(key);
         }
      }

      return null;
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nbtTagCompound != null) {
            addExplosionInfo(nbtTagCompound, tooltip);
         }
      }
   }

   public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
      byte b = nbt.getByte("Type");
      if (b >= 0 && b <= 4) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + b).trim());
      } else {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
      }

      int[] ints = nbt.getIntArray("Colors");
      if (ints.length > 0) {
         boolean bl = true;
         String string = "";

         for (int i : ints) {
            if (!bl) {
               string = string + ", ";
            }

            bl = false;
            boolean bl2 = false;

            for (int j = 0; j < ItemDye.dyeColors.length; j++) {
               if (i == ItemDye.dyeColors[j]) {
                  bl2 = true;
                  string = string + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                  break;
               }
            }

            if (!bl2) {
               string = string + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(string);
      }

      int[] ints2 = nbt.getIntArray("FadeColors");
      if (ints2.length > 0) {
         boolean bl3 = true;
         String string2 = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

         for (int k : ints2) {
            if (!bl3) {
               string2 = string2 + ", ";
            }

            bl3 = false;
            boolean bl4 = false;

            for (int l = 0; l < 16; l++) {
               if (k == ItemDye.dyeColors[l]) {
                  bl4 = true;
                  string2 = string2 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(l).getUnlocalizedName());
                  break;
               }
            }

            if (!bl4) {
               string2 = string2 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(string2);
      }

      boolean bl5 = nbt.getBoolean("Trail");
      if (bl5) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
      }

      boolean bl6 = nbt.getBoolean("Flicker");
      if (bl6) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
      }
   }
}

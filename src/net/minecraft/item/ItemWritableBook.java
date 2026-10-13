package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   private static final long a = 128761696988519L;

   public ItemWritableBook() {
      this.setMaxStackSize(1);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      var3.displayGUIBook(var1);
      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return var1;
   }

   public static boolean isNBTValid(NBTTagCompound var0) {
      if (var0 == null) {
         return false;
      }

      if (!var0.hasKey("pages", 9)) {
         return false;
      }

      NBTTagList var4 = var0.getTagList("pages", 8);

      for (int var5 = 0; var5 < var4.tagCount(); var5++) {
         String var6 = var4.getStringTagAt(var5);
         if (var6 == null) {
            return false;
         }

         if (var6.length() > 32767) {
            return false;
         }
      }

      return true;
   }
}

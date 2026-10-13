package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   private static final long a = 69630992118560L;

   public MerchantRecipeList() {
   }

   public MerchantRecipeList(NBTTagCompound var1) {
      this.readRecipiesFromTags(var1);
   }

   public MerchantRecipe canRecipeBeUsed(ItemStack var1, ItemStack var2, int var3) {
      if (var3 > 0 && var3 < this.size()) {
         MerchantRecipe var9 = this.get(var3);
         return this.func_181078_a(var1, var9.getItemToBuy())
               && (var2 == null && !var9.hasSecondItemToBuy() || var9.hasSecondItemToBuy() && this.func_181078_a(var2, var9.getSecondItemToBuy()))
               && var1.stackSize >= var9.getItemToBuy().stackSize
               && (!var9.hasSecondItemToBuy() || var2.stackSize >= var9.getSecondItemToBuy().stackSize)
            ? var9
            : null;
      }

      for (int var7 = 0; var7 < this.size(); var7++) {
         MerchantRecipe var8 = this.get(var7);
         if (this.func_181078_a(var1, var8.getItemToBuy())
            && var1.stackSize >= var8.getItemToBuy().stackSize
            && (
               !var8.hasSecondItemToBuy() && var2 == null
                  || var8.hasSecondItemToBuy() && this.func_181078_a(var2, var8.getSecondItemToBuy()) && var2.stackSize >= var8.getSecondItemToBuy().stackSize
            )) {
            return var8;
         }
      }

      return null;
   }

   private boolean func_181078_a(ItemStack var1, ItemStack var2) {
      return ItemStack.areItemsEqual(var1, var2)
         && (!var2.hasTagCompound() || var1.hasTagCompound() && NBTUtil.func_181123_a(var2.getTagCompound(), var1.getTagCompound(), false));
   }

   public void writeToBuf(PacketBuffer var1) {
      var1.writeByte((byte)(this.size() & 0xFF));

      for (int var5 = 0; var5 < this.size(); var5++) {
         MerchantRecipe var6 = this.get(var5);
         var1.writeItemStackToBuffer(var6.getItemToBuy());
         var1.writeItemStackToBuffer(var6.getItemToSell());
         ItemStack var7 = var6.getSecondItemToBuy();
         var1.writeBoolean(var7 != null);
         if (var7 != null) {
            var1.writeItemStackToBuffer(var7);
         }

         var1.writeBoolean(var6.isRecipeDisabled());
         var1.writeInt(var6.getToolUses());
         var1.writeInt(var6.getMaxTradeUses());
      }
   }

   public static MerchantRecipeList readFromBuf(PacketBuffer var0) throws IOException {
      MerchantRecipeList var4 = new MerchantRecipeList();
      int var5 = var0.readByte() & 255;

      for (int var6 = 0; var6 < var5; var6++) {
         ItemStack var7 = var0.readItemStackFromBuffer();
         ItemStack var8 = var0.readItemStackFromBuffer();
         ItemStack var9 = null;
         if (var0.readBoolean()) {
            var9 = var0.readItemStackFromBuffer();
         }

         boolean var10 = var0.readBoolean();
         int var11 = var0.readInt();
         int var12 = var0.readInt();
         MerchantRecipe var13 = new MerchantRecipe(var7, var9, var8, var11, var12);
         if (var10) {
            var13.compensateToolUses();
         }

         var4.add(var13);
      }

      return var4;
   }

   public void readRecipiesFromTags(NBTTagCompound var1) {
      NBTTagList var5 = var1.getTagList("Recipes", 10);

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         this.add(new MerchantRecipe(var7));
      }
   }

   public NBTTagCompound getRecipiesAsTags() {
      NBTTagCompound var4 = new NBTTagCompound();
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.size(); var6++) {
         MerchantRecipe var7 = this.get(var6);
         var5.appendTag(var7.writeToTags());
      }

      var4.setTag("Recipes", var5);
      return var4;
   }
}

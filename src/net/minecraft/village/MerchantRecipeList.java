package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList() {
   }

   public MerchantRecipeList(NBTTagCompound compound) {
      this.readRecipiesFromTags(compound);
   }

   public MerchantRecipe canRecipeBeUsed(ItemStack itemStack, ItemStack itemStack2, int i) {
      if (i > 0 && i < this.size()) {
         MerchantRecipe merchantRecipe = this.get(i);
         return this.func_181078_a(itemStack, merchantRecipe.getItemToBuy())
               && (itemStack2 == null && !merchantRecipe.hasSecondItemToBuy() || merchantRecipe.hasSecondItemToBuy() && this.func_181078_a(itemStack2, merchantRecipe.getSecondItemToBuy()))
               && itemStack.stackSize >= merchantRecipe.getItemToBuy().stackSize
               && (!merchantRecipe.hasSecondItemToBuy() || itemStack2.stackSize >= merchantRecipe.getSecondItemToBuy().stackSize)
            ? merchantRecipe
            : null;
      }

      for (int j = 0; j < this.size(); j++) {
         MerchantRecipe merchantRecipe2 = this.get(j);
         if (this.func_181078_a(itemStack, merchantRecipe2.getItemToBuy())
            && itemStack.stackSize >= merchantRecipe2.getItemToBuy().stackSize
            && (
               !merchantRecipe2.hasSecondItemToBuy() && itemStack2 == null
                  || merchantRecipe2.hasSecondItemToBuy()
                     && this.func_181078_a(itemStack2, merchantRecipe2.getSecondItemToBuy())
                     && itemStack2.stackSize >= merchantRecipe2.getSecondItemToBuy().stackSize
            )) {
            return merchantRecipe2;
         }
      }

      return null;
   }

   private boolean func_181078_a(ItemStack itemStack, ItemStack itemStack2) {
      return ItemStack.areItemsEqual(itemStack, itemStack2)
         && (
            !itemStack2.hasTagCompound() || itemStack.hasTagCompound() && NBTUtil.func_181123_a(itemStack2.getTagCompound(), itemStack.getTagCompound(), false)
         );
   }

   public void writeToBuf(PacketBuffer buffer) {
      buffer.writeByte((byte)(this.size() & 0xFF));

      for (int i = 0; i < this.size(); i++) {
         MerchantRecipe merchantRecipe = this.get(i);
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToBuy());
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToSell());
         ItemStack itemStack = merchantRecipe.getSecondItemToBuy();
         buffer.writeBoolean(itemStack != null);
         if (itemStack != null) {
            buffer.writeItemStackToBuffer(itemStack);
         }

         buffer.writeBoolean(merchantRecipe.isRecipeDisabled());
         buffer.writeInt(merchantRecipe.getToolUses());
         buffer.writeInt(merchantRecipe.getMaxTradeUses());
      }
   }

   public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
      MerchantRecipeList merchantRecipeList = new MerchantRecipeList();
      int i = buffer.readByte() & 255;

      for (int j = 0; j < i; j++) {
         ItemStack itemStack = buffer.readItemStackFromBuffer();
         ItemStack itemStack2 = buffer.readItemStackFromBuffer();
         ItemStack itemStack3 = null;
         if (buffer.readBoolean()) {
            itemStack3 = buffer.readItemStackFromBuffer();
         }

         boolean bl = buffer.readBoolean();
         int k = buffer.readInt();
         int l = buffer.readInt();
         MerchantRecipe merchantRecipe = new MerchantRecipe(itemStack, itemStack3, itemStack2, k, l);
         if (bl) {
            merchantRecipe.compensateToolUses();
         }

         merchantRecipeList.add(merchantRecipe);
      }

      return merchantRecipeList;
   }

   public void readRecipiesFromTags(NBTTagCompound compound) {
      NBTTagList nbtTagList = compound.getTagList("Recipes", 10);

      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         this.add(new MerchantRecipe(nbtTagCompound));
      }
   }

   public NBTTagCompound getRecipiesAsTags() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.size(); i++) {
         MerchantRecipe merchantRecipe = this.get(i);
         nbtTagList.appendTag(merchantRecipe.writeToTags());
      }

      nbtTagCompound.setTag("Recipes", nbtTagList);
      return nbtTagCompound;
   }
}

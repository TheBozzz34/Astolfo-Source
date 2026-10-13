package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
   private static final long a = 102342940877408L;

   public ItemEditableBook() {
      this.setMaxStackSize(1);
   }

   public static boolean validBookTagContents(NBTTagCompound var0) {
      if (!ItemWritableBook.isNBTValid(var0)) {
         return false;
      }

      if (!var0.hasKey("title", 8)) {
         return false;
      }

      String var4 = var0.getString("title");
      return var4 != null && var4.length() <= 32 ? var0.hasKey("author", 8) : false;
   }

   public static int getGeneration(ItemStack var0) {
      return var0.getTagCompound().getInteger("generation");
   }

   @Override
   public String getItemStackDisplayName(ItemStack var1) {
      if (var1.hasTagCompound()) {
         NBTTagCompound var5 = var1.getTagCompound();
         String var6 = var5.getString("title");
         if (!StringUtils.isNullOrEmpty(var6)) {
            return var6;
         }
      }

      return super.getItemStackDisplayName(var1);
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.hasTagCompound()) {
         NBTTagCompound var8 = var1.getTagCompound();
         String var9 = var8.getString("author");
         if (!StringUtils.isNullOrEmpty(var9)) {
            var3.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", var9));
         }

         var3.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + var8.getInteger("generation")));
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var2.isRemote) {
         this.resolveContents(var1, var3);
      }

      var3.displayGUIBook(var1);
      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return var1;
   }

   private void resolveContents(ItemStack var1, EntityPlayer var2) {
      if (var1 != null && var1.getTagCompound() != null) {
         NBTTagCompound var6 = var1.getTagCompound();
         if (!var6.getBoolean("resolved")) {
            var6.setBoolean("resolved", true);
            if (validBookTagContents(var6)) {
               NBTTagList var7 = var6.getTagList("pages", 8);

               for (int var8 = 0; var8 < var7.tagCount(); var8++) {
                  String var9 = var7.getStringTagAt(var8);

                  IChatComponent var10;
                  try {
                     var10 = IChatComponent.Serializer.jsonToComponent(var9);
                     var10 = ChatComponentProcessor.processComponent(var2, var10, var2);
                  } catch (Exception var12) {
                     var10 = new ChatComponentText(var9);
                  }

                  var7.set(var8, new NBTTagString(IChatComponent.Serializer.componentToJson(var10)));
               }

               var6.setTag("pages", var7);
               if (var2 instanceof EntityPlayerMP && var2.getCurrentEquippedItem() == var1) {
                  Slot var13 = var2.openContainer.getSlotFromInventory(var2.inventory, var2.inventory.currentItem);
                  ((EntityPlayerMP)var2).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, var13.slotNumber, var1));
               }
            }
         }
      }
   }

   @Override
   public boolean hasEffect(ItemStack var1) {
      return true;
   }
}

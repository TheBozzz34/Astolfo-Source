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
   public ItemEditableBook() {
      this.setMaxStackSize(1);
   }

   public static boolean validBookTagContents(NBTTagCompound nbt) {
      if (!ItemWritableBook.isNBTValid(nbt)) {
         return false;
      }

      if (!nbt.hasKey("title", 8)) {
         return false;
      }

      String string = nbt.getString("title");
      return string != null && string.length() <= 32 ? nbt.hasKey("author", 8) : false;
   }

   public static int getGeneration(ItemStack book) {
      return book.getTagCompound().getInteger("generation");
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound();
         String string = nbtTagCompound.getString("title");
         if (!StringUtils.isNullOrEmpty(string)) {
            return string;
         }
      }

      return super.getItemStackDisplayName(stack);
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound();
         String string = nbtTagCompound.getString("author");
         if (!StringUtils.isNullOrEmpty(string)) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", string));
         }

         tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + nbtTagCompound.getInteger("generation")));
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (!worldIn.isRemote) {
         this.resolveContents(itemStackIn, playerIn);
      }

      playerIn.displayGUIBook(itemStackIn);
      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   private void resolveContents(ItemStack stack, EntityPlayer player) {
      if (stack != null && stack.getTagCompound() != null) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound();
         if (!nbtTagCompound.getBoolean("resolved")) {
            nbtTagCompound.setBoolean("resolved", true);
            if (validBookTagContents(nbtTagCompound)) {
               NBTTagList nbtTagList = nbtTagCompound.getTagList("pages", 8);

               for (int i = 0; i < nbtTagList.tagCount(); i++) {
                  String string = nbtTagList.getStringTagAt(i);

                  IChatComponent iChatComponent;
                  try {
                     iChatComponent = IChatComponent.Serializer.jsonToComponent(string);
                     iChatComponent = ChatComponentProcessor.processComponent(player, iChatComponent, player);
                  } catch (Exception exception) {
                     iChatComponent = new ChatComponentText(string);
                  }

                  nbtTagList.set(i, new NBTTagString(IChatComponent.Serializer.componentToJson(iChatComponent)));
               }

               nbtTagCompound.setTag("pages", nbtTagList);
               if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack) {
                  Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                  ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slot.slotNumber, stack));
               }
            }
         }
      }
   }

   @Override
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}

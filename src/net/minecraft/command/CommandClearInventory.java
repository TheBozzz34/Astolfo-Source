package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase {
   private static final long b = 28751268425107L;

   @Override
   public String getCommandName() {
      return "clear";
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.clear.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      EntityPlayerMP var6 = var2.length == 0 ? getCommandSenderAsPlayer(var1) : getPlayer(var1, var2[0]);
      Item var7 = var2.length >= 2 ? getItemByText(var1, var2[1]) : null;
      int var8 = var2.length >= 3 ? parseInt(var2[2], -1) : -1;
      int var9 = var2.length >= 4 ? parseInt(var2[3], -1) : -1;
      NBTTagCompound var10 = null;
      if (var2.length >= 5) {
         try {
            var10 = JsonToNBT.getTagFromJson(buildString(var2, 4));
         } catch (NBTException var12) {
            throw new CommandException("commands.clear.tagError", var12.getMessage());
         }
      }

      if (var2.length >= 2 && var7 == null) {
         throw new CommandException("commands.clear.failure", var6.getName());
      }

      int var11 = var6.inventory.clearMatchingItems(var7, var8, var9, var10);
      var6.inventoryContainer.detectAndSendChanges();
      if (!var6.capabilities.isCreativeMode) {
         var6.updateHeldItem();
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, var11);
      if (var11 == 0) {
         throw new CommandException("commands.clear.failure", var6.getName());
      }

      if (var9 == 0) {
         var1.addChatMessage(new ChatComponentTranslation("commands.clear.testing", var6.getName(), var11));
      } else {
         notifyOperators(var1, this, "commands.clear.success", var6.getName(), var11);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, this.func_147209_d())
         : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, Item.itemRegistry.getKeys()) : null);
   }

   public String[] func_147209_d() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}

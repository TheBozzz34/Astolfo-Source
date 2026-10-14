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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clear.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      EntityPlayerMP entityPlayerMP = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
      Item item = args.length >= 2 ? getItemByText(sender, args[1]) : null;
      int i = args.length >= 3 ? parseInt(args[2], -1) : -1;
      int j = args.length >= 4 ? parseInt(args[3], -1) : -1;
      NBTTagCompound nbtTagCompound = null;
      if (args.length >= 5) {
         try {
            nbtTagCompound = JsonToNBT.getTagFromJson(buildString(args, 4));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.clear.tagError", nbtException.getMessage());
         }
      }

      if (args.length >= 2 && item == null) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      int k = entityPlayerMP.inventory.clearMatchingItems(item, i, j, nbtTagCompound);
      entityPlayerMP.inventoryContainer.detectAndSendChanges();
      if (!entityPlayerMP.capabilities.isCreativeMode) {
         entityPlayerMP.updateHeldItem();
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k);
      if (k == 0) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      if (j == 0) {
         sender.addChatMessage(new ChatComponentTranslation("commands.clear.testing", entityPlayerMP.getName(), k));
      } else {
         notifyOperators(sender, this, "commands.clear.success", entityPlayerMP.getName(), k);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.func_147209_d())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   public String[] func_147209_d() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}

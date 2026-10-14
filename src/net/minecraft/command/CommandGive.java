package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandGive extends CommandBase {
   private static final long b = 126301419266952L;
   private static final long j = 2165545295444705344L;

   @Override
   public String getCommandName() {
      return "give";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.give.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.give.usage");
      }

      EntityPlayerMP entityPlayerMP = getPlayer(sender, args[0]);
      Item item = getItemByText(sender, args[1]);
      int i = args.length >= 3 ? parseInt(args[2], 1, (int)j) : 1;
      int k = args.length >= 4 ? parseInt(args[3]) : 0;
      ItemStack itemStack = new ItemStack(item, i, k);
      if (args.length >= 5) {
         String string = getChatComponentFromNthArg(sender, args, 4).getUnformattedText();

         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(string));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.give.tagError", nbtException.getMessage());
         }
      }

      boolean bl = entityPlayerMP.inventory.addItemStackToInventory(itemStack);
      if (bl) {
         entityPlayerMP.worldObj.playSoundAtEntity(entityPlayerMP, "random.pop", 0.2F, ((entityPlayerMP.getRNG().nextFloat() - entityPlayerMP.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         entityPlayerMP.inventoryContainer.detectAndSendChanges();
      }

      if (bl && itemStack.stackSize <= 0) {
         itemStack.stackSize = 1;
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i);
         EntityItem entityItem = entityPlayerMP.dropPlayerItemWithRandomChoice(itemStack, false);
         if (entityItem != null) {
            entityItem.func_174870_v();
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i - itemStack.stackSize);
         EntityItem entityItem2 = entityPlayerMP.dropPlayerItemWithRandomChoice(itemStack, false);
         if (entityItem2 != null) {
            entityItem2.setNoPickupDelay();
            entityItem2.setOwner(entityPlayerMP.getName());
         }
      }

      notifyOperators(sender, this, "commands.give.success", itemStack.getChatComponent(), i, entityPlayerMP.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   public String[] getPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}

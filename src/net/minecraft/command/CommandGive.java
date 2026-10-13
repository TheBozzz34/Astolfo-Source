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
   public String getCommandUsage(ICommandSender var1) {
      return "commands.give.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.give.usage");
      }

      EntityPlayerMP var6 = getPlayer(var1, var2[0]);
      Item var7 = getItemByText(var1, var2[1]);
      int var8 = var2.length >= 3 ? parseInt(var2[2], 1, (int)j) : 1;
      int var9 = var2.length >= 4 ? parseInt(var2[3]) : 0;
      ItemStack var10 = new ItemStack(var7, var8, var9);
      if (var2.length >= 5) {
         String var11 = getChatComponentFromNthArg(var1, var2, 4).getUnformattedText();

         try {
            var10.setTagCompound(JsonToNBT.getTagFromJson(var11));
         } catch (NBTException var13) {
            throw new CommandException("commands.give.tagError", var13.getMessage());
         }
      }

      boolean var14 = var6.inventory.addItemStackToInventory(var10);
      if (var14) {
         var6.worldObj.playSoundAtEntity(var6, "random.pop", 0.2F, ((var6.getRNG().nextFloat() - var6.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         var6.inventoryContainer.detectAndSendChanges();
      }

      if (var14 && var10.stackSize <= 0) {
         var10.stackSize = 1;
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, var8);
         EntityItem var15 = var6.dropPlayerItemWithRandomChoice(var10, false);
         if (var15 != null) {
            var15.func_174870_v();
         }
      } else {
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, var8 - var10.stackSize);
         EntityItem var12 = var6.dropPlayerItemWithRandomChoice(var10, false);
         if (var12 != null) {
            var12.setNoPickupDelay();
            var12.setOwner(var6.getName());
         }
      }

      notifyOperators(var1, this, "commands.give.success", var10.getChatComponent(), var8, var6.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, this.getPlayers())
         : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, Item.itemRegistry.getKeys()) : null);
   }

   public String[] getPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}

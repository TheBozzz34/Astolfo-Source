package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData extends CommandBase {
   private static final long b = 200737392066L;

   @Override
   public String getCommandName() {
      return "entitydata";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.entitydata.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.entitydata.usage");
      }

      Entity var6 = getEntity(var1, var2[0]);
      if (var6 instanceof EntityPlayer) {
         throw new CommandException("commands.entitydata.noPlayers", var6.getDisplayName());
      }

      NBTTagCompound var7 = new NBTTagCompound();
      var6.writeToNBT(var7);
      NBTTagCompound var8 = (NBTTagCompound)var7.copy();

      NBTTagCompound var9;
      try {
         var9 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(var1, var2, 1).getUnformattedText());
      } catch (NBTException var11) {
         throw new CommandException("commands.entitydata.tagError", var11.getMessage());
      }

      var9.removeTag("UUIDMost");
      var9.removeTag("UUIDLeast");
      var7.merge(var9);
      if (var7.equals(var8)) {
         throw new CommandException("commands.entitydata.failed", var7.toString());
      }

      var6.readFromNBT(var7);
      notifyOperators(var1, this, "commands.entitydata.success", var7.toString());
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}

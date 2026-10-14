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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.entitydata.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.entitydata.usage");
      }

      Entity entity = getEntity(sender, args[0]);
      if (entity instanceof EntityPlayer) {
         throw new CommandException("commands.entitydata.noPlayers", entity.getDisplayName());
      }

      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      entity.writeToNBT(nbtTagCompound);
      NBTTagCompound nbtTagCompound2 = (NBTTagCompound)nbtTagCompound.copy();

      NBTTagCompound nbtTagCompound3;
      try {
         nbtTagCompound3 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
      } catch (NBTException nbtException) {
         throw new CommandException("commands.entitydata.tagError", nbtException.getMessage());
      }

      nbtTagCompound3.removeTag("UUIDMost");
      nbtTagCompound3.removeTag("UUIDLeast");
      nbtTagCompound.merge(nbtTagCompound3);
      if (nbtTagCompound.equals(nbtTagCompound2)) {
         throw new CommandException("commands.entitydata.failed", nbtTagCompound.toString());
      }

      entity.readFromNBT(nbtTagCompound);
      notifyOperators(sender, this, "commands.entitydata.success", nbtTagCompound.toString());
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}

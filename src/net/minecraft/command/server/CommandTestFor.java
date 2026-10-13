package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTestFor extends CommandBase {
   private static final long b = 128792094121190L;

   @Override
   public String getCommandName() {
      return "testfor";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.testfor.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.testfor.usage");
      }

      Entity var6 = getEntity(var1, var2[0]);
      NBTTagCompound var7 = null;
      if (var2.length >= 2) {
         try {
            var7 = JsonToNBT.getTagFromJson(buildString(var2, 1));
         } catch (NBTException var9) {
            throw new CommandException("commands.testfor.tagError", var9.getMessage());
         }
      }

      if (var7 != null) {
         NBTTagCompound var8 = new NBTTagCompound();
         var6.writeToNBT(var8);
         if (!NBTUtil.func_181123_a(var7, var8, true)) {
            throw new CommandException("commands.testfor.failure", var6.getName());
         }
      }

      notifyOperators(var1, this, "commands.testfor.success", var6.getName());
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}

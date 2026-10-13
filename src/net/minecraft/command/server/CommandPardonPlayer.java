package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandPardonPlayer extends CommandBase {
   private static final long b = 86386518290267L;

   @Override
   public String getCommandName() {
      return "pardon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.unban.usage";
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(var1);
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var6 = MinecraftServer.getServer();
         GameProfile var7 = var6.getConfigurationManager().getBannedPlayers().isUsernameBanned(var2[0]);
         if (var7 == null) {
            throw new CommandException("commands.unban.failed", var2[0]);
         }

         var6.getConfigurationManager().getBannedPlayers().removeEntry(var7);
         notifyOperators(var1, this, "commands.unban.success", var2[0]);
      } else {
         throw new WrongUsageException("commands.unban.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1
         ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys())
         : null;
   }
}

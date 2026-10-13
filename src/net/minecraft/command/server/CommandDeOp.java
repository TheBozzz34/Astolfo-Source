package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandDeOp extends CommandBase {
   private static final long b = 106319104453525L;

   @Override
   public String getCommandName() {
      return "deop";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.deop.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var6 = MinecraftServer.getServer();
         GameProfile var7 = var6.getConfigurationManager().getOppedPlayers().getGameProfileFromName(var2[0]);
         if (var7 == null) {
            throw new CommandException("commands.deop.failed", var2[0]);
         }

         var6.getConfigurationManager().C(var7);
         notifyOperators(var1, this, "commands.deop.success", var2[0]);
      } else {
         throw new WrongUsageException("commands.deop.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()) : null;
   }
}

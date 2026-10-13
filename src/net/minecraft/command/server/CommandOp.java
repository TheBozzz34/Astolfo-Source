package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandOp extends CommandBase {
   private static final long b = 44188091695974L;

   @Override
   public String getCommandName() {
      return "op";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.op.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var6 = MinecraftServer.getServer();
         GameProfile var7 = var6.getPlayerProfileCache().getGameProfileForUsername(var2[0]);
         if (var7 == null) {
            throw new CommandException("commands.op.failed", var2[0]);
         }

         var6.getConfigurationManager().J(var7);
         notifyOperators(var1, this, "commands.op.success", var2[0]);
      } else {
         throw new WrongUsageException("commands.op.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         String var7 = var2[var2.length - 1];
         ArrayList var8 = Lists.newArrayList();

         for (GameProfile var12 : MinecraftServer.getServer().getGameProfiles()) {
            if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(var12) && doesStringStartWith(var7, var12.getName())) {
               var8.add(var12.getName());
            }
         }

         return var8;
      } else {
         return null;
      }
   }
}

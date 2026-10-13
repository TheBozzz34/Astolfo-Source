package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

public class CommandBanPlayer extends CommandBase {
   private static final long b = 51367927906228L;

   @Override
   public String getCommandName() {
      return "ban";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.ban.usage";
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(var1);
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].length() > 0) {
         MinecraftServer var6 = MinecraftServer.getServer();
         GameProfile var7 = var6.getPlayerProfileCache().getGameProfileForUsername(var2[0]);
         if (var7 == null) {
            throw new CommandException("commands.ban.failed", var2[0]);
         }

         String var8 = null;
         if (var2.length >= 2) {
            var8 = getChatComponentFromNthArg(var1, var2, 1).getUnformattedText();
         }

         UserListBansEntry var9 = new UserListBansEntry(var7, (Date)null, var1.getName(), (Date)null, var8);
         var6.getConfigurationManager().getBannedPlayers().addEntry(var9);
         EntityPlayerMP var10 = var6.getConfigurationManager().getPlayerByUsername(var2[0]);
         if (var10 != null) {
            var10.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
         }

         notifyOperators(var1, this, "commands.ban.success", var2[0]);
      } else {
         throw new WrongUsageException("commands.ban.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}

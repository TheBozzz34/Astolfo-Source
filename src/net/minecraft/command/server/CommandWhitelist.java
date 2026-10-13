package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {
   private static final long b = 13732055680716L;
   private static final long j = 7607369198502674438L;

   @Override
   public String getCommandName() {
      return "whitelist";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.whitelist.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.whitelist.usage");
      }

      MinecraftServer var6 = MinecraftServer.getServer();
      if (var2[0].equals("on")) {
         var6.getConfigurationManager().setWhiteListEnabled(true);
         notifyOperators(var1, this, "commands.whitelist.enabled");
      } else if (var2[0].equals("off")) {
         var6.getConfigurationManager().setWhiteListEnabled(false);
         notifyOperators(var1, this, "commands.whitelist.disabled");
      } else if (var2[0].equals("list")) {
         var1.addChatMessage(
            new ChatComponentTranslation(
               "commands.whitelist.list",
               var6.getConfigurationManager().getWhitelistedPlayerNames().length,
               var6.getConfigurationManager().getAvailablePlayerDat().length
            )
         );
         String[] var7 = var6.getConfigurationManager().getWhitelistedPlayerNames();
         var1.addChatMessage(new ChatComponentText(joinNiceString(var7)));
      } else if (var2[0].equals("add")) {
         if (var2.length < 2) {
            throw new WrongUsageException("commands.whitelist.add.usage");
         }

         GameProfile var8 = var6.getPlayerProfileCache().getGameProfileForUsername(var2[1]);
         if (var8 == null) {
            throw new CommandException("commands.whitelist.add.failed", var2[1]);
         }

         var6.getConfigurationManager().addWhitelistedPlayer(var8);
         notifyOperators(var1, this, "commands.whitelist.add.success", var2[1]);
      } else if (var2[0].equals("remove")) {
         if (var2.length < 2) {
            throw new WrongUsageException("commands.whitelist.remove.usage");
         }

         GameProfile var9 = var6.getConfigurationManager().getWhitelistedPlayers().getBannedProfile(var2[1]);
         if (var9 == null) {
            throw new CommandException("commands.whitelist.remove.failed", var2[1]);
         }

         var6.getConfigurationManager().removePlayerFromWhitelist(var9);
         notifyOperators(var1, this, "commands.whitelist.remove.success", var2[1]);
      } else if (var2[0].equals("reload")) {
         var6.getConfigurationManager().loadWhiteList();
         notifyOperators(var1, this, "commands.whitelist.reloaded");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         String[] var10001 = new String[(int)j];
         var10001[0] = "on";
         var10001[1] = "off";
         var10001[2] = "list";
         var10001[3] = "add";
         var10001[4] = "remove";
         var10001[5] = "reload";
         return getListOfStringsMatchingLastWord(var2, var10001);
      }

      if (var2.length == 2) {
         if (var2[0].equals("remove")) {
            return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
         }

         if (var2[0].equals("add")) {
            return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
         }
      }

      return null;
   }
}

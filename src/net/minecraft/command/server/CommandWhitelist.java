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
   public String getCommandUsage(ICommandSender sender) {
      return "commands.whitelist.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.whitelist.usage");
      }

      MinecraftServer minecraftServer = MinecraftServer.getServer();
      if (args[0].equals("on")) {
         minecraftServer.getConfigurationManager().setWhiteListEnabled(true);
         notifyOperators(sender, this, "commands.whitelist.enabled");
      } else if (args[0].equals("off")) {
         minecraftServer.getConfigurationManager().setWhiteListEnabled(false);
         notifyOperators(sender, this, "commands.whitelist.disabled");
      } else if (args[0].equals("list")) {
         sender.addChatMessage(
            new ChatComponentTranslation(
               "commands.whitelist.list",
               minecraftServer.getConfigurationManager().getWhitelistedPlayerNames().length,
               minecraftServer.getConfigurationManager().getAvailablePlayerDat().length
            )
         );
         String[] strings = minecraftServer.getConfigurationManager().getWhitelistedPlayerNames();
         sender.addChatMessage(new ChatComponentText(joinNiceString(strings)));
      } else if (args[0].equals("add")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.whitelist.add.usage");
         }

         GameProfile gameProfile = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(args[1]);
         if (gameProfile == null) {
            throw new CommandException("commands.whitelist.add.failed", args[1]);
         }

         minecraftServer.getConfigurationManager().addWhitelistedPlayer(gameProfile);
         notifyOperators(sender, this, "commands.whitelist.add.success", args[1]);
      } else if (args[0].equals("remove")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.whitelist.remove.usage");
         }

         GameProfile gameProfile2 = minecraftServer.getConfigurationManager().getWhitelistedPlayers().getBannedProfile(args[1]);
         if (gameProfile2 == null) {
            throw new CommandException("commands.whitelist.remove.failed", args[1]);
         }

         minecraftServer.getConfigurationManager().removePlayerFromWhitelist(gameProfile2);
         notifyOperators(sender, this, "commands.whitelist.remove.success", args[1]);
      } else if (args[0].equals("reload")) {
         minecraftServer.getConfigurationManager().loadWhiteList();
         notifyOperators(sender, this, "commands.whitelist.reloaded");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         String[] strings = new String[(int)j];
         strings[0] = "on";
         strings[1] = "off";
         strings[2] = "list";
         strings[3] = "add";
         strings[4] = "remove";
         strings[5] = "reload";
         return getListOfStringsMatchingLastWord(args, strings);
      }

      if (args.length == 2) {
         if (args[0].equals("remove")) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
         }

         if (args[0].equals("add")) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
         }
      }

      return null;
   }
}

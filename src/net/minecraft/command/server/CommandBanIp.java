package net.minecraft.command.server;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class CommandBanIp extends CommandBase {
   public static final Pattern field_147211_a = Pattern.compile(
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
   );

   @Override
   public String getCommandName() {
      return "ban-ip";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.banip.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 1 && args[0].length() > 1) {
         IChatComponent iChatComponent = args.length >= 2 ? getChatComponentFromNthArg(sender, args, 1) : null;
         Matcher matcher2 = field_147211_a.matcher(args[0]);
         if (matcher2.matches()) {
            this.func_147210_a(sender, args[0], iChatComponent == null ? null : iChatComponent.getUnformattedText());
         } else {
            EntityPlayerMP entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
            if (entityPlayerMP == null) {
               throw new PlayerNotFoundException("commands.banip.invalid");
            }

            this.func_147210_a(sender, entityPlayerMP.getPlayerIP(), iChatComponent == null ? null : iChatComponent.getUnformattedText());
         }
      } else {
         throw new WrongUsageException("commands.banip.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   public void func_147210_a(ICommandSender sender, String address, String reason) {
      IPBanEntry ipBanEntry = new IPBanEntry(address, (Date)null, sender.getName(), (Date)null, reason);
      MinecraftServer.getServer().getConfigurationManager().getBannedIPs().addEntry(ipBanEntry);
      List list = MinecraftServer.getServer().getConfigurationManager().getPlayersMatchingAddress(address);
      String[] strings = new String[list.size()];
      int i = 0;

      for (Object object : list) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)object;
         entityPlayerMP.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
         strings[i++] = entityPlayerMP.getName();
      }

      if (list.isEmpty()) {
         notifyOperators(sender, this, "commands.banip.success", address);
      } else {
         notifyOperators(sender, this, "commands.banip.success.players", address, joinNiceString(strings));
      }
   }
}

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
   private static final long b = 28722025304256L;

   @Override
   public String getCommandName() {
      return "ban-ip";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(var1);
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.banip.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].length() > 1) {
         IChatComponent var6 = var2.length >= 2 ? getChatComponentFromNthArg(var1, var2, 1) : null;
         Matcher var7 = field_147211_a.matcher(var2[0]);
         if (var7.matches()) {
            this.func_147210_a(var1, var2[0], var6 == null ? null : var6.getUnformattedText());
         } else {
            EntityPlayerMP var8 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(var2[0]);
            if (var8 == null) {
               throw new PlayerNotFoundException("commands.banip.invalid");
            }

            this.func_147210_a(var1, var8.getPlayerIP(), var6 == null ? null : var6.getUnformattedText());
         }
      } else {
         throw new WrongUsageException("commands.banip.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   public void func_147210_a(ICommandSender var1, String var2, String var3) {
      IPBanEntry var7 = new IPBanEntry(var2, (Date)null, var1.getName(), (Date)null, var3);
      MinecraftServer.getServer().getConfigurationManager().getBannedIPs().addEntry(var7);
      List var8 = MinecraftServer.getServer().getConfigurationManager().getPlayersMatchingAddress(var2);
      String[] var9 = new String[var8.size()];
      int var10 = 0;

      for (Object var12$raw : var8) {
         EntityPlayerMP var12 = (EntityPlayerMP)var12$raw;
         var12.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
         var9[var10++] = var12.getName();
      }

      if (var8.isEmpty()) {
         notifyOperators(var1, this, "commands.banip.success", var2);
      } else {
         notifyOperators(var1, this, "commands.banip.success.players", var2, joinNiceString(var9));
      }
   }
}

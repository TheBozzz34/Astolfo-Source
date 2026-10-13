package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandServerKick extends CommandBase {
   private static final long b = 46483176747560L;

   @Override
   public String getCommandName() {
      return "kick";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.kick.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 0 && var2[0].length() > 1) {
         EntityPlayerMP var6 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(var2[0]);
         String var7 = "Kicked by an operator.";
         boolean var8 = false;
         if (var6 == null) {
            throw new PlayerNotFoundException();
         }

         if (var2.length >= 2) {
            var7 = getChatComponentFromNthArg(var1, var2, 1).getUnformattedText();
            var8 = true;
         }

         var6.playerNetServerHandler.kickPlayerFromServer(var7);
         if (var8) {
            notifyOperators(var1, this, "commands.kick.success.reason", var6.getName(), var7);
         } else {
            notifyOperators(var1, this, "commands.kick.success", var6.getName());
         }
      } else {
         throw new WrongUsageException("commands.kick.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}

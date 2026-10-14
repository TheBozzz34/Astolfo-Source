package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandServerKick extends CommandBase {
   @Override
   public String getCommandName() {
      return "kick";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.kick.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length > 0 && args[0].length() > 1) {
         EntityPlayerMP entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
         String string = "Kicked by an operator.";
         boolean bl = false;
         if (entityPlayerMP == null) {
            throw new PlayerNotFoundException();
         }

         if (args.length >= 2) {
            string = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
            bl = true;
         }

         entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(string);
         if (bl) {
            notifyOperators(sender, this, "commands.kick.success.reason", entityPlayerMP.getName(), string);
         } else {
            notifyOperators(sender, this, "commands.kick.success", entityPlayerMP.getName());
         }
      } else {
         throw new WrongUsageException("commands.kick.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}

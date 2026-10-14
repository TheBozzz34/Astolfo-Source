package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase {
   @Override
   public String getCommandName() {
      return "xp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.xp.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.xp.usage");
      }

      String string = args[0];
      boolean bl = string.endsWith("l") || string.endsWith("L");
      if (bl && string.length() > 1) {
         string = string.substring(0, string.length() - 1);
      }

      int i = parseInt(string);
      boolean bl2 = i < 0;
      if (bl2) {
         i *= -1;
      }

      EntityPlayerMP entityPlayerMP = args.length > 1 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
      if (bl) {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayerMP.experienceLevel);
         if (bl2) {
            entityPlayerMP.addExperienceLevel(-i);
            notifyOperators(sender, this, "commands.xp.success.negative.levels", i, entityPlayerMP.getName());
         } else {
            entityPlayerMP.addExperienceLevel(i);
            notifyOperators(sender, this, "commands.xp.success.levels", i, entityPlayerMP.getName());
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityPlayerMP.experienceTotal);
         if (bl2) {
            throw new CommandException("commands.xp.failure.widthdrawXp");
         }

         entityPlayerMP.addExperience(i);
         notifyOperators(sender, this, "commands.xp.success", i, entityPlayerMP.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
   }

   public String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }
}

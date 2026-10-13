package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase {
   private static final long b = 14028192292040L;

   @Override
   public String getCommandName() {
      return "xp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.xp.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.xp.usage");
      }

      String var6 = var2[0];
      boolean var7 = var6.endsWith("l") || var6.endsWith("L");
      if (var7 && var6.length() > 1) {
         var6 = var6.substring(0, var6.length() - 1);
      }

      int var8 = parseInt(var6);
      boolean var9 = var8 < 0;
      if (var9) {
         var8 *= -1;
      }

      EntityPlayerMP var10 = var2.length > 1 ? getPlayer(var1, var2[1]) : getCommandSenderAsPlayer(var1);
      if (var7) {
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var10.experienceLevel);
         if (var9) {
            var10.addExperienceLevel(-var8);
            notifyOperators(var1, this, "commands.xp.success.negative.levels", var8, var10.getName());
         } else {
            var10.addExperienceLevel(var8);
            notifyOperators(var1, this, "commands.xp.success.levels", var8, var10.getName());
         }
      } else {
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var10.experienceTotal);
         if (var9) {
            throw new CommandException("commands.xp.failure.widthdrawXp");
         }

         var10.addExperience(var8);
         notifyOperators(var1, this, "commands.xp.success", var8, var10.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 2 ? getListOfStringsMatchingLastWord(var2, this.getAllUsernames()) : null;
   }

   public String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 1;
   }
}

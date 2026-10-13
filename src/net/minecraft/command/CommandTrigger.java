package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger extends CommandBase {
   private static final long b = 111827173311155L;

   @Override
   public String getCommandName() {
      return "trigger";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.trigger.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 3) {
         throw new WrongUsageException("commands.trigger.usage");
      }

      EntityPlayerMP var6;
      if (var1 instanceof EntityPlayerMP) {
         var6 = (EntityPlayerMP)var1;
      } else {
         Entity var7 = var1.getCommandSenderEntity();
         if (!(var7 instanceof EntityPlayerMP)) {
            throw new CommandException("commands.trigger.invalidPlayer");
         }

         var6 = (EntityPlayerMP)var7;
      }

      Scoreboard var11 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
      ScoreObjective var8 = var11.getObjective(var2[0]);
      if (var8 != null && var8.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
         int var9 = parseInt(var2[2]);
         if (!var11.entityHasObjective(var6.getName(), var8)) {
            throw new CommandException("commands.trigger.invalidObjective", var2[0]);
         }

         Score var10 = var11.getValueFromObjective(var6.getName(), var8);
         if (var10.isLocked()) {
            throw new CommandException("commands.trigger.disabled", var2[0]);
         }

         if ("set".equals(var2[1])) {
            var10.setScorePoints(var9);
         } else {
            if (!"add".equals(var2[1])) {
               throw new CommandException("commands.trigger.invalidMode", var2[1]);
            }

            var10.increseScore(var9);
         }

         var10.setLocked(true);
         if (var6.theItemInWorldManager.isCreative()) {
            notifyOperators(var1, this, "commands.trigger.success", var2[0], var2[1], var2[2]);
         }
      } else {
         throw new CommandException("commands.trigger.invalidObjective", var2[0]);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         Scoreboard var7 = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
         ArrayList var8 = Lists.newArrayList();

         for (ScoreObjective var10 : var7.getScoreObjectives()) {
            if (var10.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
               var8.add(var10.getName());
            }
         }

         return getListOfStringsMatchingLastWord(var2, (java.lang.String[])(var8.toArray(new String[var8.size()])));
      } else {
         return var2.length == 2 ? getListOfStringsMatchingLastWord(var2, "add", "set") : null;
      }
   }
}

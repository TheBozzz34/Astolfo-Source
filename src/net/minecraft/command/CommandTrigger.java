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
   @Override
   public String getCommandName() {
      return "trigger";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.trigger.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 3) {
         throw new WrongUsageException("commands.trigger.usage");
      }

      EntityPlayerMP entityPlayerMP;
      if (sender instanceof EntityPlayerMP) {
         entityPlayerMP = (EntityPlayerMP)sender;
      } else {
         Entity entity = sender.getCommandSenderEntity();
         if (!(entity instanceof EntityPlayerMP)) {
            throw new CommandException("commands.trigger.invalidPlayer");
         }

         entityPlayerMP = (EntityPlayerMP)entity;
      }

      Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
      ScoreObjective scoreObjective = scoreboard.getObjective(args[0]);
      if (scoreObjective != null && scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
         int i = parseInt(args[2]);
         if (!scoreboard.entityHasObjective(entityPlayerMP.getName(), scoreObjective)) {
            throw new CommandException("commands.trigger.invalidObjective", args[0]);
         }

         Score score = scoreboard.getValueFromObjective(entityPlayerMP.getName(), scoreObjective);
         if (score.isLocked()) {
            throw new CommandException("commands.trigger.disabled", args[0]);
         }

         if ("set".equals(args[1])) {
            score.setScorePoints(i);
         } else {
            if (!"add".equals(args[1])) {
               throw new CommandException("commands.trigger.invalidMode", args[1]);
            }

            score.increseScore(i);
         }

         score.setLocked(true);
         if (entityPlayerMP.theItemInWorldManager.isCreative()) {
            notifyOperators(sender, this, "commands.trigger.success", args[0], args[1], args[2]);
         }
      } else {
         throw new CommandException("commands.trigger.invalidObjective", args[0]);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
         ArrayList arrayList = Lists.newArrayList();

         for (ScoreObjective scoreObjective : scoreboard.getScoreObjectives()) {
            if (scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
               arrayList.add(scoreObjective.getName());
            }
         }

         return getListOfStringsMatchingLastWord(args, (java.lang.String[])(arrayList.toArray(new String[arrayList.size()])));
      } else {
         return args.length == 2 ? getListOfStringsMatchingLastWord(args, "add", "set") : null;
      }
   }
}

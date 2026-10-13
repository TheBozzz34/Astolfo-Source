package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase {
   private static final long b = 20543841536203L;

   @Override
   public String getCommandName() {
      return "achievement";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.achievement.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.achievement.usage");
      }

      final StatBase var6 = StatList.getOneShotStat(var2[1]);
      if (var6 == null && !var2[1].equals("*")) {
         throw new CommandException("commands.achievement.unknownAchievement", var2[1]);
      }

      final EntityPlayerMP var7 = var2.length >= 3 ? getPlayer(var1, var2[2]) : getCommandSenderAsPlayer(var1);
      boolean var8 = var2[0].equalsIgnoreCase("give");
      boolean var9 = var2[0].equalsIgnoreCase("take");
      if (var8 || var9) {
         if (var6 == null) {
            if (var8) {
               for (Achievement var11 : AchievementList.achievementList) {
                  var7.triggerAchievement(var11);
               }

               notifyOperators(var1, this, "commands.achievement.give.success.all", var7.getName());
            } else if (var9) {
               for (Achievement var19 : Lists.reverse(AchievementList.achievementList)) {
                  var7.func_175145_a(var19);
               }

               notifyOperators(var1, this, "commands.achievement.take.success.all", var7.getName());
            }
         } else {
            if (var6 instanceof Achievement) {
               Achievement var18 = (Achievement)var6;
               if (var8) {
                  if (var7.getStatFile().hasAchievementUnlocked(var18)) {
                     throw new CommandException("commands.achievement.alreadyHave", var7.getName(), var6.createChatComponent());
                  }

                  ArrayList var21 = Lists.newArrayList();

                  while (var18.parentAchievement != null && !var7.getStatFile().hasAchievementUnlocked(var18.parentAchievement)) {
                     var21.add(var18.parentAchievement);
                     var18 = var18.parentAchievement;
                  }

                  for (Object var24$raw : Lists.reverse(var21)) {
                     Achievement var24 = (Achievement)var24$raw;
                     var7.triggerAchievement(var24);
                  }
               } else if (var9) {
                  if (!var7.getStatFile().hasAchievementUnlocked(var18)) {
                     throw new CommandException("commands.achievement.dontHave", var7.getName(), var6.createChatComponent());
                  }

                  ArrayList var20 = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new Predicate<Achievement>() {
                     private static final long a = 83822419777137L;

                     public boolean apply(Achievement var1) {
                        return var7.getStatFile().hasAchievementUnlocked(var1) && var1 != var6;
                     }
                  }));
                  ArrayList var12 = Lists.newArrayList(var20);

                  for (Object var14$raw : var20) {
                     Achievement var14 = (Achievement)var14$raw;
                     Achievement var15 = var14;
                     boolean var16 = false;

                     while (var15 != null) {
                        if (var15 == var6) {
                           var16 = true;
                        }

                        var15 = var15.parentAchievement;
                     }

                     if (!var16) {
                        for (Achievement var26 = var14; var26 != null; var26 = var26.parentAchievement) {
                           var12.remove(var14);
                        }
                     }
                  }

                  for (Object var25$raw : var12) {
                     Achievement var25 = (Achievement)var25$raw;
                     var7.func_175145_a(var25);
                  }
               }
            }

            if (var8) {
               var7.triggerAchievement(var6);
               notifyOperators(var1, this, "commands.achievement.give.success.one", var7.getName(), var6.createChatComponent());
            } else if (var9) {
               var7.func_175145_a(var6);
               notifyOperators(var1, this, "commands.achievement.take.success.one", var6.createChatComponent(), var7.getName());
            }
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return getListOfStringsMatchingLastWord(var2, "give", "take");
      }

      if (var2.length != 2) {
         return var2.length == 3 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
      }

      ArrayList var7 = Lists.newArrayList();

      for (StatBase var9 : StatList.allStats) {
         var7.add(var9.statId);
      }

      return getListOfStringsMatchingLastWord(var2, var7);
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 2;
   }
}

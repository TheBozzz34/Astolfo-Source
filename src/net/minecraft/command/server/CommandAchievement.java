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
   @Override
   public String getCommandName() {
      return "achievement";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.achievement.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.achievement.usage");
      }

      final StatBase statBase = StatList.getOneShotStat(args[1]);
      if (statBase == null && !args[1].equals("*")) {
         throw new CommandException("commands.achievement.unknownAchievement", args[1]);
      }

      final EntityPlayerMP entityPlayerMP = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
      boolean bl = args[0].equalsIgnoreCase("give");
      boolean bl2 = args[0].equalsIgnoreCase("take");
      if (bl || bl2) {
         if (statBase == null) {
            if (bl) {
               for (Achievement achievement2 : AchievementList.achievementList) {
                  entityPlayerMP.triggerAchievement(achievement2);
               }

               notifyOperators(sender, this, "commands.achievement.give.success.all", entityPlayerMP.getName());
            } else if (bl2) {
               for (Achievement achievement3 : Lists.reverse(AchievementList.achievementList)) {
                  entityPlayerMP.func_175145_a(achievement3);
               }

               notifyOperators(sender, this, "commands.achievement.take.success.all", entityPlayerMP.getName());
            }
         } else {
            if (statBase instanceof Achievement) {
               Achievement achievement4 = (Achievement)statBase;
               if (bl) {
                  if (entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement4)) {
                     throw new CommandException("commands.achievement.alreadyHave", entityPlayerMP.getName(), statBase.createChatComponent());
                  }

                  ArrayList arrayList = Lists.newArrayList();

                  while (achievement4.parentAchievement != null && !entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement4.parentAchievement)) {
                     arrayList.add(achievement4.parentAchievement);
                     achievement4 = achievement4.parentAchievement;
                  }

                  for (Object object : Lists.reverse(arrayList)) {
                     Achievement achievement5 = (Achievement)object;
                     entityPlayerMP.triggerAchievement(achievement5);
                  }
               } else if (bl2) {
                  if (!entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement4)) {
                     throw new CommandException("commands.achievement.dontHave", entityPlayerMP.getName(), statBase.createChatComponent());
                  }

                  ArrayList arrayList2 = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new Predicate<Achievement>() {
                     public boolean apply(Achievement achievement) {
                        return entityPlayerMP.getStatFile().hasAchievementUnlocked(achievement) && achievement != statBase;
                     }
                  }));
                  ArrayList arrayList3 = Lists.newArrayList(arrayList2);

                  for (Object object2 : arrayList2) {
                     Achievement achievement6 = (Achievement)object2;
                     Achievement achievement7 = achievement6;
                     boolean bl3 = false;

                     while (achievement7 != null) {
                        if (achievement7 == statBase) {
                           bl3 = true;
                        }

                        achievement7 = achievement7.parentAchievement;
                     }

                     if (!bl3) {
                        for (Achievement achievement8 = achievement6; achievement8 != null; achievement8 = achievement8.parentAchievement) {
                           arrayList3.remove(achievement6);
                        }
                     }
                  }

                  for (Object object3 : arrayList3) {
                     Achievement achievement9 = (Achievement)object3;
                     entityPlayerMP.func_175145_a(achievement9);
                  }
               }
            }

            if (bl) {
               entityPlayerMP.triggerAchievement(statBase);
               notifyOperators(sender, this, "commands.achievement.give.success.one", entityPlayerMP.getName(), statBase.createChatComponent());
            } else if (bl2) {
               entityPlayerMP.func_175145_a(statBase);
               notifyOperators(sender, this, "commands.achievement.take.success.one", statBase.createChatComponent(), entityPlayerMP.getName());
            }
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, "give", "take");
      }

      if (args.length != 2) {
         return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
      }

      ArrayList arrayList = Lists.newArrayList();

      for (StatBase statBase : StatList.allStats) {
         arrayList.add(statBase.statId);
      }

      return getListOfStringsMatchingLastWord(args, arrayList);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 2;
   }
}

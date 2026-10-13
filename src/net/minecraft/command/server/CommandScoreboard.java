package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
   private static final long b = 15854723139209L;

   @Override
   public String getCommandName() {
      return "scoreboard";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (!this.func_175780_b(var1, var2)) {
         if (var2.length < 1) {
            throw new WrongUsageException("commands.scoreboard.usage");
         }

         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               this.listObjectives(var1);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
               }

               this.addObjective(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
               }

               this.removeObjective(var1, var2[2]);
            } else {
               if (!var2[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage");
               }

               if (var2.length != 3 && var2.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
               }

               this.setObjectiveDisplay(var1, var2, 2);
            }
         } else if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage");
               }

               this.listPlayers(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage");
               }

               this.setPlayer(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage");
               }

               this.setPlayer(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("set")) {
               if (var2.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage");
               }

               this.setPlayer(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("reset")) {
               if (var2.length != 3 && var2.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage");
               }

               this.resetPlayers(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("enable")) {
               if (var2.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.enable.usage");
               }

               this.func_175779_n(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("test")) {
               if (var2.length != 5 && var2.length != 6) {
                  throw new WrongUsageException("commands.scoreboard.players.test.usage");
               }

               this.func_175781_o(var1, var2, 2);
            } else {
               if (!var2[1].equalsIgnoreCase("operation")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage");
               }

               if (var2.length != 7) {
                  throw new WrongUsageException("commands.scoreboard.players.operation.usage");
               }

               this.func_175778_p(var1, var2, 2);
            }
         } else {
            if (!var2[0].equalsIgnoreCase("teams")) {
               throw new WrongUsageException("commands.scoreboard.usage");
            }

            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage");
               }

               this.listTeams(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage");
               }

               this.addTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
               }

               this.removeTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("empty")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
               }

               this.emptyTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length < 4 && (var2.length != 3 || !(var1 instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage");
               }

               this.joinTeam(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("leave")) {
               if (var2.length < 3 && !(var1 instanceof EntityPlayer)) {
                  throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
               }

               this.leaveTeam(var1, var2, 2);
            } else {
               if (!var2[1].equalsIgnoreCase("option")) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage");
               }

               if (var2.length != 4 && var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               this.setTeamOption(var1, var2, 2);
            }
         }
      }
   }

   private boolean func_175780_b(ICommandSender var1, String[] var2) throws CommandException {
      int var6 = -1;

      for (int var7 = 0; var7 < var2.length; var7++) {
         if (this.isUsernameIndex(var2, var7) && "*".equals(var2[var7])) {
            if (var6 >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            var6 = var7;
         }
      }

      if (var6 < 0) {
         return false;
      }

      ArrayList var15 = Lists.newArrayList(this.getScoreboard().getObjectiveNames());
      String var8 = var2[var6];
      ArrayList var9 = Lists.newArrayList();

      for (Object var11$raw : var15) {
         String var11 = (String)var11$raw;
         var2[var6] = var11;

         try {
            this.processCommand(var1, var2);
            var9.add(var11);
         } catch (CommandException var14) {
            ChatComponentTranslation var13 = new ChatComponentTranslation(var14.getMessage(), var14.getErrorObjects());
            var13.getChatStyle().setColor(EnumChatFormatting.RED);
            var1.addChatMessage(var13);
         }
      }

      var2[var6] = var8;
      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var9.size());
      if (var9.size() == 0) {
         throw new WrongUsageException("commands.scoreboard.allMatchesFailed");
      } else {
         return true;
      }
   }

   public Scoreboard getScoreboard() {
      return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
   }

   public ScoreObjective getObjective(String var1, boolean var2) throws CommandException {
      Scoreboard var6 = this.getScoreboard();
      ScoreObjective var7 = var6.getObjective(var1);
      if (var7 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", var1);
      } else if (var2 && var7.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", var1);
      } else {
         return var7;
      }
   }

   public ScorePlayerTeam getTeam(String var1) throws CommandException {
      Scoreboard var5 = this.getScoreboard();
      ScorePlayerTeam var6 = var5.getTeam(var1);
      if (var6 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", var1);
      } else {
         return var6;
      }
   }

   public void addObjective(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var7 = var2[var3++];
      String var8 = var2[var3++];
      Scoreboard var9 = this.getScoreboard();
      IScoreObjectiveCriteria var10 = IScoreObjectiveCriteria.INSTANCES.get(var8);
      if (var10 == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", var8);
      }

      if (var9.getObjective(var7) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", var7);
      }

      if (var7.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", var7, 16);
      }

      if (var7.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
      }

      if (var2.length > var3) {
         String var11 = getChatComponentFromNthArg(var1, var2, var3).getUnformattedText();
         if (var11.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", var11, 32);
         }

         if (var11.length() > 0) {
            var9.addScoreObjective(var7, var10).setDisplayName(var11);
         } else {
            var9.addScoreObjective(var7, var10);
         }
      } else {
         var9.addScoreObjective(var7, var10);
      }

      notifyOperators(var1, this, "commands.scoreboard.objectives.add.success", var7);
   }

   public void addTeam(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var7 = var2[var3++];
      Scoreboard var8 = this.getScoreboard();
      if (var8.getTeam(var7) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", var7);
      }

      if (var7.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", var7, 16);
      }

      if (var7.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage");
      }

      if (var2.length > var3) {
         String var9 = getChatComponentFromNthArg(var1, var2, var3).getUnformattedText();
         if (var9.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", var9, 32);
         }

         if (var9.length() > 0) {
            var8.createTeam(var7).setTeamName(var9);
         } else {
            var8.createTeam(var7);
         }
      } else {
         var8.createTeam(var7);
      }

      notifyOperators(var1, this, "commands.scoreboard.teams.add.success", var7);
   }

   public void setTeamOption(ICommandSender var1, String[] var2, int var3) throws CommandException {
      ScorePlayerTeam var7 = this.getTeam(var2[var3++]);
      if (var7 != null) {
         String var8 = var2[var3++].toLowerCase();
         if (!var8.equalsIgnoreCase("color")
            && !var8.equalsIgnoreCase("friendlyfire")
            && !var8.equalsIgnoreCase("seeFriendlyInvisibles")
            && !var8.equalsIgnoreCase("nametagVisibility")
            && !var8.equalsIgnoreCase("deathMessageVisibility")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage");
         }

         if (var2.length == 4) {
            if (var8.equalsIgnoreCase("color")) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", var8, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            if (!var8.equalsIgnoreCase("friendlyfire") && !var8.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var8.equalsIgnoreCase("nametagVisibility") && !var8.equalsIgnoreCase("deathMessageVisibility")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceStringFromCollection(Arrays.asList("true", "false")));
         }

         String var9 = var2[var3];
         if (var8.equalsIgnoreCase("color")) {
            EnumChatFormatting var10 = EnumChatFormatting.getValueByName(var9);
            if (var10 == null || var10.isFancyStyling()) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", var8, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            var7.setChatFormat(var10);
            var7.setNamePrefix(var10.toString());
            var7.setNameSuffix(EnumChatFormatting.RESET.toString());
         } else if (var8.equalsIgnoreCase("friendlyfire")) {
            if (!var9.equalsIgnoreCase("true") && !var9.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            var7.setAllowFriendlyFire(var9.equalsIgnoreCase("true"));
         } else if (var8.equalsIgnoreCase("seeFriendlyInvisibles")) {
            if (!var9.equalsIgnoreCase("true") && !var9.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            var7.setSeeFriendlyInvisiblesEnabled(var9.equalsIgnoreCase("true"));
         } else if (var8.equalsIgnoreCase("nametagVisibility")) {
            Team.EnumVisible var13 = Team.EnumVisible.func_178824_a(var9);
            if (var13 == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            var7.setNameTagVisibility(var13);
         } else if (var8.equalsIgnoreCase("deathMessageVisibility")) {
            Team.EnumVisible var14 = Team.EnumVisible.func_178824_a(var9);
            if (var14 == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var8, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            var7.setDeathMessageVisibility(var14);
         }

         notifyOperators(var1, this, "commands.scoreboard.teams.option.success", var8, var7.getRegisteredName(), var9);
      }
   }

   public void removeTeam(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      ScorePlayerTeam var8 = this.getTeam(var2[var3]);
      if (var8 != null) {
         var7.removeTeam(var8);
         notifyOperators(var1, this, "commands.scoreboard.teams.remove.success", var8.getRegisteredName());
      }
   }

   public void listTeams(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      if (var2.length > var3) {
         ScorePlayerTeam var8 = this.getTeam(var2[var3]);
         if (var8 == null) {
            return;
         }

         Collection var9 = var8.getMembershipCollection();
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var9.size());
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", var8.getRegisteredName());
         }

         ChatComponentTranslation var10 = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", var9.size(), var8.getRegisteredName());
         var10.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         var1.addChatMessage(var10);
         var1.addChatMessage(new ChatComponentText(joinNiceString(var9.toArray())));
      } else {
         Collection var12 = var7.getTeams();
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var12.size());
         if (var12.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatComponentTranslation var13 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", var12.size());
         var13.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         var1.addChatMessage(var13);

         for (Object var11$raw : var12) {
            ScorePlayerTeam var11 = (ScorePlayerTeam)var11$raw;
            var1.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.teams.list.entry", var11.getRegisteredName(), var11.getTeamName(), var11.getMembershipCollection().size()
               )
            );
         }
      }
   }

   public void joinTeam(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      String var8 = var2[var3++];
      HashSet var9 = Sets.newHashSet();
      HashSet var10 = Sets.newHashSet();
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         String var16 = getCommandSenderAsPlayer(var1).getName();
         if (var7.addPlayerToTeam(var16, var8)) {
            var9.add(var16);
         } else {
            var10.add(var16);
         }
      } else {
         while (var3 < var2.length) {
            String var11 = var2[var3++];
            if (var11.startsWith("@")) {
               for (Entity var13 : func_175763_c(var1, var11)) {
                  String var14 = getEntityName(var1, var13.getUniqueID().toString());
                  if (var7.addPlayerToTeam(var14, var8)) {
                     var9.add(var14);
                  } else {
                     var10.add(var14);
                  }
               }
            } else {
               String var12 = getEntityName(var1, var11);
               if (var7.addPlayerToTeam(var12, var8)) {
                  var9.add(var12);
               } else {
                  var10.add(var12);
               }
            }
         }
      }

      if (!var9.isEmpty()) {
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var9.size());
         notifyOperators(var1, this, "commands.scoreboard.teams.join.success", var9.size(), var8, joinNiceString(var9.toArray(new String[var9.size()])));
      }

      if (!var10.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", var10.size(), var8, joinNiceString(var10.toArray(new String[var10.size()])));
      }
   }

   public void leaveTeam(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      HashSet var8 = Sets.newHashSet();
      HashSet var9 = Sets.newHashSet();
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         String var14 = getCommandSenderAsPlayer(var1).getName();
         if (var7.removePlayerFromTeams(var14)) {
            var8.add(var14);
         } else {
            var9.add(var14);
         }
      } else {
         while (var3 < var2.length) {
            String var10 = var2[var3++];
            if (var10.startsWith("@")) {
               for (Entity var12 : func_175763_c(var1, var10)) {
                  String var13 = getEntityName(var1, var12.getUniqueID().toString());
                  if (var7.removePlayerFromTeams(var13)) {
                     var8.add(var13);
                  } else {
                     var9.add(var13);
                  }
               }
            } else {
               String var11 = getEntityName(var1, var10);
               if (var7.removePlayerFromTeams(var11)) {
                  var8.add(var11);
               } else {
                  var9.add(var11);
               }
            }
         }
      }

      if (!var8.isEmpty()) {
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var8.size());
         notifyOperators(var1, this, "commands.scoreboard.teams.leave.success", var8.size(), joinNiceString(var8.toArray(new String[var8.size()])));
      }

      if (!var9.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", var9.size(), joinNiceString(var9.toArray(new String[var9.size()])));
      }
   }

   public void emptyTeam(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      ScorePlayerTeam var8 = this.getTeam(var2[var3]);
      if (var8 != null) {
         ArrayList var9 = Lists.newArrayList(var8.getMembershipCollection());
         var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var9.size());
         if (var9.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", var8.getRegisteredName());
         }

         for (Object var11$raw : var9) {
            String var11 = (String)var11$raw;
            var7.removePlayerFromTeam(var11, var8);
         }

         notifyOperators(var1, this, "commands.scoreboard.teams.empty.success", var9.size(), var8.getRegisteredName());
      }
   }

   public void removeObjective(ICommandSender var1, String var2) throws CommandException {
      Scoreboard var5 = this.getScoreboard();
      ScoreObjective var6 = this.getObjective(var2, false);
      var5.removeObjective(var6);
      notifyOperators(var1, this, "commands.scoreboard.objectives.remove.success", var2);
   }

   public void listObjectives(ICommandSender var1) throws CommandException {
      Scoreboard var5 = this.getScoreboard();
      Collection var6 = var5.getScoreObjectives();
      if (var6.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      }

      ChatComponentTranslation var7 = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", var6.size());
      var7.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      var1.addChatMessage(var7);

      for (Object var9$raw : var6) {
         ScoreObjective var9 = (ScoreObjective)var9$raw;
         var1.addChatMessage(
            new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", var9.getName(), var9.getDisplayName(), var9.getCriteria().getName())
         );
      }
   }

   public void setObjectiveDisplay(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      String var8 = var2[var3++];
      int var9 = Scoreboard.getObjectiveDisplaySlotNumber(var8);
      ScoreObjective var10 = null;
      if (var2.length == 4) {
         var10 = this.getObjective(var2[var3], false);
      }

      if (var9 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", var8);
      }

      var7.setObjectiveInDisplaySlot(var9, var10);
      if (var10 != null) {
         notifyOperators(var1, this, "commands.scoreboard.objectives.setdisplay.successSet", Scoreboard.getObjectiveDisplaySlot(var9), var10.getName());
      } else {
         notifyOperators(var1, this, "commands.scoreboard.objectives.setdisplay.successCleared", Scoreboard.getObjectiveDisplaySlot(var9));
      }
   }

   public void listPlayers(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      if (var2.length > var3) {
         String var8 = getEntityName(var1, var2[var3]);
         Map var9 = var7.getObjectivesForEntity(var8);
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var9.size());
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", var8);
         }

         ChatComponentTranslation var10 = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", var9.size(), var8);
         var10.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         var1.addChatMessage(var10);

         for (Object var12$raw : var9.values()) {
            Score var12 = (Score)var12$raw;
            var1.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.players.list.player.entry",
                  var12.getScorePoints(),
                  var12.getObjective().getDisplayName(),
                  var12.getObjective().getName()
               )
            );
         }
      } else {
         Collection var13 = var7.getObjectiveNames();
         var1.setCommandStat(CommandResultStats.Type.QUERY_RESULT, var13.size());
         if (var13.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatComponentTranslation var14 = new ChatComponentTranslation("commands.scoreboard.players.list.count", var13.size());
         var14.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         var1.addChatMessage(var14);
         var1.addChatMessage(new ChatComponentText(joinNiceString(var13.toArray())));
      }
   }

   public void setPlayer(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var7 = var2[var3 - 1];
      int var8 = var3;
      String var9 = getEntityName(var1, var2[var3++]);
      if (var9.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", var9, 40);
      }

      ScoreObjective var10 = this.getObjective(var2[var3++], true);
      int var11 = var7.equalsIgnoreCase("set") ? parseInt(var2[var3++]) : parseInt(var2[var3++], 0);
      if (var2.length > var3) {
         Entity var12 = getEntity(var1, var2[var8]);

         try {
            NBTTagCompound var13 = JsonToNBT.getTagFromJson(buildString(var2, var3));
            NBTTagCompound var14 = new NBTTagCompound();
            var12.writeToNBT(var14);
            if (!NBTUtil.func_181123_a(var13, var14, true)) {
               throw new CommandException("commands.scoreboard.players.set.tagMismatch", var9);
            }
         } catch (NBTException var15) {
            throw new CommandException("commands.scoreboard.players.set.tagError", var15.getMessage());
         }
      }

      Scoreboard var19 = this.getScoreboard();
      Score var20 = var19.getValueFromObjective(var9, var10);
      if (var7.equalsIgnoreCase("set")) {
         var20.setScorePoints(var11);
      } else if (var7.equalsIgnoreCase("add")) {
         var20.increseScore(var11);
      } else {
         var20.decreaseScore(var11);
      }

      notifyOperators(var1, this, "commands.scoreboard.players.set.success", var10.getName(), var9, var20.getScorePoints());
   }

   public void resetPlayers(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      String var8 = getEntityName(var1, var2[var3++]);
      if (var2.length > var3) {
         ScoreObjective var9 = this.getObjective(var2[var3++], false);
         var7.removeObjectiveFromEntity(var8, var9);
         notifyOperators(var1, this, "commands.scoreboard.players.resetscore.success", var9.getName(), var8);
      } else {
         var7.removeObjectiveFromEntity(var8, (ScoreObjective)null);
         notifyOperators(var1, this, "commands.scoreboard.players.reset.success", var8);
      }
   }

   public void func_175779_n(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var6 = this.getScoreboard();
      String var7 = getPlayerName(var1, var2[var3++]);
      if (var7.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", var7, 40);
      }

      ScoreObjective var8 = this.getObjective(var2[var3], false);
      if (var8.getCriteria() != IScoreObjectiveCriteria.TRIGGER) {
         throw new CommandException("commands.scoreboard.players.enable.noTrigger", var8.getName());
      }

      Score var9 = var6.getValueFromObjective(var7, var8);
      var9.setLocked(false);
      notifyOperators(var1, this, "commands.scoreboard.players.enable.success", var8.getName(), var7);
   }

   public void func_175781_o(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      String var8 = getEntityName(var1, var2[var3++]);
      if (var8.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", var8, 40);
      }

      ScoreObjective var9 = this.getObjective(var2[var3++], false);
      if (!var7.entityHasObjective(var8, var9)) {
         throw new CommandException("commands.scoreboard.players.test.notFound", var9.getName(), var8);
      }

      int var10 = var2[var3].equals("*") ? Integer.MIN_VALUE : parseInt(var2[var3]);
      var3++;
      int var11 = var3 < var2.length && !var2[var3].equals("*") ? parseInt(var2[var3], var10) : Integer.MAX_VALUE;
      Score var12 = var7.getValueFromObjective(var8, var9);
      if (var12.getScorePoints() >= var10 && var12.getScorePoints() <= var11) {
         notifyOperators(var1, this, "commands.scoreboard.players.test.success", var12.getScorePoints(), var10, var11);
      } else {
         throw new CommandException("commands.scoreboard.players.test.failed", var12.getScorePoints(), var10, var11);
      }
   }

   public void func_175778_p(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var7 = this.getScoreboard();
      String var8 = getEntityName(var1, var2[var3++]);
      ScoreObjective var9 = this.getObjective(var2[var3++], true);
      String var10 = var2[var3++];
      String var11 = getEntityName(var1, var2[var3++]);
      ScoreObjective var12 = this.getObjective(var2[var3], false);
      if (var8.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", var8, 40);
      }

      if (var11.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", var11, 40);
      }

      Score var13 = var7.getValueFromObjective(var8, var9);
      if (!var7.entityHasObjective(var11, var12)) {
         throw new CommandException("commands.scoreboard.players.operation.notFound", var12.getName(), var11);
      }

      Score var14 = var7.getValueFromObjective(var11, var12);
      if (var10.equals("+=")) {
         var13.setScorePoints(var13.getScorePoints() + var14.getScorePoints());
      } else if (var10.equals("-=")) {
         var13.setScorePoints(var13.getScorePoints() - var14.getScorePoints());
      } else if (var10.equals("*=")) {
         var13.setScorePoints(var13.getScorePoints() * var14.getScorePoints());
      } else if (var10.equals("/=")) {
         if (var14.getScorePoints() != 0) {
            var13.setScorePoints(var13.getScorePoints() / var14.getScorePoints());
         }
      } else if (var10.equals("%=")) {
         if (var14.getScorePoints() != 0) {
            var13.setScorePoints(var13.getScorePoints() % var14.getScorePoints());
         }
      } else if (var10.equals("=")) {
         var13.setScorePoints(var14.getScorePoints());
      } else if (var10.equals("<")) {
         var13.setScorePoints(Math.min(var13.getScorePoints(), var14.getScorePoints()));
      } else if (var10.equals(">")) {
         var13.setScorePoints(Math.max(var13.getScorePoints(), var14.getScorePoints()));
      } else {
         if (!var10.equals("><")) {
            throw new CommandException("commands.scoreboard.players.operation.invalidOperation", var10);
         }

         int var15 = var13.getScorePoints();
         var13.setScorePoints(var14.getScorePoints());
         var14.setScorePoints(var15);
      }

      notifyOperators(var1, this, "commands.scoreboard.players.operation.success");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return getListOfStringsMatchingLastWord(var2, "objectives", "players", "teams");
      }

      if (var2[0].equalsIgnoreCase("objectives")) {
         if (var2.length == 2) {
            return getListOfStringsMatchingLastWord(var2, "list", "add", "remove", "setdisplay");
         }

         if (var2[1].equalsIgnoreCase("add")) {
            if (var2.length == 4) {
               Set var7 = IScoreObjectiveCriteria.INSTANCES.keySet();
               return getListOfStringsMatchingLastWord(var2, var7);
            }
         } else if (var2[1].equalsIgnoreCase("remove")) {
            if (var2.length == 3) {
               return getListOfStringsMatchingLastWord(var2, this.func_147184_a(false));
            }
         } else if (var2[1].equalsIgnoreCase("setdisplay")) {
            if (var2.length == 3) {
               return getListOfStringsMatchingLastWord(var2, Scoreboard.getDisplaySlotStrings());
            }

            if (var2.length == 4) {
               return getListOfStringsMatchingLastWord(var2, this.func_147184_a(false));
            }
         }
      } else if (var2[0].equalsIgnoreCase("players")) {
         if (var2.length == 2) {
            return getListOfStringsMatchingLastWord(var2, "set", "add", "remove", "reset", "list", "enable", "test", "operation");
         }

         if (!var2[1].equalsIgnoreCase("set") && !var2[1].equalsIgnoreCase("add") && !var2[1].equalsIgnoreCase("remove") && !var2[1].equalsIgnoreCase("reset")) {
            if (var2[1].equalsIgnoreCase("enable")) {
               if (var2.length == 3) {
                  return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
               }

               if (var2.length == 4) {
                  return getListOfStringsMatchingLastWord(var2, this.func_175782_e());
               }
            } else if (!var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("test")) {
               if (var2[1].equalsIgnoreCase("operation")) {
                  if (var2.length == 3) {
                     return getListOfStringsMatchingLastWord(var2, this.getScoreboard().getObjectiveNames());
                  }

                  if (var2.length == 4) {
                     return getListOfStringsMatchingLastWord(var2, this.func_147184_a(true));
                  }

                  if (var2.length == 5) {
                     return getListOfStringsMatchingLastWord(var2, "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");
                  }

                  if (var2.length == 6) {
                     return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
                  }

                  if (var2.length == 7) {
                     return getListOfStringsMatchingLastWord(var2, this.func_147184_a(false));
                  }
               }
            } else {
               if (var2.length == 3) {
                  return getListOfStringsMatchingLastWord(var2, this.getScoreboard().getObjectiveNames());
               }

               if (var2.length == 4 && var2[1].equalsIgnoreCase("test")) {
                  return getListOfStringsMatchingLastWord(var2, this.func_147184_a(false));
               }
            }
         } else {
            if (var2.length == 3) {
               return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
            }

            if (var2.length == 4) {
               return getListOfStringsMatchingLastWord(var2, this.func_147184_a(true));
            }
         }
      } else if (var2[0].equalsIgnoreCase("teams")) {
         if (var2.length == 2) {
            return getListOfStringsMatchingLastWord(var2, "add", "remove", "join", "leave", "empty", "list", "option");
         }

         if (var2[1].equalsIgnoreCase("join")) {
            if (var2.length == 3) {
               return getListOfStringsMatchingLastWord(var2, this.getScoreboard().getTeamNames());
            }

            if (var2.length >= 4) {
               return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
            }
         } else {
            if (var2[1].equalsIgnoreCase("leave")) {
               return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
            }

            if (!var2[1].equalsIgnoreCase("empty") && !var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("remove")) {
               if (var2[1].equalsIgnoreCase("option")) {
                  if (var2.length == 3) {
                     return getListOfStringsMatchingLastWord(var2, this.getScoreboard().getTeamNames());
                  }

                  if (var2.length == 4) {
                     return getListOfStringsMatchingLastWord(
                        var2, "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"
                     );
                  }

                  if (var2.length == 5) {
                     if (var2[3].equalsIgnoreCase("color")) {
                        return getListOfStringsMatchingLastWord(var2, EnumChatFormatting.getValidValues(true, false));
                     }

                     if (var2[3].equalsIgnoreCase("nametagVisibility") || var2[3].equalsIgnoreCase("deathMessageVisibility")) {
                        return getListOfStringsMatchingLastWord(var2, Team.EnumVisible.func_178825_a());
                     }

                     if (var2[3].equalsIgnoreCase("friendlyfire") || var2[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                        return getListOfStringsMatchingLastWord(var2, "true", "false");
                     }
                  }
               }
            } else if (var2.length == 3) {
               return getListOfStringsMatchingLastWord(var2, this.getScoreboard().getTeamNames());
            }
         }
      }

      return null;
   }

   public List<String> func_147184_a(boolean var1) {
      Collection var5 = this.getScoreboard().getScoreObjectives();
      ArrayList var6 = Lists.newArrayList();

      for (Object var8$raw : var5) {
         ScoreObjective var8 = (ScoreObjective)var8$raw;
         if (!var1 || !var8.getCriteria().isReadOnly()) {
            var6.add(var8.getName());
         }
      }

      return var6;
   }

   public List<String> func_175782_e() {
      Collection var4 = this.getScoreboard().getScoreObjectives();
      ArrayList var5 = Lists.newArrayList();

      for (Object var7$raw : var4) {
         ScoreObjective var7 = (ScoreObjective)var7$raw;
         if (var7.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            var5.add(var7.getName());
         }
      }

      return var5;
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return !var1[0].equalsIgnoreCase("players")
         ? (var1[0].equalsIgnoreCase("teams") ? var2 == 2 : false)
         : (var1.length > 1 && var1[1].equalsIgnoreCase("operation") ? var2 == 2 || var2 == 5 : var2 == 2);
   }
}

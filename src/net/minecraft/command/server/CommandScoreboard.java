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
   @Override
   public String getCommandName() {
      return "scoreboard";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (!this.func_175780_b(sender, args)) {
         if (args.length < 1) {
            throw new WrongUsageException("commands.scoreboard.usage");
         }

         if (args[0].equalsIgnoreCase("objectives")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               this.listObjectives(sender);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
               }

               this.addObjective(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
               }

               this.removeObjective(sender, args[2]);
            } else {
               if (!args[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage");
               }

               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
               }

               this.setObjectiveDisplay(sender, args, 2);
            }
         } else if (args[0].equalsIgnoreCase("players")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage");
               }

               this.listPlayers(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("set")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("reset")) {
               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage");
               }

               this.resetPlayers(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("enable")) {
               if (args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.enable.usage");
               }

               this.func_175779_n(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("test")) {
               if (args.length != 5 && args.length != 6) {
                  throw new WrongUsageException("commands.scoreboard.players.test.usage");
               }

               this.func_175781_o(sender, args, 2);
            } else {
               if (!args[1].equalsIgnoreCase("operation")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage");
               }

               if (args.length != 7) {
                  throw new WrongUsageException("commands.scoreboard.players.operation.usage");
               }

               this.func_175778_p(sender, args, 2);
            }
         } else {
            if (!args[0].equalsIgnoreCase("teams")) {
               throw new WrongUsageException("commands.scoreboard.usage");
            }

            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage");
               }

               this.listTeams(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage");
               }

               this.addTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
               }

               this.removeTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("empty")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
               }

               this.emptyTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("join")) {
               if (args.length < 4 && (args.length != 3 || !(sender instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage");
               }

               this.joinTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("leave")) {
               if (args.length < 3 && !(sender instanceof EntityPlayer)) {
                  throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
               }

               this.leaveTeam(sender, args, 2);
            } else {
               if (!args[1].equalsIgnoreCase("option")) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage");
               }

               if (args.length != 4 && args.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               this.setTeamOption(sender, args, 2);
            }
         }
      }
   }

   private boolean func_175780_b(ICommandSender iCommandSender, String[] strings) throws CommandException {
      int i = -1;

      for (int j = 0; j < strings.length; j++) {
         if (this.isUsernameIndex(strings, j) && "*".equals(strings[j])) {
            if (i >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            i = j;
         }
      }

      if (i < 0) {
         return false;
      }

      ArrayList arrayList = Lists.newArrayList(this.getScoreboard().getObjectiveNames());
      String string = strings[i];
      ArrayList arrayList2 = Lists.newArrayList();

      for (Object object : arrayList) {
         String string2 = (String)object;
         strings[i] = string2;

         try {
            this.processCommand(iCommandSender, strings);
            arrayList2.add(string2);
         } catch (CommandException commandException) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(commandException.getMessage(), commandException.getErrorObjects());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            iCommandSender.addChatMessage(chatComponentTranslation);
         }
      }

      strings[i] = string;
      iCommandSender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, arrayList2.size());
      if (arrayList2.size() == 0) {
         throw new WrongUsageException("commands.scoreboard.allMatchesFailed");
      } else {
         return true;
      }
   }

   public Scoreboard getScoreboard() {
      return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
   }

   public ScoreObjective getObjective(String name, boolean edit) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScoreObjective scoreObjective = scoreboard2.getObjective(name);
      if (scoreObjective == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", name);
      } else if (edit && scoreObjective.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", name);
      } else {
         return scoreObjective;
      }
   }

   public ScorePlayerTeam getTeam(String name) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScorePlayerTeam scorePlayerTeam = scoreboard2.getTeam(name);
      if (scorePlayerTeam == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", name);
      } else {
         return scorePlayerTeam;
      }
   }

   public void addObjective(ICommandSender sender, String[] args, int index) throws CommandException {
      String string = args[index++];
      String string2 = args[index++];
      Scoreboard scoreboard2 = this.getScoreboard();
      IScoreObjectiveCriteria iScoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(string2);
      if (iScoreObjectiveCriteria == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", string2);
      }

      if (scoreboard2.getObjective(string) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", string);
      }

      if (string.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", string, 16);
      }

      if (string.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
      }

      if (args.length > index) {
         String string3 = getChatComponentFromNthArg(sender, args, index).getUnformattedText();
         if (string3.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", string3, 32);
         }

         if (string3.length() > 0) {
            scoreboard2.addScoreObjective(string, iScoreObjectiveCriteria).setDisplayName(string3);
         } else {
            scoreboard2.addScoreObjective(string, iScoreObjectiveCriteria);
         }
      } else {
         scoreboard2.addScoreObjective(string, iScoreObjectiveCriteria);
      }

      notifyOperators(sender, this, "commands.scoreboard.objectives.add.success", string);
   }

   public void addTeam(ICommandSender sender, String[] args, int index) throws CommandException {
      String string = args[index++];
      Scoreboard scoreboard2 = this.getScoreboard();
      if (scoreboard2.getTeam(string) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", string);
      }

      if (string.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", string, 16);
      }

      if (string.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage");
      }

      if (args.length > index) {
         String string2 = getChatComponentFromNthArg(sender, args, index).getUnformattedText();
         if (string2.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", string2, 32);
         }

         if (string2.length() > 0) {
            scoreboard2.createTeam(string).setTeamName(string2);
         } else {
            scoreboard2.createTeam(string);
         }
      } else {
         scoreboard2.createTeam(string);
      }

      notifyOperators(sender, this, "commands.scoreboard.teams.add.success", string);
   }

   public void setTeamOption(ICommandSender sender, String[] args, int index) throws CommandException {
      ScorePlayerTeam scorePlayerTeam = this.getTeam(args[index++]);
      if (scorePlayerTeam != null) {
         String string = args[index++].toLowerCase();
         if (!string.equalsIgnoreCase("color")
            && !string.equalsIgnoreCase("friendlyfire")
            && !string.equalsIgnoreCase("seeFriendlyInvisibles")
            && !string.equalsIgnoreCase("nametagVisibility")
            && !string.equalsIgnoreCase("deathMessageVisibility")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage");
         }

         if (args.length == 4) {
            if (string.equalsIgnoreCase("color")) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", string, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            if (!string.equalsIgnoreCase("friendlyfire") && !string.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!string.equalsIgnoreCase("nametagVisibility") && !string.equalsIgnoreCase("deathMessageVisibility")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceStringFromCollection(Arrays.asList("true", "false")));
         }

         String string2 = args[index];
         if (string.equalsIgnoreCase("color")) {
            EnumChatFormatting enumChatFormatting = EnumChatFormatting.getValueByName(string2);
            if (enumChatFormatting == null || enumChatFormatting.isFancyStyling()) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", string, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            scorePlayerTeam.setChatFormat(enumChatFormatting);
            scorePlayerTeam.setNamePrefix(enumChatFormatting.toString());
            scorePlayerTeam.setNameSuffix(EnumChatFormatting.RESET.toString());
         } else if (string.equalsIgnoreCase("friendlyfire")) {
            if (!string2.equalsIgnoreCase("true") && !string2.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            scorePlayerTeam.setAllowFriendlyFire(string2.equalsIgnoreCase("true"));
         } else if (string.equalsIgnoreCase("seeFriendlyInvisibles")) {
            if (!string2.equalsIgnoreCase("true") && !string2.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            scorePlayerTeam.setSeeFriendlyInvisiblesEnabled(string2.equalsIgnoreCase("true"));
         } else if (string.equalsIgnoreCase("nametagVisibility")) {
            Team.EnumVisible enumVisible = Team.EnumVisible.func_178824_a(string2);
            if (enumVisible == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            scorePlayerTeam.setNameTagVisibility(enumVisible);
         } else if (string.equalsIgnoreCase("deathMessageVisibility")) {
            Team.EnumVisible enumVisible2 = Team.EnumVisible.func_178824_a(string2);
            if (enumVisible2 == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", string, joinNiceString(Team.EnumVisible.func_178825_a()));
            }

            scorePlayerTeam.setDeathMessageVisibility(enumVisible2);
         }

         notifyOperators(sender, this, "commands.scoreboard.teams.option.success", string, scorePlayerTeam.getRegisteredName(), string2);
      }
   }

   public void removeTeam(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScorePlayerTeam scorePlayerTeam = this.getTeam(strings[i]);
      if (scorePlayerTeam != null) {
         scoreboard2.removeTeam(scorePlayerTeam);
         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.remove.success", scorePlayerTeam.getRegisteredName());
      }
   }

   public void listTeams(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      if (strings.length > i) {
         ScorePlayerTeam scorePlayerTeam = this.getTeam(strings[i]);
         if (scorePlayerTeam == null) {
            return;
         }

         Collection collection = scorePlayerTeam.getMembershipCollection();
         iCommandSender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, collection.size());
         if (collection.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", scorePlayerTeam.getRegisteredName());
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", collection.size(), scorePlayerTeam.getRegisteredName());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation);
         iCommandSender.addChatMessage(new ChatComponentText(joinNiceString(collection.toArray())));
      } else {
         Collection collection2 = scoreboard2.getTeams();
         iCommandSender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, collection2.size());
         if (collection2.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", collection2.size());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation2);

         for (Object object : collection2) {
            ScorePlayerTeam scorePlayerTeam2 = (ScorePlayerTeam)object;
            iCommandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.teams.list.entry", scorePlayerTeam2.getRegisteredName(), scorePlayerTeam2.getTeamName(), scorePlayerTeam2.getMembershipCollection().size()
               )
            );
         }
      }
   }

   public void joinTeam(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = strings[i++];
      HashSet hashSet = Sets.newHashSet();
      HashSet hashSet2 = Sets.newHashSet();
      if (iCommandSender instanceof EntityPlayer && i == strings.length) {
         String string2 = getCommandSenderAsPlayer(iCommandSender).getName();
         if (scoreboard2.addPlayerToTeam(string2, string)) {
            hashSet.add(string2);
         } else {
            hashSet2.add(string2);
         }
      } else {
         while (i < strings.length) {
            String string3 = strings[i++];
            if (string3.startsWith("@")) {
               for (Entity entity : func_175763_c(iCommandSender, string3)) {
                  String string4 = getEntityName(iCommandSender, entity.getUniqueID().toString());
                  if (scoreboard2.addPlayerToTeam(string4, string)) {
                     hashSet.add(string4);
                  } else {
                     hashSet2.add(string4);
                  }
               }
            } else {
               String string5 = getEntityName(iCommandSender, string3);
               if (scoreboard2.addPlayerToTeam(string5, string)) {
                  hashSet.add(string5);
               } else {
                  hashSet2.add(string5);
               }
            }
         }
      }

      if (!hashSet.isEmpty()) {
         iCommandSender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, hashSet.size());
         notifyOperators(
            iCommandSender, this, "commands.scoreboard.teams.join.success", hashSet.size(), string, joinNiceString(hashSet.toArray(new String[hashSet.size()]))
         );
      }

      if (!hashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", hashSet2.size(), string, joinNiceString(hashSet2.toArray(new String[hashSet2.size()])));
      }
   }

   public void leaveTeam(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      HashSet hashSet = Sets.newHashSet();
      HashSet hashSet2 = Sets.newHashSet();
      if (iCommandSender instanceof EntityPlayer && i == strings.length) {
         String string = getCommandSenderAsPlayer(iCommandSender).getName();
         if (scoreboard2.removePlayerFromTeams(string)) {
            hashSet.add(string);
         } else {
            hashSet2.add(string);
         }
      } else {
         while (i < strings.length) {
            String string2 = strings[i++];
            if (string2.startsWith("@")) {
               for (Entity entity : func_175763_c(iCommandSender, string2)) {
                  String string3 = getEntityName(iCommandSender, entity.getUniqueID().toString());
                  if (scoreboard2.removePlayerFromTeams(string3)) {
                     hashSet.add(string3);
                  } else {
                     hashSet2.add(string3);
                  }
               }
            } else {
               String string4 = getEntityName(iCommandSender, string2);
               if (scoreboard2.removePlayerFromTeams(string4)) {
                  hashSet.add(string4);
               } else {
                  hashSet2.add(string4);
               }
            }
         }
      }

      if (!hashSet.isEmpty()) {
         iCommandSender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, hashSet.size());
         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.leave.success", hashSet.size(), joinNiceString(hashSet.toArray(new String[hashSet.size()])));
      }

      if (!hashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", hashSet2.size(), joinNiceString(hashSet2.toArray(new String[hashSet2.size()])));
      }
   }

   public void emptyTeam(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScorePlayerTeam scorePlayerTeam = this.getTeam(strings[i]);
      if (scorePlayerTeam != null) {
         ArrayList arrayList = Lists.newArrayList(scorePlayerTeam.getMembershipCollection());
         iCommandSender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, arrayList.size());
         if (arrayList.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", scorePlayerTeam.getRegisteredName());
         }

         for (Object object : arrayList) {
            String string = (String)object;
            scoreboard2.removePlayerFromTeam(string, scorePlayerTeam);
         }

         notifyOperators(iCommandSender, this, "commands.scoreboard.teams.empty.success", arrayList.size(), scorePlayerTeam.getRegisteredName());
      }
   }

   public void removeObjective(ICommandSender iCommandSender, String string) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      ScoreObjective scoreObjective = this.getObjective(string, false);
      scoreboard2.removeObjective(scoreObjective);
      notifyOperators(iCommandSender, this, "commands.scoreboard.objectives.remove.success", string);
   }

   public void listObjectives(ICommandSender iCommandSender) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      Collection collection = scoreboard2.getScoreObjectives();
      if (collection.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", collection.size());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      iCommandSender.addChatMessage(chatComponentTranslation);

      for (Object object : collection) {
         ScoreObjective scoreObjective = (ScoreObjective)object;
         iCommandSender.addChatMessage(
            new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", scoreObjective.getName(), scoreObjective.getDisplayName(), scoreObjective.getCriteria().getName())
         );
      }
   }

   public void setObjectiveDisplay(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = strings[i++];
      int j = Scoreboard.getObjectiveDisplaySlotNumber(string);
      ScoreObjective scoreObjective = null;
      if (strings.length == 4) {
         scoreObjective = this.getObjective(strings[i], false);
      }

      if (j < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", string);
      }

      scoreboard2.setObjectiveInDisplaySlot(j, scoreObjective);
      if (scoreObjective != null) {
         notifyOperators(
            iCommandSender, this, "commands.scoreboard.objectives.setdisplay.successSet", Scoreboard.getObjectiveDisplaySlot(j), scoreObjective.getName()
         );
      } else {
         notifyOperators(iCommandSender, this, "commands.scoreboard.objectives.setdisplay.successCleared", Scoreboard.getObjectiveDisplaySlot(j));
      }
   }

   public void listPlayers(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      if (strings.length > i) {
         String string = getEntityName(iCommandSender, strings[i]);
         Map map = scoreboard2.getObjectivesForEntity(string);
         iCommandSender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, map.size());
         if (map.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", string);
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", map.size(), string);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation);

         for (Object object : map.values()) {
            Score score = (Score)object;
            iCommandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.players.list.player.entry",
                  score.getScorePoints(),
                  score.getObjective().getDisplayName(),
                  score.getObjective().getName()
               )
            );
         }
      } else {
         Collection collection = scoreboard2.getObjectiveNames();
         iCommandSender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, collection.size());
         if (collection.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.scoreboard.players.list.count", collection.size());
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         iCommandSender.addChatMessage(chatComponentTranslation2);
         iCommandSender.addChatMessage(new ChatComponentText(joinNiceString(collection.toArray())));
      }
   }

   public void setPlayer(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      String string = strings[i - 1];
      int j = i;
      String string2 = getEntityName(iCommandSender, strings[i++]);
      if (string2.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", string2, 40);
      }

      ScoreObjective scoreObjective = this.getObjective(strings[i++], true);
      int k = string.equalsIgnoreCase("set") ? parseInt(strings[i++]) : parseInt(strings[i++], 0);
      if (strings.length > i) {
         Entity entity = getEntity(iCommandSender, strings[j]);

         try {
            NBTTagCompound nbtTagCompound = JsonToNBT.getTagFromJson(buildString(strings, i));
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            entity.writeToNBT(nbtTagCompound2);
            if (!NBTUtil.func_181123_a(nbtTagCompound, nbtTagCompound2, true)) {
               throw new CommandException("commands.scoreboard.players.set.tagMismatch", string2);
            }
         } catch (NBTException nbtException) {
            throw new CommandException("commands.scoreboard.players.set.tagError", nbtException.getMessage());
         }
      }

      Scoreboard scoreboard2 = this.getScoreboard();
      Score score = scoreboard2.getValueFromObjective(string2, scoreObjective);
      if (string.equalsIgnoreCase("set")) {
         score.setScorePoints(k);
      } else if (string.equalsIgnoreCase("add")) {
         score.increseScore(k);
      } else {
         score.decreaseScore(k);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.players.set.success", scoreObjective.getName(), string2, score.getScorePoints());
   }

   public void resetPlayers(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = getEntityName(iCommandSender, strings[i++]);
      if (strings.length > i) {
         ScoreObjective scoreObjective = this.getObjective(strings[i++], false);
         scoreboard2.removeObjectiveFromEntity(string, scoreObjective);
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.resetscore.success", scoreObjective.getName(), string);
      } else {
         scoreboard2.removeObjectiveFromEntity(string, (ScoreObjective)null);
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.reset.success", string);
      }
   }

   public void func_175779_n(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = getPlayerName(iCommandSender, strings[i++]);
      if (string.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", string, 40);
      }

      ScoreObjective scoreObjective = this.getObjective(strings[i], false);
      if (scoreObjective.getCriteria() != IScoreObjectiveCriteria.TRIGGER) {
         throw new CommandException("commands.scoreboard.players.enable.noTrigger", scoreObjective.getName());
      }

      Score score = scoreboard2.getValueFromObjective(string, scoreObjective);
      score.setLocked(false);
      notifyOperators(iCommandSender, this, "commands.scoreboard.players.enable.success", scoreObjective.getName(), string);
   }

   public void func_175781_o(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = getEntityName(iCommandSender, strings[i++]);
      if (string.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", string, 40);
      }

      ScoreObjective scoreObjective = this.getObjective(strings[i++], false);
      if (!scoreboard2.entityHasObjective(string, scoreObjective)) {
         throw new CommandException("commands.scoreboard.players.test.notFound", scoreObjective.getName(), string);
      }

      int j = strings[i].equals("*") ? Integer.MIN_VALUE : parseInt(strings[i]);
      i++;
      int k = i < strings.length && !strings[i].equals("*") ? parseInt(strings[i], j) : Integer.MAX_VALUE;
      Score score = scoreboard2.getValueFromObjective(string, scoreObjective);
      if (score.getScorePoints() >= j && score.getScorePoints() <= k) {
         notifyOperators(iCommandSender, this, "commands.scoreboard.players.test.success", score.getScorePoints(), j, k);
      } else {
         throw new CommandException("commands.scoreboard.players.test.failed", score.getScorePoints(), j, k);
      }
   }

   public void func_175778_p(ICommandSender iCommandSender, String[] strings, int i) throws CommandException {
      Scoreboard scoreboard2 = this.getScoreboard();
      String string = getEntityName(iCommandSender, strings[i++]);
      ScoreObjective scoreObjective = this.getObjective(strings[i++], true);
      String string2 = strings[i++];
      String string3 = getEntityName(iCommandSender, strings[i++]);
      ScoreObjective scoreObjective2 = this.getObjective(strings[i], false);
      if (string.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", string, 40);
      }

      if (string3.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", string3, 40);
      }

      Score score = scoreboard2.getValueFromObjective(string, scoreObjective);
      if (!scoreboard2.entityHasObjective(string3, scoreObjective2)) {
         throw new CommandException("commands.scoreboard.players.operation.notFound", scoreObjective2.getName(), string3);
      }

      Score score2 = scoreboard2.getValueFromObjective(string3, scoreObjective2);
      if (string2.equals("+=")) {
         score.setScorePoints(score.getScorePoints() + score2.getScorePoints());
      } else if (string2.equals("-=")) {
         score.setScorePoints(score.getScorePoints() - score2.getScorePoints());
      } else if (string2.equals("*=")) {
         score.setScorePoints(score.getScorePoints() * score2.getScorePoints());
      } else if (string2.equals("/=")) {
         if (score2.getScorePoints() != 0) {
            score.setScorePoints(score.getScorePoints() / score2.getScorePoints());
         }
      } else if (string2.equals("%=")) {
         if (score2.getScorePoints() != 0) {
            score.setScorePoints(score.getScorePoints() % score2.getScorePoints());
         }
      } else if (string2.equals("=")) {
         score.setScorePoints(score2.getScorePoints());
      } else if (string2.equals("<")) {
         score.setScorePoints(Math.min(score.getScorePoints(), score2.getScorePoints()));
      } else if (string2.equals(">")) {
         score.setScorePoints(Math.max(score.getScorePoints(), score2.getScorePoints()));
      } else {
         if (!string2.equals("><")) {
            throw new CommandException("commands.scoreboard.players.operation.invalidOperation", string2);
         }

         int j = score.getScorePoints();
         score.setScorePoints(score2.getScorePoints());
         score2.setScorePoints(j);
      }

      notifyOperators(iCommandSender, this, "commands.scoreboard.players.operation.success");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, "objectives", "players", "teams");
      }

      if (args[0].equalsIgnoreCase("objectives")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "setdisplay");
         }

         if (args[1].equalsIgnoreCase("add")) {
            if (args.length == 4) {
               Set set2 = IScoreObjectiveCriteria.INSTANCES.keySet();
               return getListOfStringsMatchingLastWord(args, set2);
            }
         } else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
            }
         } else if (args[1].equalsIgnoreCase("setdisplay")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, Scoreboard.getDisplaySlotStrings());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
            }
         }
      } else if (args[0].equalsIgnoreCase("players")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "set", "add", "remove", "reset", "list", "enable", "test", "operation");
         }

         if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove") && !args[1].equalsIgnoreCase("reset")) {
            if (args[1].equalsIgnoreCase("enable")) {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
               }

               if (args.length == 4) {
                  return getListOfStringsMatchingLastWord(args, this.func_175782_e());
               }
            } else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test")) {
               if (args[1].equalsIgnoreCase("operation")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(args, this.func_147184_a(true));
                  }

                  if (args.length == 5) {
                     return getListOfStringsMatchingLastWord(args, "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");
                  }

                  if (args.length == 6) {
                     return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                  }

                  if (args.length == 7) {
                     return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
                  }
               }
            } else {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
               }

               if (args.length == 4 && args[1].equalsIgnoreCase("test")) {
                  return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
               }
            }
         } else {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.func_147184_a(true));
            }
         }
      } else if (args[0].equalsIgnoreCase("teams")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "join", "leave", "empty", "list", "option");
         }

         if (args[1].equalsIgnoreCase("join")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }

            if (args.length >= 4) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
         } else {
            if (args[1].equalsIgnoreCase("leave")) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
               if (args[1].equalsIgnoreCase("option")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(
                        args, "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"
                     );
                  }

                  if (args.length == 5) {
                     if (args[3].equalsIgnoreCase("color")) {
                        return getListOfStringsMatchingLastWord(args, EnumChatFormatting.getValidValues(true, false));
                     }

                     if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility")) {
                        return getListOfStringsMatchingLastWord(args, Team.EnumVisible.func_178825_a());
                     }

                     if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                        return getListOfStringsMatchingLastWord(args, "true", "false");
                     }
                  }
               }
            } else if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }
         }
      }

      return null;
   }

   public List<String> func_147184_a(boolean bl) {
      Collection collection = this.getScoreboard().getScoreObjectives();
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : collection) {
         ScoreObjective scoreObjective = (ScoreObjective)object;
         if (!bl || !scoreObjective.getCriteria().isReadOnly()) {
            arrayList.add(scoreObjective.getName());
         }
      }

      return arrayList;
   }

   public List<String> func_175782_e() {
      Collection collection = this.getScoreboard().getScoreObjectives();
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : collection) {
         ScoreObjective scoreObjective = (ScoreObjective)object;
         if (scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            arrayList.add(scoreObjective.getName());
         }
      }

      return arrayList;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return !args[0].equalsIgnoreCase("players")
         ? (args[0].equalsIgnoreCase("teams") ? index == 2 : false)
         : (args.length > 1 && args[1].equalsIgnoreCase("operation") ? index == 2 || index == 5 : index == 2);
   }
}

package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Scoreboard {
   private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   private final Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
   private static String[] field_178823_g = null;

   public ScoreObjective getObjective(String name) {
      return this.scoreObjectives.get(name);
   }

   public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
      }

      ScoreObjective scoreObjective = this.getObjective(name);
      if (scoreObjective != null) {
         throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
      }

      scoreObjective = new ScoreObjective(this, name, criteria);
      List list = this.scoreObjectiveCriterias.get(criteria);
      if (list == null) {
         list = Lists.newArrayList();
         this.scoreObjectiveCriterias.put(criteria, list);
      }

      list.add(scoreObjective);
      this.scoreObjectives.put(name, scoreObjective);
      this.onScoreObjectiveAdded(scoreObjective);
      return scoreObjective;
   }

   public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria criteria) {
      Collection collection = this.scoreObjectiveCriterias.get(criteria);
      return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
   }

   public boolean entityHasObjective(String name, ScoreObjective scoreObjective) {
      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         return false;
      }

      Score score = (Score)map.get(scoreObjective);
      return score != null;
   }

   public Score getValueFromObjective(String name, ScoreObjective objective) {
      if (name.length() > 40) {
         throw new IllegalArgumentException("The player name '" + name + "' is too long!");
      }

      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
         this.entitiesScoreObjectives.put(name, map);
      }

      Score score = (Score)map.get(objective);
      if (score == null) {
         score = new Score(this, objective, name);
         map.put(objective, score);
      }

      return score;
   }

   public Collection<Score> getSortedScores(ScoreObjective objective) {
      ArrayList arrayList = Lists.newArrayList();

      for (Map map : this.entitiesScoreObjectives.values()) {
         Score score = (Score)map.get(objective);
         if (score != null) {
            arrayList.add(score);
         }
      }

      Collections.sort(arrayList, Score.scoreComparator);
      return arrayList;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.entitiesScoreObjectives.keySet();
   }

   public void removeObjectiveFromEntity(String name, ScoreObjective objective) {
      if (objective == null) {
         Map map = this.entitiesScoreObjectives.remove(name);
         if (map != null) {
            this.func_96516_a(name);
         }
      } else {
         Map map2 = this.entitiesScoreObjectives.get(name);
         if (map2 != null) {
            Score score = (Score)map2.remove(objective);
            if (map2.size() < 1) {
               Map map3 = this.entitiesScoreObjectives.remove(name);
               if (map3 != null) {
                  this.func_96516_a(name);
               }
            } else if (score != null) {
               this.func_178820_a(name, objective);
            }
         }
      }
   }

   public Collection<Score> getScores() {
      Collection collection = this.entitiesScoreObjectives.values();
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : collection) {
         Map map = (Map)object;
         arrayList.addAll(map.values());
      }

      return arrayList;
   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String name) {
      Map map = this.entitiesScoreObjectives.get(name);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(ScoreObjective scoreObjective) {
      if (scoreObjective != null) {
         this.scoreObjectives.remove(scoreObjective.getName());

         for (int i = 0; i < 19; i++) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
               this.setObjectiveInDisplaySlot(i, (ScoreObjective)null);
            }
         }

         List list = this.scoreObjectiveCriterias.get(scoreObjective.getCriteria());
         if (list != null) {
            list.remove(scoreObjective);
         }

         for (Map map : this.entitiesScoreObjectives.values()) {
            map.remove(scoreObjective);
         }

         this.onScoreObjectiveRemoved(scoreObjective);
      }
   }

   public void setObjectiveInDisplaySlot(int i, ScoreObjective scoreObjective) {
      this.objectiveDisplaySlots[i] = scoreObjective;
   }

   public ScoreObjective getObjectiveInDisplaySlot(int i) {
      return this.objectiveDisplaySlots[i];
   }

   public ScorePlayerTeam getTeam(String string) {
      return this.teams.get(string);
   }

   public ScorePlayerTeam createTeam(String name) {
      if (name.length() > 16) {
         throw new IllegalArgumentException("The team name '" + name + "' is too long!");
      }

      ScorePlayerTeam scorePlayerTeam = this.getTeam(name);
      if (scorePlayerTeam != null) {
         throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
      }

      scorePlayerTeam = new ScorePlayerTeam(this, name);
      this.teams.put(name, scorePlayerTeam);
      this.broadcastTeamCreated(scorePlayerTeam);
      return scorePlayerTeam;
   }

   public void removeTeam(ScorePlayerTeam scorePlayerTeam) {
      if (scorePlayerTeam != null) {
         this.teams.remove(scorePlayerTeam.getRegisteredName());

         for (String string : scorePlayerTeam.getMembershipCollection()) {
            this.teamMemberships.remove(string);
         }

         this.func_96513_c(scorePlayerTeam);
      }
   }

   public boolean addPlayerToTeam(String player, String newTeam) {
      if (player.length() > 40) {
         throw new IllegalArgumentException("The player name '" + player + "' is too long!");
      }

      if (!this.teams.containsKey(newTeam)) {
         return false;
      }

      ScorePlayerTeam scorePlayerTeam = this.getTeam(newTeam);
      if (this.getPlayersTeam(player) != null) {
         this.removePlayerFromTeams(player);
      }

      this.teamMemberships.put(player, scorePlayerTeam);
      scorePlayerTeam.getMembershipCollection().add(player);
      return true;
   }

   public boolean removePlayerFromTeams(String string) {
      ScorePlayerTeam scorePlayerTeam = this.getPlayersTeam(string);
      if (scorePlayerTeam != null) {
         this.removePlayerFromTeam(string, scorePlayerTeam);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String string, ScorePlayerTeam scorePlayerTeam) {
      if (this.getPlayersTeam(string) != scorePlayerTeam) {
         throw new IllegalStateException(
            "Player is either on another team or not on any team. Cannot remove from team '" + scorePlayerTeam.getRegisteredName() + "'."
         );
      }

      this.teamMemberships.remove(string);
      scorePlayerTeam.getMembershipCollection().remove(string);
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   public ScorePlayerTeam getPlayersTeam(String string) {
      return this.teamMemberships.get(string);
   }

   public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn) {
   }

   public void onObjectiveDisplayNameChanged(ScoreObjective scoreObjective) {
   }

   public void onScoreObjectiveRemoved(ScoreObjective scoreObjective) {
   }

   public void func_96536_a(Score score) {
   }

   public void func_96516_a(String string) {
   }

   public void func_178820_a(String string, ScoreObjective scoreObjective) {
   }

   public void broadcastTeamCreated(ScorePlayerTeam playerTeam) {
   }

   public void sendTeamUpdate(ScorePlayerTeam playerTeam) {
   }

   public void func_96513_c(ScorePlayerTeam playerTeam) {
   }

   public static String getObjectiveDisplaySlot(int i) {
      switch (i) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            if (i >= 3 && i <= 18) {
               EnumChatFormatting enumChatFormatting = EnumChatFormatting.func_175744_a(i - 3);
               if (enumChatFormatting != null && enumChatFormatting != EnumChatFormatting.RESET) {
                  return "sidebar.team." + enumChatFormatting.getFriendlyName();
               }
            }

            return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String string) {
      if (string.equalsIgnoreCase("list")) {
         return 0;
      }

      if (string.equalsIgnoreCase("sidebar")) {
         return 1;
      }

      if (string.equalsIgnoreCase("belowName")) {
         return 2;
      }

      if (string.startsWith("sidebar.team.")) {
         String string2 = string.substring("sidebar.team.".length());
         EnumChatFormatting enumChatFormatting = EnumChatFormatting.getValueByName(string2);
         if (enumChatFormatting != null && enumChatFormatting.getColorIndex() >= 0) {
            return enumChatFormatting.getColorIndex() + 3;
         }
      }

      return -1;
   }

   public static String[] getDisplaySlotStrings() {
      if (field_178823_g == null) {
         field_178823_g = new String[19];

         for (int i = 0; i < 19; i++) {
            field_178823_g[i] = getObjectiveDisplaySlot(i);
         }
      }

      return field_178823_g;
   }

   public void func_181140_a(Entity entity) {
      if (entity != null && !(entity instanceof EntityPlayer) && !entity.isEntityAlive()) {
         String string = entity.getUniqueID().toString();
         this.removeObjectiveFromEntity(string, (ScoreObjective)null);
         this.removePlayerFromTeams(string);
      }
   }
}

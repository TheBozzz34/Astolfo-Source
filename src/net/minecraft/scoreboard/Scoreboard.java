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
   private static final long b = 107193003611245L;

   public ScoreObjective getObjective(String var1) {
      return this.scoreObjectives.get(var1);
   }

   public ScoreObjective addScoreObjective(String var1, IScoreObjectiveCriteria var2) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + var1 + "' is too long!");
      }

      ScoreObjective var7 = this.getObjective(var1);
      if (var7 != null) {
         throw new IllegalArgumentException("An objective with the name '" + var1 + "' already exists!");
      }

      var7 = new ScoreObjective(this, var1, var2);
      List var8 = this.scoreObjectiveCriterias.get(var2);
      if (var8 == null) {
         var8 = Lists.newArrayList();
         this.scoreObjectiveCriterias.put(var2, var8);
      }

      var8.add(var7);
      this.scoreObjectives.put(var1, var7);
      this.onScoreObjectiveAdded(var7);
      return var7;
   }

   public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria var1) {
      Collection var5 = this.scoreObjectiveCriterias.get(var1);
      return var5 == null ? Lists.newArrayList() : Lists.newArrayList(var5);
   }

   public boolean entityHasObjective(String var1, ScoreObjective var2) {
      Map var6 = this.entitiesScoreObjectives.get(var1);
      if (var6 == null) {
         return false;
      }

      Score var7 = (Score)var6.get(var2);
      return var7 != null;
   }

   public Score getValueFromObjective(String var1, ScoreObjective var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      }

      Map var6 = this.entitiesScoreObjectives.get(var1);
      if (var6 == null) {
         var6 = Maps.newHashMap();
         this.entitiesScoreObjectives.put(var1, var6);
      }

      Score var7 = (Score)var6.get(var2);
      if (var7 == null) {
         var7 = new Score(this, var2, var1);
         var6.put(var2, var7);
      }

      return var7;
   }

   public Collection<Score> getSortedScores(ScoreObjective var1) {
      ArrayList var4 = Lists.newArrayList();

      for (Map var6 : this.entitiesScoreObjectives.values()) {
         Score var7 = (Score)var6.get(var1);
         if (var7 != null) {
            var4.add(var7);
         }
      }

      Collections.sort(var4, Score.scoreComparator);
      return var4;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.entitiesScoreObjectives.keySet();
   }

   public void removeObjectiveFromEntity(String var1, ScoreObjective var2) {
      if (var2 == null) {
         Map var6 = this.entitiesScoreObjectives.remove(var1);
         if (var6 != null) {
            this.func_96516_a(var1);
         }
      } else {
         Map var9 = this.entitiesScoreObjectives.get(var1);
         if (var9 != null) {
            Score var7 = (Score)var9.remove(var2);
            if (var9.size() < 1) {
               Map var8 = this.entitiesScoreObjectives.remove(var1);
               if (var8 != null) {
                  this.func_96516_a(var1);
               }
            } else if (var7 != null) {
               this.func_178820_a(var1, var2);
            }
         }
      }
   }

   public Collection<Score> getScores() {
      Collection var4 = this.entitiesScoreObjectives.values();
      ArrayList var5 = Lists.newArrayList();

      for (Object var7$raw : var4) {
         Map var7 = (Map)var7$raw;
         var5.addAll(var7.values());
      }

      return var5;
   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String var1) {
      Map var5 = this.entitiesScoreObjectives.get(var1);
      if (var5 == null) {
         var5 = Maps.newHashMap();
      }

      return var5;
   }

   public void removeObjective(ScoreObjective var1) {
      if (var1 != null) {
         this.scoreObjectives.remove(var1.getName());

         for (int var5 = 0; var5 < 19; var5++) {
            if (this.getObjectiveInDisplaySlot(var5) == var1) {
               this.setObjectiveInDisplaySlot(var5, (ScoreObjective)null);
            }
         }

         List var8 = this.scoreObjectiveCriterias.get(var1.getCriteria());
         if (var8 != null) {
            var8.remove(var1);
         }

         for (Map var7 : this.entitiesScoreObjectives.values()) {
            var7.remove(var1);
         }

         this.onScoreObjectiveRemoved(var1);
      }
   }

   public void setObjectiveInDisplaySlot(int var1, ScoreObjective var2) {
      this.objectiveDisplaySlots[var1] = var2;
   }

   public ScoreObjective getObjectiveInDisplaySlot(int var1) {
      return this.objectiveDisplaySlots[var1];
   }

   public ScorePlayerTeam getTeam(String var1) {
      return this.teams.get(var1);
   }

   public ScorePlayerTeam createTeam(String var1) {
      if (var1.length() > 16) {
         throw new IllegalArgumentException("The team name '" + var1 + "' is too long!");
      }

      ScorePlayerTeam var5 = this.getTeam(var1);
      if (var5 != null) {
         throw new IllegalArgumentException("A team with the name '" + var1 + "' already exists!");
      }

      var5 = new ScorePlayerTeam(this, var1);
      this.teams.put(var1, var5);
      this.broadcastTeamCreated(var5);
      return var5;
   }

   public void removeTeam(ScorePlayerTeam var1) {
      if (var1 != null) {
         this.teams.remove(var1.getRegisteredName());

         for (String var6 : var1.getMembershipCollection()) {
            this.teamMemberships.remove(var6);
         }

         this.func_96513_c(var1);
      }
   }

   public boolean addPlayerToTeam(String var1, String var2) {
      if (var1.length() > 40) {
         throw new IllegalArgumentException("The player name '" + var1 + "' is too long!");
      }

      if (!this.teams.containsKey(var2)) {
         return false;
      }

      ScorePlayerTeam var6 = this.getTeam(var2);
      if (this.getPlayersTeam(var1) != null) {
         this.removePlayerFromTeams(var1);
      }

      this.teamMemberships.put(var1, var6);
      var6.getMembershipCollection().add(var1);
      return true;
   }

   public boolean removePlayerFromTeams(String var1) {
      ScorePlayerTeam var5 = this.getPlayersTeam(var1);
      if (var5 != null) {
         this.removePlayerFromTeam(var1, var5);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String var1, ScorePlayerTeam var2) {
      if (this.getPlayersTeam(var1) != var2) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + var2.getRegisteredName() + "'.");
      }

      this.teamMemberships.remove(var1);
      var2.getMembershipCollection().remove(var1);
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   public ScorePlayerTeam getPlayersTeam(String var1) {
      return this.teamMemberships.get(var1);
   }

   public void onScoreObjectiveAdded(ScoreObjective var1) {
   }

   public void onObjectiveDisplayNameChanged(ScoreObjective var1) {
   }

   public void onScoreObjectiveRemoved(ScoreObjective var1) {
   }

   public void func_96536_a(Score var1) {
   }

   public void func_96516_a(String var1) {
   }

   public void func_178820_a(String var1, ScoreObjective var2) {
   }

   public void broadcastTeamCreated(ScorePlayerTeam var1) {
   }

   public void sendTeamUpdate(ScorePlayerTeam var1) {
   }

   public void func_96513_c(ScorePlayerTeam var1) {
   }

   public static String getObjectiveDisplaySlot(int var0) {
      switch (var0) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            if (var0 >= 3 && var0 <= 18) {
               EnumChatFormatting var4 = EnumChatFormatting.func_175744_a(var0 - 3);
               if (var4 != null && var4 != EnumChatFormatting.RESET) {
                  return "sidebar.team." + var4.getFriendlyName();
               }
            }

            return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String var0) {
      if (var0.equalsIgnoreCase("list")) {
         return 0;
      }

      if (var0.equalsIgnoreCase("sidebar")) {
         return 1;
      }

      if (var0.equalsIgnoreCase("belowName")) {
         return 2;
      }

      if (var0.startsWith("sidebar.team.")) {
         String var4 = var0.substring("sidebar.team.".length());
         EnumChatFormatting var5 = EnumChatFormatting.getValueByName(var4);
         if (var5 != null && var5.getColorIndex() >= 0) {
            return var5.getColorIndex() + 3;
         }
      }

      return -1;
   }

   public static String[] getDisplaySlotStrings() {
      if (field_178823_g == null) {
         field_178823_g = new String[19];

         for (int var3 = 0; var3 < 19; var3++) {
            field_178823_g[var3] = getObjectiveDisplaySlot(var3);
         }
      }

      return field_178823_g;
   }

   public void func_181140_a(Entity var1) {
      if (var1 != null && !(var1 instanceof EntityPlayer) && !var1.isEntityAlive()) {
         String var5 = var1.getUniqueID().toString();
         this.removeObjectiveFromEntity(var5, (ScoreObjective)null);
         this.removePlayerFromTeams(var5);
      }
   }
}

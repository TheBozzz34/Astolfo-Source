package net.minecraft.scoreboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger logger = LogManager.getLogger();
   private Scoreboard theScoreboard;
   private NBTTagCompound delayedInitNbt;
   private static final long a = 123098251514214L;

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String var1) {
      super(var1);
   }

   public void setScoreboard(Scoreboard var1) {
      this.theScoreboard = var1;
      if (this.delayedInitNbt != null) {
         this.readFromNBT(this.delayedInitNbt);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      if (this.theScoreboard == null) {
         this.delayedInitNbt = var1;
      } else {
         this.readObjectives(var1.getTagList("Objectives", 10));
         this.readScores(var1.getTagList("PlayerScores", 10));
         if (var1.hasKey("DisplaySlots", 10)) {
            this.readDisplayConfig(var1.getCompoundTag("DisplaySlots"));
         }

         if (var1.hasKey("Teams", 9)) {
            this.readTeams(var1.getTagList("Teams", 10));
         }
      }
   }

   public void readTeams(NBTTagList var1) {
      for (int var5 = 0; var5 < var1.tagCount(); var5++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var5);
         String var7 = var6.getString("Name");
         if (var7.length() > 16) {
            var7 = var7.substring(0, 16);
         }

         ScorePlayerTeam var8 = this.theScoreboard.createTeam(var7);
         String var9 = var6.getString("DisplayName");
         if (var9.length() > 32) {
            var9 = var9.substring(0, 32);
         }

         var8.setTeamName(var9);
         if (var6.hasKey("TeamColor", 8)) {
            var8.setChatFormat(EnumChatFormatting.getValueByName(var6.getString("TeamColor")));
         }

         var8.setNamePrefix(var6.getString("Prefix"));
         var8.setNameSuffix(var6.getString("Suffix"));
         if (var6.hasKey("AllowFriendlyFire", 99)) {
            var8.setAllowFriendlyFire(var6.getBoolean("AllowFriendlyFire"));
         }

         if (var6.hasKey("SeeFriendlyInvisibles", 99)) {
            var8.setSeeFriendlyInvisiblesEnabled(var6.getBoolean("SeeFriendlyInvisibles"));
         }

         if (var6.hasKey("NameTagVisibility", 8)) {
            Team.EnumVisible var10 = Team.EnumVisible.func_178824_a(var6.getString("NameTagVisibility"));
            if (var10 != null) {
               var8.setNameTagVisibility(var10);
            }
         }

         if (var6.hasKey("DeathMessageVisibility", 8)) {
            Team.EnumVisible var11 = Team.EnumVisible.func_178824_a(var6.getString("DeathMessageVisibility"));
            if (var11 != null) {
               var8.setDeathMessageVisibility(var11);
            }
         }

         this.func_96502_a(var8, var6.getTagList("Players", 8));
      }
   }

   public void func_96502_a(ScorePlayerTeam var1, NBTTagList var2) {
      for (int var6 = 0; var6 < var2.tagCount(); var6++) {
         this.theScoreboard.addPlayerToTeam(var2.getStringTagAt(var6), var1.getRegisteredName());
      }
   }

   public void readDisplayConfig(NBTTagCompound var1) {
      for (int var5 = 0; var5 < 19; var5++) {
         if (var1.hasKey("slot_" + var5, 8)) {
            String var6 = var1.getString("slot_" + var5);
            ScoreObjective var7 = this.theScoreboard.getObjective(var6);
            this.theScoreboard.setObjectiveInDisplaySlot(var5, var7);
         }
      }
   }

   public void readObjectives(NBTTagList var1) {
      for (int var5 = 0; var5 < var1.tagCount(); var5++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var5);
         IScoreObjectiveCriteria var7 = IScoreObjectiveCriteria.INSTANCES.get(var6.getString("CriteriaName"));
         if (var7 != null) {
            String var8 = var6.getString("Name");
            if (var8.length() > 16) {
               var8 = var8.substring(0, 16);
            }

            ScoreObjective var9 = this.theScoreboard.addScoreObjective(var8, var7);
            var9.setDisplayName(var6.getString("DisplayName"));
            var9.setRenderType(IScoreObjectiveCriteria.EnumRenderType.func_178795_a(var6.getString("RenderType")));
         }
      }
   }

   public void readScores(NBTTagList var1) {
      for (int var5 = 0; var5 < var1.tagCount(); var5++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var5);
         ScoreObjective var7 = this.theScoreboard.getObjective(var6.getString("Objective"));
         String var8 = var6.getString("Name");
         if (var8.length() > 40) {
            var8 = var8.substring(0, 40);
         }

         Score var9 = this.theScoreboard.getValueFromObjective(var8, var7);
         var9.setScorePoints(var6.getInteger("Score"));
         if (var6.hasKey("Locked")) {
            var9.setLocked(var6.getBoolean("Locked"));
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      if (this.theScoreboard == null) {
         logger.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         var1.setTag("Objectives", this.objectivesToNbt());
         var1.setTag("PlayerScores", this.scoresToNbt());
         var1.setTag("Teams", this.func_96496_a());
         this.func_96497_d(var1);
      }
   }

   public NBTTagList func_96496_a() {
      NBTTagList var4 = new NBTTagList();

      for (ScorePlayerTeam var6 : this.theScoreboard.getTeams()) {
         NBTTagCompound var7 = new NBTTagCompound();
         var7.setString("Name", var6.getRegisteredName());
         var7.setString("DisplayName", var6.getTeamName());
         if (var6.getChatFormat().getColorIndex() >= 0) {
            var7.setString("TeamColor", var6.getChatFormat().getFriendlyName());
         }

         var7.setString("Prefix", var6.getColorPrefix());
         var7.setString("Suffix", var6.getColorSuffix());
         var7.setBoolean("AllowFriendlyFire", var6.getAllowFriendlyFire());
         var7.setBoolean("SeeFriendlyInvisibles", var6.getSeeFriendlyInvisiblesEnabled());
         var7.setString("NameTagVisibility", var6.getNameTagVisibility().internalName);
         var7.setString("DeathMessageVisibility", var6.getDeathMessageVisibility().internalName);
         NBTTagList var8 = new NBTTagList();

         for (String var10 : var6.getMembershipCollection()) {
            var8.appendTag(new NBTTagString(var10));
         }

         var7.setTag("Players", var8);
         var4.appendTag(var7);
      }

      return var4;
   }

   public void func_96497_d(NBTTagCompound var1) {
      NBTTagCompound var5 = new NBTTagCompound();
      boolean var6 = false;

      for (int var7 = 0; var7 < 19; var7++) {
         ScoreObjective var8 = this.theScoreboard.getObjectiveInDisplaySlot(var7);
         if (var8 != null) {
            var5.setString("slot_" + var7, var8.getName());
            var6 = true;
         }
      }

      if (var6) {
         var1.setTag("DisplaySlots", var5);
      }
   }

   public NBTTagList objectivesToNbt() {
      NBTTagList var4 = new NBTTagList();

      for (ScoreObjective var6 : this.theScoreboard.getScoreObjectives()) {
         if (var6.getCriteria() != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setString("Name", var6.getName());
            var7.setString("CriteriaName", var6.getCriteria().getName());
            var7.setString("DisplayName", var6.getDisplayName());
            var7.setString("RenderType", var6.getRenderType().func_178796_a());
            var4.appendTag(var7);
         }
      }

      return var4;
   }

   public NBTTagList scoresToNbt() {
      NBTTagList var4 = new NBTTagList();

      for (Score var6 : this.theScoreboard.getScores()) {
         if (var6.getObjective() != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setString("Name", var6.getPlayerName());
            var7.setString("Objective", var6.getObjective().getName());
            var7.setInteger("Score", var6.getScorePoints());
            var7.setBoolean("Locked", var6.isLocked());
            var4.appendTag(var7);
         }
      }

      return var4;
   }
}

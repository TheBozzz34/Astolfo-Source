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

   public ScoreboardSaveData() {
      this("scoreboard");
   }

   public ScoreboardSaveData(String name) {
      super(name);
   }

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.theScoreboard = scoreboardIn;
      if (this.delayedInitNbt != null) {
         this.readFromNBT(this.delayedInitNbt);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         this.delayedInitNbt = nbt;
      } else {
         this.readObjectives(nbt.getTagList("Objectives", 10));
         this.readScores(nbt.getTagList("PlayerScores", 10));
         if (nbt.hasKey("DisplaySlots", 10)) {
            this.readDisplayConfig(nbt.getCompoundTag("DisplaySlots"));
         }

         if (nbt.hasKey("Teams", 9)) {
            this.readTeams(nbt.getTagList("Teams", 10));
         }
      }
   }

   public void readTeams(NBTTagList nbtTagList) {
      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
         String string = nbtTagCompound.getString("Name");
         if (string.length() > 16) {
            string = string.substring(0, 16);
         }

         ScorePlayerTeam scorePlayerTeam = this.theScoreboard.createTeam(string);
         String string2 = nbtTagCompound.getString("DisplayName");
         if (string2.length() > 32) {
            string2 = string2.substring(0, 32);
         }

         scorePlayerTeam.setTeamName(string2);
         if (nbtTagCompound.hasKey("TeamColor", 8)) {
            scorePlayerTeam.setChatFormat(EnumChatFormatting.getValueByName(nbtTagCompound.getString("TeamColor")));
         }

         scorePlayerTeam.setNamePrefix(nbtTagCompound.getString("Prefix"));
         scorePlayerTeam.setNameSuffix(nbtTagCompound.getString("Suffix"));
         if (nbtTagCompound.hasKey("AllowFriendlyFire", 99)) {
            scorePlayerTeam.setAllowFriendlyFire(nbtTagCompound.getBoolean("AllowFriendlyFire"));
         }

         if (nbtTagCompound.hasKey("SeeFriendlyInvisibles", 99)) {
            scorePlayerTeam.setSeeFriendlyInvisiblesEnabled(nbtTagCompound.getBoolean("SeeFriendlyInvisibles"));
         }

         if (nbtTagCompound.hasKey("NameTagVisibility", 8)) {
            Team.EnumVisible enumVisible = Team.EnumVisible.func_178824_a(nbtTagCompound.getString("NameTagVisibility"));
            if (enumVisible != null) {
               scorePlayerTeam.setNameTagVisibility(enumVisible);
            }
         }

         if (nbtTagCompound.hasKey("DeathMessageVisibility", 8)) {
            Team.EnumVisible enumVisible2 = Team.EnumVisible.func_178824_a(nbtTagCompound.getString("DeathMessageVisibility"));
            if (enumVisible2 != null) {
               scorePlayerTeam.setDeathMessageVisibility(enumVisible2);
            }
         }

         this.func_96502_a(scorePlayerTeam, nbtTagCompound.getTagList("Players", 8));
      }
   }

   public void func_96502_a(ScorePlayerTeam scorePlayerTeam, NBTTagList nbtTagList) {
      for (int i = 0; i < nbtTagList.tagCount(); i++) {
         this.theScoreboard.addPlayerToTeam(nbtTagList.getStringTagAt(i), scorePlayerTeam.getRegisteredName());
      }
   }

   public void readDisplayConfig(NBTTagCompound nbtTagCompound) {
      for (int i = 0; i < 19; i++) {
         if (nbtTagCompound.hasKey("slot_" + i, 8)) {
            String string = nbtTagCompound.getString("slot_" + i);
            ScoreObjective scoreObjective = this.theScoreboard.getObjective(string);
            this.theScoreboard.setObjectiveInDisplaySlot(i, scoreObjective);
         }
      }
   }

   public void readObjectives(NBTTagList nbt) {
      for (int i = 0; i < nbt.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbt.getCompoundTagAt(i);
         IScoreObjectiveCriteria iScoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(nbtTagCompound.getString("CriteriaName"));
         if (iScoreObjectiveCriteria != null) {
            String string = nbtTagCompound.getString("Name");
            if (string.length() > 16) {
               string = string.substring(0, 16);
            }

            ScoreObjective scoreObjective = this.theScoreboard.addScoreObjective(string, iScoreObjectiveCriteria);
            scoreObjective.setDisplayName(nbtTagCompound.getString("DisplayName"));
            scoreObjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.func_178795_a(nbtTagCompound.getString("RenderType")));
         }
      }
   }

   public void readScores(NBTTagList nbt) {
      for (int i = 0; i < nbt.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbt.getCompoundTagAt(i);
         ScoreObjective scoreObjective = this.theScoreboard.getObjective(nbtTagCompound.getString("Objective"));
         String string = nbtTagCompound.getString("Name");
         if (string.length() > 40) {
            string = string.substring(0, 40);
         }

         Score score = this.theScoreboard.getValueFromObjective(string, scoreObjective);
         score.setScorePoints(nbtTagCompound.getInteger("Score"));
         if (nbtTagCompound.hasKey("Locked")) {
            score.setLocked(nbtTagCompound.getBoolean("Locked"));
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      if (this.theScoreboard == null) {
         logger.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         nbt.setTag("Objectives", this.objectivesToNbt());
         nbt.setTag("PlayerScores", this.scoresToNbt());
         nbt.setTag("Teams", this.func_96496_a());
         this.func_96497_d(nbt);
      }
   }

   public NBTTagList func_96496_a() {
      NBTTagList nbtTagList = new NBTTagList();

      for (ScorePlayerTeam scorePlayerTeam : this.theScoreboard.getTeams()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setString("Name", scorePlayerTeam.getRegisteredName());
         nbtTagCompound.setString("DisplayName", scorePlayerTeam.getTeamName());
         if (scorePlayerTeam.getChatFormat().getColorIndex() >= 0) {
            nbtTagCompound.setString("TeamColor", scorePlayerTeam.getChatFormat().getFriendlyName());
         }

         nbtTagCompound.setString("Prefix", scorePlayerTeam.getColorPrefix());
         nbtTagCompound.setString("Suffix", scorePlayerTeam.getColorSuffix());
         nbtTagCompound.setBoolean("AllowFriendlyFire", scorePlayerTeam.getAllowFriendlyFire());
         nbtTagCompound.setBoolean("SeeFriendlyInvisibles", scorePlayerTeam.getSeeFriendlyInvisiblesEnabled());
         nbtTagCompound.setString("NameTagVisibility", scorePlayerTeam.getNameTagVisibility().internalName);
         nbtTagCompound.setString("DeathMessageVisibility", scorePlayerTeam.getDeathMessageVisibility().internalName);
         NBTTagList nbtTagList2 = new NBTTagList();

         for (String string : scorePlayerTeam.getMembershipCollection()) {
            nbtTagList2.appendTag(new NBTTagString(string));
         }

         nbtTagCompound.setTag("Players", nbtTagList2);
         nbtTagList.appendTag(nbtTagCompound);
      }

      return nbtTagList;
   }

   public void func_96497_d(NBTTagCompound nbtTagCompound) {
      NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
      boolean bl = false;

      for (int i = 0; i < 19; i++) {
         ScoreObjective scoreObjective = this.theScoreboard.getObjectiveInDisplaySlot(i);
         if (scoreObjective != null) {
            nbtTagCompound2.setString("slot_" + i, scoreObjective.getName());
            bl = true;
         }
      }

      if (bl) {
         nbtTagCompound.setTag("DisplaySlots", nbtTagCompound2);
      }
   }

   public NBTTagList objectivesToNbt() {
      NBTTagList nbtTagList = new NBTTagList();

      for (ScoreObjective scoreObjective : this.theScoreboard.getScoreObjectives()) {
         if (scoreObjective.getCriteria() != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setString("Name", scoreObjective.getName());
            nbtTagCompound.setString("CriteriaName", scoreObjective.getCriteria().getName());
            nbtTagCompound.setString("DisplayName", scoreObjective.getDisplayName());
            nbtTagCompound.setString("RenderType", scoreObjective.getRenderType().func_178796_a());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }

   public NBTTagList scoresToNbt() {
      NBTTagList nbtTagList = new NBTTagList();

      for (Score score : this.theScoreboard.getScores()) {
         if (score.getObjective() != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setString("Name", score.getPlayerName());
            nbtTagCompound.setString("Objective", score.getObjective().getName());
            nbtTagCompound.setInteger("Score", score.getScorePoints());
            nbtTagCompound.setBoolean("Locked", score.isLocked());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }
}

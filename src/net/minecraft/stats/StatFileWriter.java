package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
   public final Map<StatBase, TupleIntJsonSerializable> statsData = Maps.newConcurrentMap();

   public boolean hasAchievementUnlocked(Achievement achievementIn) {
      return this.readStat(achievementIn) > 0;
   }

   public boolean canUnlockAchievement(Achievement achievementIn) {
      return achievementIn.parentAchievement == null || this.hasAchievementUnlocked(achievementIn.parentAchievement);
   }

   public int func_150874_c(Achievement achievement) {
      if (this.hasAchievementUnlocked(achievement)) {
         return 0;
      }

      int i = 0;

      for (Achievement achievement2 = achievement.parentAchievement; achievement2 != null && !this.hasAchievementUnlocked(achievement2); i++) {
         achievement2 = achievement2.parentAchievement;
      }

      return i;
   }

   public void increaseStat(EntityPlayer player, StatBase stat, int amount) {
      if (!stat.isAchievement() || this.canUnlockAchievement((Achievement)stat)) {
         this.unlockAchievement(player, stat, this.readStat(stat) + amount);
      }
   }

   public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int i) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statIn);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(statIn, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setIntegerValue(i);
   }

   public int readStat(StatBase stat) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(stat);
      return tupleIntJsonSerializable == null ? 0 : tupleIntJsonSerializable.getIntegerValue();
   }

   public <T extends IJsonSerializable> T func_150870_b(StatBase statBase) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statBase);
      return tupleIntJsonSerializable != null ? tupleIntJsonSerializable.getJsonSerializableValue() : null;
   }

   public <T extends IJsonSerializable> T func_150872_a(StatBase statBase, T iJsonSerializable) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statBase);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(statBase, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setJsonSerializableValue(iJsonSerializable);
      return (T)iJsonSerializable;
   }
}

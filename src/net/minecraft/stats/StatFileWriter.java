package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
   public final Map<StatBase, TupleIntJsonSerializable> statsData = Maps.newConcurrentMap();
   private static final long b = 122045762422275L;

   public boolean hasAchievementUnlocked(Achievement var1) {
      return this.readStat(var1) > 0;
   }

   public boolean canUnlockAchievement(Achievement var1) {
      return var1.parentAchievement == null || this.hasAchievementUnlocked(var1.parentAchievement);
   }

   public int func_150874_c(Achievement var1) {
      if (this.hasAchievementUnlocked(var1)) {
         return 0;
      }

      int var5 = 0;

      for (Achievement var6 = var1.parentAchievement; var6 != null && !this.hasAchievementUnlocked(var6); var5++) {
         var6 = var6.parentAchievement;
      }

      return var5;
   }

   public void increaseStat(EntityPlayer var1, StatBase var2, int var3) {
      if (!var2.isAchievement() || this.canUnlockAchievement((Achievement)var2)) {
         this.unlockAchievement(var1, var2, this.readStat(var2) + var3);
      }
   }

   public void unlockAchievement(EntityPlayer var1, StatBase var2, int var3) {
      TupleIntJsonSerializable var7 = this.statsData.get(var2);
      if (var7 == null) {
         var7 = new TupleIntJsonSerializable();
         this.statsData.put(var2, var7);
      }

      var7.setIntegerValue(var3);
   }

   public int readStat(StatBase var1) {
      TupleIntJsonSerializable var5 = this.statsData.get(var1);
      return var5 == null ? 0 : var5.getIntegerValue();
   }

   public <T extends IJsonSerializable> T func_150870_b(StatBase var1) {
      TupleIntJsonSerializable var5 = this.statsData.get(var1);
      return var5 != null ? var5.getJsonSerializableValue() : null;
   }

   public <T extends IJsonSerializable> T func_150872_a(StatBase var1, T var2) {
      TupleIntJsonSerializable var6 = this.statsData.get(var1);
      if (var6 == null) {
         var6 = new TupleIntJsonSerializable();
         this.statsData.put(var1, var6);
      }

      var6.setJsonSerializableValue(var2);
      return (T)var2;
   }
}

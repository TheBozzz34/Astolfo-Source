package net.minecraft.stats;

import net.minecraft.scoreboard.ScoreDummyCriteria;

public class ObjectiveStat extends ScoreDummyCriteria {
   private final StatBase stat;

   public ObjectiveStat(StatBase var1) {
      super(var1.statId);
      this.stat = var1;
   }
}

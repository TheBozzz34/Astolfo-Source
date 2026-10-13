package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   private static final long a = 136004039803262L;

   public ScoreHealthCriteria(String var1) {
      super(var1);
   }

   @Override
   public int setScore(List<EntityPlayer> var1) {
      float var5 = 0.0F;

      for (EntityPlayer var7 : var1) {
         var5 += var7.getHealth() + var7.getAbsorptionAmount();
      }

      if (var1.size() > 0) {
         var5 /= var1.size();
      }

      return MathHelper.ceiling_float_int(var5);
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
   }
}

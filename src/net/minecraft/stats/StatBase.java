package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;

public class StatBase {
   public final String statId;
   private final IChatComponent statName;
   public boolean isIndependent;
   private final IStatType type;
   private final IScoreObjectiveCriteria objectiveCriteria;
   private Class<? extends IJsonSerializable> field_150956_d;
   private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
   public static IStatType simpleStatType = new IStatType() {
      @Override
      public String format(int var1) {
         return StatBase.numberFormat.format(var1);
      }
   };
   private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
   public static IStatType timeStatType = new IStatType() {
      private static final long a = 69958889824939L;

      @Override
      public String format(int var1) {
         double var6 = var1 / 20.0;
         double var8 = var6 / 60.0;
         double var10 = var8 / 60.0;
         double var12 = var10 / 24.0;
         double var14 = var12 / 365.0;
         return var14 > 0.5
            ? StatBase.decimalFormat.format(var14) + " y"
            : (
               var12 > 0.5
                  ? StatBase.decimalFormat.format(var12) + " d"
                  : (var10 > 0.5 ? StatBase.decimalFormat.format(var10) + " h" : (var8 > 0.5 ? StatBase.decimalFormat.format(var8) + " m" : var6 + " s"))
            );
      }
   };
   public static IStatType distanceStatType = new IStatType() {
      private static final long a = 136726769203444L;

      @Override
      public String format(int var1) {
         double var5 = var1 / 100.0;
         double var7 = var5 / 1000.0;
         return var7 > 0.5 ? StatBase.decimalFormat.format(var7) + " km" : (var5 > 0.5 ? StatBase.decimalFormat.format(var5) + " m" : var1 + " cm");
      }
   };
   public static IStatType field_111202_k = new IStatType() {
      @Override
      public String format(int var1) {
         return StatBase.decimalFormat.format(var1 * 0.1);
      }
   };
   private static final long e = 82460474577747L;
   private static final long t = 6136178913476345981L;

   public StatBase(String var1, IChatComponent var2, IStatType var3) {
      this.statId = var1;
      this.statName = var2;
      this.type = var3;
      this.objectiveCriteria = new ObjectiveStat(this);
      IScoreObjectiveCriteria.INSTANCES.put(this.objectiveCriteria.getName(), this.objectiveCriteria);
   }

   public StatBase(String var1, IChatComponent var2) {
      this(var1, var2, simpleStatType);
   }

   public StatBase initIndependentStat() {
      this.isIndependent = true;
      return this;
   }

   public StatBase registerStat() {
      if (StatList.oneShotStats.containsKey(this.statId)) {
         throw new RuntimeException(
            "Duplicate stat id: \"" + StatList.oneShotStats.get(this.statId).statName + "\" and \"" + this.statName + "\" at id " + this.statId
         );
      }

      StatList.allStats.add(this);
      StatList.oneShotStats.put(this.statId, this);
      return this;
   }

   public boolean isAchievement() {
      return false;
   }

   public String format(int var1) {
      return this.type.format(var1);
   }

   public IChatComponent getStatName() {
      IChatComponent var1 = this.statName.createCopy();
      var1.getChatStyle().setColor(EnumChatFormatting.GRAY);
      var1.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
      return var1;
   }

   public IChatComponent createChatComponent() {
      IChatComponent var1 = this.getStatName();
      IChatComponent var2 = new ChatComponentText("[").appendSibling(var1).appendText("]");
      var2.setChatStyle(var1.getChatStyle());
      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         StatBase var5 = (StatBase)var1;
         return this.statId.equals(var5.statId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.statId.hashCode();
   }

   @Override
   public String toString() {
      return "Stat{id="
         + this.statId
         + ", nameId="
         + this.statName
         + ", awardLocallyOnly="
         + this.isIndependent
         + ", formatter="
         + this.type
         + ", objectiveCriteria="
         + this.objectiveCriteria
         + (int)t;
   }

   public IScoreObjectiveCriteria getCriteria() {
      return this.objectiveCriteria;
   }

   public Class<? extends IJsonSerializable> func_150954_l() {
      return this.field_150956_d;
   }

   public StatBase func_150953_b(Class<? extends IJsonSerializable> var1) {
      this.field_150956_d = var1;
      return this;
   }
}

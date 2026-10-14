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
      public String format(int number) {
         return StatBase.numberFormat.format(number);
      }
   };
   private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
   public static IStatType timeStatType = new IStatType() {
      @Override
      public String format(int number) {
         double e = number / 20.0;
         double d2 = e / 60.0;
         double d3 = d2 / 60.0;
         double d4 = d3 / 24.0;
         double d5 = d4 / 365.0;
         return d5 > 0.5
            ? StatBase.decimalFormat.format(d5) + " y"
            : (
               d4 > 0.5
                  ? StatBase.decimalFormat.format(d4) + " d"
                  : (d3 > 0.5 ? StatBase.decimalFormat.format(d3) + " h" : (d2 > 0.5 ? StatBase.decimalFormat.format(d2) + " m" : e + " s"))
            );
      }
   };
   public static IStatType distanceStatType = new IStatType() {
      @Override
      public String format(int number) {
         double d = number / 100.0;
         double e = d / 1000.0;
         return e > 0.5 ? StatBase.decimalFormat.format(e) + " km" : (d > 0.5 ? StatBase.decimalFormat.format(d) + " m" : number + " cm");
      }
   };
   public static IStatType field_111202_k = new IStatType() {
      @Override
      public String format(int number) {
         return StatBase.decimalFormat.format(number * 0.1);
      }
   };

   public StatBase(String statIdIn, IChatComponent statNameIn, IStatType typeIn) {
      this.statId = statIdIn;
      this.statName = statNameIn;
      this.type = typeIn;
      this.objectiveCriteria = new ObjectiveStat(this);
      IScoreObjectiveCriteria.INSTANCES.put(this.objectiveCriteria.getName(), this.objectiveCriteria);
   }

   public StatBase(String statIdIn, IChatComponent statNameIn) {
      this(statIdIn, statNameIn, simpleStatType);
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

   public String format(int i) {
      return this.type.format(i);
   }

   public IChatComponent getStatName() {
      IChatComponent iChatComponent = this.statName.createCopy();
      iChatComponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
      iChatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
      return iChatComponent;
   }

   public IChatComponent createChatComponent() {
      IChatComponent iChatComponent = this.getStatName();
      IChatComponent iChatComponent2 = new ChatComponentText("[").appendSibling(iChatComponent).appendText("]");
      iChatComponent2.setChatStyle(iChatComponent.getChatStyle());
      return iChatComponent2;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         StatBase statBase = (StatBase)object;
         return this.statId.equals(statBase.statId);
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
         + '}';
   }

   public IScoreObjectiveCriteria getCriteria() {
      return this.objectiveCriteria;
   }

   public Class<? extends IJsonSerializable> func_150954_l() {
      return this.field_150956_d;
   }

   public StatBase func_150953_b(Class<? extends IJsonSerializable> class2) {
      this.field_150956_d = class2;
      return this;
   }
}

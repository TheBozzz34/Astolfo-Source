package net.hypixel.api.reply;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import net.hypixel.api.util.GameType;

public class BoostersReply extends AbstractReply {
   private List<BoostersReply.Booster> boosters;
   private BoostersReply.BoosterState boosterState;
   private static final long a = 127013117322428L;

   public List<BoostersReply.Booster> getBoosters() {
      return this.boosters;
   }

   public BoostersReply.BoosterState getBoosterState() {
      return this.boosterState;
   }

   @Override
   public String toString() {
      return "BoostersReply{boosters=" + this.boosters + ", boosterState=" + this.boosterState + "} " + super.toString();
   }

   public static class Booster {
      private UUID purchaserUuid;
      private double amount;
      private int originalLength;
      private int length;
      private GameType gameType;
      private ZonedDateTime dateActivated;
      private List<UUID> stacked;
      private boolean queuedToStack;
      private static final long a = 27001429631230L;
      private static final long e = -1023592596145962883L;

      public UUID getPurchaserUuid() {
         return this.purchaserUuid;
      }

      public double getAmount() {
         return this.amount;
      }

      public int W() {
         return this.originalLength;
      }

      public int v() {
         return this.length;
      }

      public GameType getGameType() {
         return this.gameType;
      }

      public ZonedDateTime getDateActivated() {
         return this.dateActivated;
      }

      public List<UUID> getStacked() {
         return this.stacked;
      }

      public boolean isQueuedToStack() {
         return this.queuedToStack;
      }

      @Override
      public String toString() {
         return "Booster{purchaserUuid="
            + this.purchaserUuid
            + ", amount="
            + this.amount
            + ", originalLength="
            + this.originalLength
            + ", length="
            + this.length
            + ", gameType="
            + this.gameType
            + ", dateActivated="
            + this.dateActivated
            + ", stacked="
            + this.stacked
            + ", queuedToStack="
            + this.queuedToStack
            + (int)e;
      }
   }

   public static class BoosterState {
      private boolean decrementing;
      private static final long a = 33922908383710L;
      private static final long c = -8970209746552356739L;

      public boolean isDecrementing() {
         return this.decrementing;
      }

      @Override
      public String toString() {
         return "BoosterState{decrementing=" + this.decrementing + (int)c;
      }
   }
}

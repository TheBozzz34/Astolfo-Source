package net.hypixel.api.reply;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import net.hypixel.api.util.GameType;

public class BoostersReply extends AbstractReply {
   private List<BoostersReply.Booster> boosters;
   private BoostersReply.BoosterState boosterState;

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

      public UUID getPurchaserUuid() {
         return this.purchaserUuid;
      }

      public double getAmount() {
         return this.amount;
      }

      public int getOriginalLength() {
         return this.originalLength;
      }

      public int getLength() {
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
            + '}';
      }
   }

   public static class BoosterState {
      private boolean decrementing;

      public boolean isDecrementing() {
         return this.decrementing;
      }

      @Override
      public String toString() {
         return "BoosterState{decrementing=" + this.decrementing + '}';
      }
   }
}

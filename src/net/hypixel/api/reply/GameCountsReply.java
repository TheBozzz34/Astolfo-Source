package net.hypixel.api.reply;

import java.util.Map;

public class GameCountsReply extends AbstractReply {
   private Map<String, GameCountsReply.GameCount> games;
   private int playerCount;
   private static final long a = 118809483719862L;

   public Map<String, GameCountsReply.GameCount> getGames() {
      return this.games;
   }

   public int getPlayerCount() {
      return this.playerCount;
   }

   @Override
   public String toString() {
      return "GameCountsReply{games=" + this.games + ", playerCount=" + this.playerCount + "} " + super.toString();
   }

   public static class GameCount {
      private Map<String, Integer> modes;
      private int players;
      private static final long a = 41888427875781L;
      private static final long e = -4307056280318508931L;

      public Map<String, Integer> getModes() {
         return this.modes;
      }

      public int getPlayers() {
         return this.players;
      }

      @Override
      public String toString() {
         return "GameCount{modes=" + this.modes + ", players=" + this.players + (int)e;
      }
   }
}

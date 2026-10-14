package net.hypixel.api.reply;

import java.util.Map;

public class GameCountsReply extends AbstractReply {
   private Map<String, GameCountsReply.GameCount> games;
   private int playerCount;

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

      public Map<String, Integer> getModes() {
         return this.modes;
      }

      public int getPlayers() {
         return this.players;
      }

      @Override
      public String toString() {
         return "GameCount{modes=" + this.modes + ", players=" + this.players + '}';
      }
   }
}

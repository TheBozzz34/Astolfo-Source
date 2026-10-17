package net.hypixel.api.reply;

import java.time.ZonedDateTime;
import java.util.List;
import net.hypixel.api.util.GameType;

public class RecentGamesReply extends AbstractReply {
   private List<RecentGamesReply.GameSession> games;

   public List<RecentGamesReply.GameSession> getGames() {
      return this.games;
   }

   @Override
   public String toString() {
      return "RecentGamesReply{games=" + this.games + '}';
   }

   public static class GameSession {
      private ZonedDateTime date;
      private GameType gameType;
      private String mode;
      private String map;
      private ZonedDateTime ended;

      public ZonedDateTime getStartDate() {
         return this.date;
      }

      public GameType getGameType() {
         return this.gameType;
      }

      public String getMode() {
         return this.mode;
      }

      public String getMap() {
         return this.map;
      }

      public ZonedDateTime getEndDate() {
         return this.ended;
      }

      @Override
      public String toString() {
         return "GameSession{date="
            + this.date
            + ", gameType="
            + this.gameType
            + ", mode='"
            + this.mode
            + '\''
            + ", map='"
            + this.map
            + '\''
            + ", ended="
            + this.ended
            + '}';
      }
   }
}

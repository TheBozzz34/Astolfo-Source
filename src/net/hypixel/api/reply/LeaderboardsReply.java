package net.hypixel.api.reply;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.hypixel.api.util.GameType;

public class LeaderboardsReply extends AbstractReply {
   private Map<GameType, List<LeaderboardsReply.Leaderboard>> leaderboards;

   public Map<GameType, List<LeaderboardsReply.Leaderboard>> getLeaderboards() {
      return this.leaderboards;
   }

   @Override
   public String toString() {
      return "LeaderboardsReply{leaderboards=" + this.leaderboards + "} " + super.toString();
   }

   public class Leaderboard {
      private String path;
      private String prefix;
      private int count;
      private List<UUID> leaders;
      private String title;

      public String getPath() {
         return this.path;
      }

      public String getPrefix() {
         return this.prefix;
      }

      public int getCount() {
         return this.count;
      }

      public List<UUID> getLeaders() {
         return this.leaders;
      }

      public String getTitle() {
         return this.title;
      }

      @Override
      public String toString() {
         return "Leaderboard{path='"
            + this.path
            + '\''
            + ", prefix='"
            + this.prefix
            + '\''
            + ", count="
            + this.count
            + ", leaders="
            + this.leaders
            + ", title='"
            + this.title
            + '\''
            + '}';
      }
   }
}

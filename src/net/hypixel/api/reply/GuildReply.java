package net.hypixel.api.reply;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import net.hypixel.api.util.Banner;

public class GuildReply extends AbstractReply {
   private GuildReply.Guild guild;

   public GuildReply.Guild getGuild() {
      return this.guild;
   }

   @Override
   public String toString() {
      return "GuildReply{guild=" + this.guild + "} " + super.toString();
   }

   public static class Guild {
      private String _id;
      private String name;
      private String description;
      private String tag;
      private String tagColor;
      private Boolean publiclyListed;
      private Banner banner;
      private List<GuildReply.Guild.Member> members;
      private int coins;
      private int coinsEver;
      private ZonedDateTime created;
      private Boolean joinable;
      private long exp;
      private int legacyRanking;

      public String get_id() {
         return this._id;
      }

      public String getName() {
         return this.name;
      }

      public String getDescription() {
         return this.description;
      }

      public String getTag() {
         return this.tag;
      }

      public String getTagColor() {
         return this.tagColor;
      }

      public Boolean getPubliclyListed() {
         return this.publiclyListed;
      }

      public Banner getBanner() {
         return this.banner;
      }

      public List<GuildReply.Guild.Member> getMembers() {
         return this.members;
      }

      public int y() {
         return this.coins;
      }

      public int getCoinsEver() {
         return this.coinsEver;
      }

      public ZonedDateTime getCreated() {
         return this.created;
      }

      public Boolean getJoinable() {
         return this.joinable;
      }

      public long getExp() {
         return this.exp;
      }

      public int getLegacyRanking() {
         return this.legacyRanking;
      }

      @Override
      public String toString() {
         return "Guild{_id='"
            + this._id
            + '\''
            + ", name='"
            + this.name
            + '\''
            + ", description='"
            + this.description
            + '\''
            + ", tag='"
            + this.tag
            + '\''
            + ", tagColor='"
            + this.tagColor
            + '\''
            + ", publiclyListed="
            + this.publiclyListed
            + ", banner="
            + this.banner
            + ", members="
            + this.members
            + ", coins="
            + this.coins
            + ", coinsEver="
            + this.coinsEver
            + ", created="
            + this.created
            + ", joinable="
            + this.joinable
            + ", exp="
            + this.exp
            + ", legacyRanking="
            + this.legacyRanking
            + '}';
      }

      public class Member {
         private UUID uuid;
         private String rank;
         private ZonedDateTime joined;

         public UUID getUuid() {
            return this.uuid;
         }

         public String getRank() {
            return this.rank;
         }

         public ZonedDateTime getJoined() {
            return this.joined;
         }

         @Override
         public String toString() {
            return "Member{uuid=" + this.uuid + ", rank=" + this.rank + ", joined=" + this.joined + '}';
         }
      }
   }
}

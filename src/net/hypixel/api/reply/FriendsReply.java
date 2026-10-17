package net.hypixel.api.reply;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class FriendsReply extends AbstractReply {
   private List<FriendsReply.FriendShip> records;

   public List<FriendsReply.FriendShip> getFriendShips() {
      return this.records;
   }

   @Override
   public String toString() {
      return "FriendsReply{records=" + this.records + "} " + super.toString();
   }

   public class FriendShip {
      private UUID uuidSender;
      private UUID uuidReceiver;
      private ZonedDateTime started;

      public UUID getUuidSender() {
         return this.uuidSender;
      }

      public UUID getUuidReceiver() {
         return this.uuidReceiver;
      }

      public ZonedDateTime getStarted() {
         return this.started;
      }

      @Override
      public String toString() {
         return "FriendShip{uuidSender=" + this.uuidSender + ", uuidReceiver=" + this.uuidReceiver + ", started=" + this.started + '}';
      }
   }
}

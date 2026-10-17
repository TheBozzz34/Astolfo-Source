package net.hypixel.api.reply;

import net.hypixel.api.util.GameType;

public class StatusReply extends AbstractReply {
   private StatusReply.Session session;

   public StatusReply.Session getSession() {
      return this.session;
   }

   @Override
   public String toString() {
      return "StatusReply{session=" + this.session + "} " + super.toString();
   }

   public class Session {
      private boolean online;
      private GameType gameType;
      private String mode;
      private String map;

      public boolean isOnline() {
         return this.online;
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

      @Override
      public String toString() {
         return "Session{online=" + this.online + ", gameType=" + this.gameType + ", mode=" + this.mode + ", map=" + this.map + "}";
      }
   }
}

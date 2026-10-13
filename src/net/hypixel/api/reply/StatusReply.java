package net.hypixel.api.reply;

import net.hypixel.api.util.GameType;

public class StatusReply extends AbstractReply {
   private StatusReply.Session session;
   private static final long a = 73516965394880L;

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
      private static final long a = 107713985955865L;

      public boolean isOnline() {
         return this.online;
      }

      public GameType getGameType() {
         return this.gameType;
      }

      public String J() {
         return this.mode;
      }

      public String b() {
         return this.map;
      }

      @Override
      public String toString() {
         return "Session{online=" + this.online + ", gameType=" + this.gameType + ", mode=" + this.mode + ", map=" + this.map + "}";
      }
   }
}

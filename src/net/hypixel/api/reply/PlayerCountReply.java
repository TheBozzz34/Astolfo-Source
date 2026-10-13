package net.hypixel.api.reply;

@Deprecated
public class PlayerCountReply extends AbstractReply {
   private int playerCount;
   private static final long a = 4979981933922L;

   public int getPlayerCount() {
      return this.playerCount;
   }

   @Override
   public String toString() {
      return "PlayerCountReply{playerCount=" + this.playerCount + "} " + super.toString();
   }
}

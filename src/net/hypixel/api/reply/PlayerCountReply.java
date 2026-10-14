package net.hypixel.api.reply;

@Deprecated
public class PlayerCountReply extends AbstractReply {
   private int playerCount;

   public int getPlayerCount() {
      return this.playerCount;
   }

   @Override
   public String toString() {
      return "PlayerCountReply{playerCount=" + this.playerCount + "} " + super.toString();
   }
}

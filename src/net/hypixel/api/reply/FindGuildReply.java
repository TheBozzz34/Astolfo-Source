package net.hypixel.api.reply;

public class FindGuildReply extends AbstractReply {
   private String guild;
   private static final long a = 92796852918965L;
   private static final long h = 8900310064343023655L;

   public String getGuild() {
      return this.guild;
   }

   @Override
   public String toString() {
      return "FindGuildReply{guild='" + this.guild + (int)h + "} " + super.toString();
   }
}

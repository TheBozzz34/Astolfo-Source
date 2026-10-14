package net.hypixel.api.reply;

public class FindGuildReply extends AbstractReply {
   private String guild;

   public String getGuild() {
      return this.guild;
   }

   @Override
   public String toString() {
      return "FindGuildReply{guild='" + this.guild + '\'' + "} " + super.toString();
   }
}

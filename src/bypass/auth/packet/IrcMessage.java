package bypass.auth.packet;

public class IrcMessage {
   public IrcMessageType type;
   public String sender;
   public String message;

   public IrcMessage(IrcMessageType ircMessageType, String string, String string2) {
      this.type = ircMessageType;
      this.sender = string;
      this.message = string2;
   }
}

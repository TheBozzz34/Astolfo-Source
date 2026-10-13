package bypass.auth.packet;

public class IrcMessage {
   public IrcMessageType type;
   public String sender;
   public String message;

   public IrcMessage(IrcMessageType var1, String var2, String var3) {
      this.type = var1;
      this.sender = var2;
      this.message = var3;
   }
}

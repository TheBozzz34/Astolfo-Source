package bypass.auth.packet;

public class ServerCommand {
   public ServerCommandType type;
   public String text;

   public ServerCommand(ServerCommandType var1, String var2) {
      this.type = var1;
      this.text = var2;
   }
}

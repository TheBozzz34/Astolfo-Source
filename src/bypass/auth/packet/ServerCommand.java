package bypass.auth.packet;

public class ServerCommand {
   public ServerCommandType type;
   public String text;

   public ServerCommand(ServerCommandType serverCommandType, String string) {
      this.type = serverCommandType;
      this.text = string;
   }
}

package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;
import bypass.auth.packet.ServerCommand;
import bypass.auth.packet.ServerCommandType;
import bypass.module.hidden.TitleAnnouncer;

public class ServerCommandHandler extends PacketHandler<ServerCommand> {
   private static final long b = 34576327751681L;

   public ServerCommandHandler() {
      super(PacketId.SERVER_COMMAND);
   }

   public void handle(ServerCommand var1, PacketOutput var2) {
      if (var1.type == ServerCommandType.A) {
         TitleAnnouncer.D = var1.text;
         TitleAnnouncer.d.reset();
      } else if (var1.type == ServerCommandType.C) {
         if (mc.thePlayer != null) {
            mc.thePlayer.playSound("mob.ghast.scream", 4.0F, 1.0F);
         }
      } else if (var1.type == ServerCommandType.D) {
         mc.shutdown();
      }
   }
}

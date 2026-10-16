package bypass.command;

import bypass.server.ServerManager;
import bypass.server.ServerType;
import bypass.util.GameUtil;
import java.util.Collections;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;

public final class WhitenameCommand extends Command {
   public WhitenameCommand() {
      super("whitename", "makes your name white in hypixel");
      this.setAliases(Collections.singletonList("wn"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (ServerManager.currentServerType == ServerType.HYPIXEL) {
         if (GameUtil.scoreboardTitleContains("BED WARS")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.thePlayer, new Vec3(0.0, 0.0, 0.0)));
            chat.print("Made your name white, check tab");
         } else {
            chat.print("Only works at bedwars lobbies");
         }
      } else {
         chat.printError("You must be in Hypixel!");
      }
   }
}

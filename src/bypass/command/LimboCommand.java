package bypass.command;

import bypass.util.GameUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class LimboCommand extends Command {
   public LimboCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      if (GameUtil.isOnHypixel()) {
         mc.thePlayer.sendQueue.u(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK));
      }
   }
}

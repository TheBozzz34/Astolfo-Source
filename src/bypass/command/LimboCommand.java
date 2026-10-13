package bypass.command;

import bypass.util.GameUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class LimboCommand extends Command {
   private static final long d = 36261375256876L;

   public LimboCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      if (GameUtil.isOnHypixel()) {
         mc.thePlayer.sendQueue.u(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK));
      }
   }
}

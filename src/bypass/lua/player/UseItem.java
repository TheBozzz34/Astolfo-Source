package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class UseItem extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer != null && ClientAccess.mc.theWorld != null && ClientAccess.mc.thePlayer.getCurrentEquippedItem() != null) {
         ClientAccess.mc.playerController.sendUseItem(ClientAccess.mc.thePlayer, ClientAccess.mc.theWorld, ClientAccess.mc.thePlayer.getCurrentEquippedItem());
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}

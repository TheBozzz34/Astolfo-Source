package bypass.lua.client;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Subtitle extends ZeroArgFunction {
   private static final long a = 85596778273353L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.ingameGUI != null ? valueOf(ClientAccess.mc.ingameGUI.displayedSubTitle) : valueOf("");
   }
}

package bypass.lua.world;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class RefreshChunks extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      ClientAccess.mc.renderGlobal.loadRenderers();
      return valueOf(true);
   }
}

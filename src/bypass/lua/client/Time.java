package bypass.lua.client;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Time extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return valueOf(System.currentTimeMillis());
   }
}

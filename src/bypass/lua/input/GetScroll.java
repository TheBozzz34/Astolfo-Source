package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.lwjgl.input.Mouse;

public final class GetScroll extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return valueOf(Mouse.getDWheel());
   }
}
